package org.nymbframework.core.environment

import org.nymbframework.core.Bundle
import org.nymbframework.core.configuration.ConfigurationReader

class Environment(val configurationReader: ConfigurationReader) {
    private val _bundles = mutableListOf<Bundle>()
    val bundles: List<Bundle>
        get() = _bundles
    private val components = mutableMapOf<Class<*>, Any>()

    fun <T : Bundle> registerBundle(bundleClass: Class<T>): Environment {
        _bundles.add(bundleClass.getDeclaredConstructor(Environment::class.java).newInstance(this))
        return this
    }

    fun <T : Bundle> registerBundle(bundle: T): Environment {
        _bundles.add(bundle)
        return this
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Bundle> getBundle(bundleClass: Class<T>): T? = bundles.find { bundle -> bundleClass.isInstance(bundle) } as T?

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
}
