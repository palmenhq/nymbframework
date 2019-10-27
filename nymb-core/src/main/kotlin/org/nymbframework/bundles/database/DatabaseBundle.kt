package org.nymbframework.bundles.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.javalin.Javalin
import javax.sql.DataSource
import liquibase.Liquibase
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.ClassLoaderResourceAccessor
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.jdbi.v3.sqlobject.SqlObjectPlugin
import org.jdbi.v3.sqlobject.kotlin.KotlinSqlObjectPlugin
import org.nymbframework.bundles.database.commands.RootDatabaseCommand
import org.nymbframework.core.Bundle
import org.nymbframework.core.commandline.NymbCommand
import org.nymbframework.core.configuration.EnvFacade
import org.nymbframework.core.configuration.EnvVarAwareProperties
import org.nymbframework.core.environment.Environment
import org.nymbframework.core.environment.configure

class DatabaseBundle @JvmOverloads constructor(
    environment: Environment,
    private val hikariConfigFile: String? = null,
    private val hikariConfig: HikariConfig? = null
) : Bundle(environment) {
    override val commands: List<NymbCommand> = listOf(RootDatabaseCommand(environment))

    override fun registerComponents() {
        environment.registerLazyComponent(HikariConfig::class.java, this::getFullHikariConfig)

        environment.registerLazyComponent(HikariDataSource::class.java) { env ->
            HikariDataSource(env[HikariConfig::class.java])
        }
        environment.registerComponentAlias(DataSource::class.java, HikariDataSource::class.java)
        environment.registerLazyComponent(Jdbi::class.java) { env ->
            Jdbi.create(env[DataSource::class.java])
                .installPlugin(SqlObjectPlugin())
                .installPlugin(KotlinPlugin())
                .installPlugin(KotlinSqlObjectPlugin())
        }
        environment.registerLazyComponent(Liquibase::class.java) { env ->
            Liquibase(
                env.configurationReader["liquibase.changelog"] ?: "db/changelog.yml",
                ClassLoaderResourceAccessor(DatabaseBundle::class.java.classLoader),
                JdbcConnection(env[DataSource::class.java].connection)
            )
        }
        environment.configure(Javalin::class.java) { javalin ->
            javalin.before { ctx ->
                ctx.register(
                    Handle::class.java,
                    environment[Jdbi::class.java].open().begin()
                )
            }
            javalin.after { ctx ->
                val handle = ctx.use(Handle::class.java)

                if (handle.isClosed) {
                    return@after
                }

                if (handle.isInTransaction) {
                    if (ctx.status() >= 500) {
                        handle.rollback()
                    } else {
                        handle.commit()
                    }
                }
                handle.close()
            }
        }
    }

    private fun getFullHikariConfig(env: Environment): HikariConfig {
        if (hikariConfig != null) {
            return hikariConfig
        }

        if (hikariConfigFile != null) {
            val envVarAwareProperties = EnvVarAwareProperties(environment[EnvFacade::class.java])
            envVarAwareProperties.load(DatabaseBundle::class.java.getResourceAsStream(hikariConfigFile))
            return HikariConfig(envVarAwareProperties)
        }

        throw IllegalStateException("No suitable configuration found for Hikari")
    }

    override fun preRun() {
        environment.configure(HikariDataSource::class.java) { ds ->
            environment.registerHealthChecker(DataSourceHealthChecker(ds))
        }
    }
}
