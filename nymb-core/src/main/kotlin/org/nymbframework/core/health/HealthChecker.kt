package org.nymbframework.core.health

interface HealthChecker {
    fun check(): HealthState
}
