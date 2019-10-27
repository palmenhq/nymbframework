package org.nymbframework.core.environment

import java.util.function.Consumer
import org.nymbframework.core.Bundle
import org.nymbframework.core.configuration.ConfigurationReader
import org.nymbframework.core.environment.health.HealthChecker

class Environment(val configurationReader: ConfigurationReader) {
    private val _bundles = mutableListOf<Bundle>()
    val bundles: List<Bundle>
        get() = _bundles
    private val components = mutableMapOf<Class<*>, Any>()
    private val healthCheckers: MutableList<HealthChecker> = mutableListOf()

    fun <T : Bundle> registerBundle(bundleClass: Class<T>): Environment {
        registerBundle(bundleClass.getDeclaredConstructor(Environment::class.java).newInstance(this))
        return this
    }

    fun <T : Bundle> registerBundle(bundle: T): Environment {
        _bundles.add(bundle)
        bundle.registerComponents()
        return this
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Bundle> getBundle(bundleClass: Class<T>): T? = bundles.find { bundle -> bundleClass.isInstance(bundle) } as T?

    fun <T> configure(component: Class<T>, configure: Consumer<T>): Environment {
        configure.accept(get(component))
        return this
    }

    fun <T : Any> registerComponent(componentClass: Class<T>, instance: T) {
        if (!componentClass.isInstance(instance)) {
            throw IllegalComponentException("Expected instance to be instance of the registered type $componentClass, but was really ${instance::class}")
        }
        components[componentClass] = instance as Any
    }

    @Suppress("UNCHECKED_CAST")
    operator fun <T> get(component: Class<T>): T = if (components.contains(component)) {
        components[component] as T
    } else {
        throw ComponentNotFoundException("Tried to locate component $component but it was not present")
    }

    fun registerHealthChecker(checker: HealthChecker) {
        healthCheckers.add(checker)
    }

    fun getHealthCheckers(): List<HealthChecker> = healthCheckers
}

fun <T> Environment.configure(component: Class<T>, configure: (T) -> Unit): Environment {
    configure(component, Consumer { c -> configure(c) })

    return this
}
