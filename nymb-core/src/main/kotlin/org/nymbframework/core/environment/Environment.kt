package org.nymbframework.core.environment

import java.util.function.Consumer
import org.nymbframework.core.Bundle
import org.nymbframework.core.configuration.ConfigurationReader
import org.nymbframework.core.configuration.EnvFacade
import org.nymbframework.core.health.HealthChecker

class Environment(val configurationReader: ConfigurationReader, envFacade: EnvFacade = EnvFacade()) {
    private val _bundles = mutableListOf<Bundle>()
    val bundles: List<Bundle>
        get() = _bundles
    private val components = mutableMapOf<Class<*>, Any>()
    private val componentAliases = mutableMapOf<Class<*>, Class<*>>()
    private val lazyComponents = mutableMapOf<Class<*>, (Environment) -> Any>()
    private val healthCheckers: MutableList<HealthChecker> = mutableListOf()

    init {
        registerComponent(envFacade)
    }

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

    fun <T : Any> configure(component: Class<T>, configure: Consumer<T>): Environment {
        configure.accept(get(component))
        return this
    }

    fun <T : Any> registerComponent(componentClass: Class<T>, instance: T) {
        if (!componentClass.isInstance(instance)) {
            throw IllegalComponentException("Expected instance to be instance of the registered type $componentClass, but was really ${instance::class}")
        }
        components[componentClass] = instance
    }

    fun <T : Any> registerComponent(instance: T) {
        components[instance::class.java] = instance
    }

    fun <T : Any> registerLazyComponent(componentClass: Class<T>, getter: (Environment) -> T) {
        lazyComponents[componentClass] = getter
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> has(component: Class<T>): Boolean {
        val resolvedComponent = resolveComponent(component)
        if (components.containsKey(resolvedComponent)) return true
        if (lazyComponents.containsKey(resolvedComponent)) return true

        return false
    }

    @Suppress("UNCHECKED_CAST")
    operator fun <T : Any> get(component: Class<T>): T {
        val resolvedComponent = resolveComponent(component)
        return if (has(component as Class<Any>)) {
            if (lazyComponents.containsKey(resolvedComponent)) {
                components[resolvedComponent] = (lazyComponents[resolvedComponent]!!)(this)
                lazyComponents.remove(resolvedComponent)
            }

            components[resolvedComponent] as T
        } else {
            throw ComponentNotFoundException("Tried to locate component $component (resolved as $resolvedComponent) but it was not present")
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T : Any> resolveComponent(component: Class<T>): Class<T> {
        if (components.containsKey(component) || lazyComponents.containsKey(component)) {
            return component
        }
        if (componentAliases.containsKey(component)) {
            return resolveComponent(componentAliases[component] as Class<T>)
        }
        return component
    }

    fun registerHealthChecker(checker: HealthChecker) {
        healthCheckers.add(checker)
    }

    fun getHealthCheckers(): List<HealthChecker> = healthCheckers
    fun registerComponentAlias(alias: Class<*>, target: Class<*>) {
        componentAliases[alias] = target
    }
}

fun <T : Any> Environment.configure(component: Class<T>, configure: (T) -> Unit): Environment {
    configure(component, Consumer { c -> configure(c) })

    return this
}
