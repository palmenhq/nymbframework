package org.nymbframework.bundles.database

import com.opentable.db.postgres.junit.EmbeddedPostgresRules
import com.opentable.db.postgres.junit.SingleInstancePostgresRule
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import com.zaxxer.hikari.pool.HikariPool
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test
import org.nymbframework.core.NymbApplication

class DatabaseBundleTest {
    @get:Rule
    val postgres: SingleInstancePostgresRule = EmbeddedPostgresRules.singleInstance()

    @Test
    fun createsDatabaseBundle() {
        val hikariConfig = HikariConfig()
        hikariConfig.dataSource = postgres.embeddedPostgres.postgresDatabase
        hikariConfig.maximumPoolSize = 1

        val app = NymbApplication.create("/test-config.properties")
        app.environment.registerBundle(DatabaseBundle(app.environment, null, hikariConfig))
        val result = app.run("check")
        assertThat(result).isEqualTo(0)
    }

    @Test
    fun createsDatabaseBundlePropertiesFromConfigFile() {
        val app = NymbApplication.create("/test-config.properties")
        val dbBundle = DatabaseBundle(app.environment, "/hikari-config.properties")
        app.environment.registerBundle(dbBundle)
        dbBundle.registerComponents()
        app.environment.getBundle(DatabaseBundle::class.java)
        try {
            app.environment[HikariDataSource::class.java].jdbcUrl
            throw AssertionError("Jdbc connection should fail but succeeded")
        } catch (e: HikariPool.PoolInitializationException) {
            assertThat(e.message).startsWith("Failed to initialize pool: Connection to 127.0.0.1:12345")
        }
    }
}
