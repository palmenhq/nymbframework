package org.nymbframework.core.environment.health

interface HealthChecker {
    fun check(): HealthState
}
