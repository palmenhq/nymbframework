package org.nymbframework.bundles.check

import org.nymbframework.core.health.HealthChecker
import org.nymbframework.core.health.HealthState
import org.slf4j.Logger
import org.slf4j.LoggerFactory.getLogger

class AppHealthChecker(private val healthCheckers: List<HealthChecker>) {
    private val logger: Logger = getLogger(AppHealthChecker::class.java)

    /**
     * Walks through all registered [HealthChecker]'s and reports any failed checks
     */
    fun check(): Boolean {
        logger.info("Performing health checks")
        var healthy = true

        healthCheckers.forEach { checker ->
            val state = checker.check()
            if (state.state == HealthState.State.UNHEALTHY) {
                logger.error("Health check ${checker::class.java} returned UNHEALTHY status: ${state.message}")
                healthy = false
            }
        }

        logger.info("Health checks performed")
        return healthy
    }
}
