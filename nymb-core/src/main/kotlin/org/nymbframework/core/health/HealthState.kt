package org.nymbframework.core.health

class HealthState(val state: State, val message: String? = null) {
    enum class State {
        HEALTHY,
        UNHEALTHY,
    }
}
