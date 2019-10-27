package org.nymbframework.bundles.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import javax.sql.DataSource
import org.jdbi.v3.core.Jdbi
import org.nymbframework.core.Bundle
import org.nymbframework.core.configuration.EnvFacade
import org.nymbframework.core.configuration.EnvVarAwareProperties
import org.nymbframework.core.environment.Environment
import org.nymbframework.core.environment.configure

class DatabaseBundle @JvmOverloads constructor(
    environment: Environment,
    private val hikariConfigFile: String? = null,
    private val hikariConfig: HikariConfig? = null
) : Bundle(environment) {
    override fun registerComponents() {
        environment.registerLazyComponent(HikariConfig::class.java, this::getFullHikariConfig)

        environment.registerLazyComponent(HikariDataSource::class.java) { env ->
            HikariDataSource(env[HikariConfig::class.java])
        }
        environment.registerComponentAlias(HikariDataSource::class.java, DataSource::class.java)
        environment.registerLazyComponent(Jdbi::class.java) { env ->
            Jdbi.create(env[DataSource::class.java])
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
