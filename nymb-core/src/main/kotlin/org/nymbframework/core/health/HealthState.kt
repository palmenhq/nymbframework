package org.nymbframework.core.health

/**
 * Represents the state of a particular health check.
 */
class HealthState(val state: State, val message: String? = null) {
    enum class State {
        HEALTHY,
        UNHEALTHY,
    }
}
