package org.nymbframework.core.health

/**
 * Represents a health checker - register it with [org.nymbframework.core.environment.Environment.registerHealthChecker]
 * for the application to become aware and perform the health checks when requested.
 */
interface HealthChecker {
    fun check(): HealthState
}
