package org.nymbframework.bundles.database

import javax.sql.DataSource
import org.nymbframework.core.health.HealthChecker
import org.nymbframework.core.health.HealthState
import org.slf4j.Logger
import org.slf4j.LoggerFactory.getLogger

class DataSourceHealthChecker(private val dataSource: DataSource) : HealthChecker {
    private val logger: Logger = getLogger(DataSourceHealthChecker::class.java)

    override fun check(): HealthState {
        logger.info("Acquiring connection to see if it's alive")
        try {
            val connection = dataSource.connection
            logger.info("Connection successfully acquired, closing it")
            connection.close()
            logger.info("Connection successfully closed")
        } catch (e: Exception) {
            logger.error("Failed to check health of database connection", e)
            return HealthState(state = HealthState.State.UNHEALTHY, message = "${e::class.java}: ${e.message}")
        }
        return HealthState(state = HealthState.State.HEALTHY)
    }
}
