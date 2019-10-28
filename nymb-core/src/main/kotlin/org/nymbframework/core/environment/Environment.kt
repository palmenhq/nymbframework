package org.nymbframework.core.environment

import java.util.function.Consumer
import org.nymbframework.core.Bundle
import org.nymbframework.core.commandline.NymbCommand
import org.nymbframework.core.configuration.ConfigurationReader
import org.nymbframework.core.configuration.EnvFacade
import org.nymbframework.core.health.HealthChecker

/**
 * Heart and core of a Nymb application. Gathers components used throughout the framework and application to make it
 * fairly modular. It works as a very dumb dependency injection container (component = class instance by its [Class])
 * but also comes with configuration and collection of registered bundles and commands.
 */
class Environment(
    /**
     * The app's configuration reader
     */
    val configurationReader: ConfigurationReader,
    /**
     * [EnvFacade] to inject into the environment
     */
    envFacade: EnvFacade = EnvFacade()
) {
    private val _commands = mutableListOf<NymbCommand>()
    /**
     * Gets all [NymbCommand]s registered at the current point in time
     */
    val commands: List<NymbCommand>
        get() = _commands
    private val _bundles = mutableListOf<Bundle>()
    /**
     * Gets all [Bundle]s registered at the current point in time
     */
    val bundles: List<Bundle>
        get() = _bundles
    private val components = mutableMapOf<Class<*>, Any>()
    private val componentAliases = mutableMapOf<Class<*>, Class<*>>()
    private val lazyComponents = mutableMapOf<Class<*>, (Environment) -> Any>()
    private val _healthCheckers: MutableList<HealthChecker> = mutableListOf()
    /**
     * List of registered health checkers that'll be used by for example the
     * [org.nymbframework.bundles.check.CheckCommand]
     */
    val healthCheckers: List<HealthChecker>
        get() = _healthCheckers

    init {
        registerComponent(envFacade)
    }

    /**
     * Register an executable [command][NymbCommand]. [org.nymbframework.core.NymbApplication] will later pick this up
     * and apply them as sub commands for the [org.nymbframework.core.commandline.RootCommand].
     */
    fun <T : NymbCommand> registerCommand(command: NymbCommand): Environment {
        _commands.add(command)
        return this
    }

    /**
     * Registers a [Bundle] instance and instantly calls [Bundle.registerComponents] after its added.
     */
    fun <T : Bundle> registerBundle(bundle: T): Environment {
        _bundles.add(bundle)
        bundle.registerComponents()
        return this
    }

    /**
     * Finds a specific registered [Bundle] instance.
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : Bundle> getBundle(bundleClass: Class<T>): T? = bundles.find { bundle -> bundleClass.isInstance(bundle) } as T?

    fun <T : Any> configure(component: Class<T>, configure: Consumer<T>): Environment {
        configure.accept(get(component))
        return this
    }

    /**
     * Registers an injectable component by its class (or a superclass). Note that only the given [componentClass] will
     * be possible to [get]. In case you need multiple entries for the same instance (i.e. both an interface and the
     * implementation) use an [alias][registerComponentAlias].
     */
    fun <T : Any> registerComponent(componentClass: Class<T>, instance: T): Environment {
        if (!componentClass.isInstance(instance)) {
            throw IllegalComponentException("Expected instance to be instance of the registered type $componentClass, but was really ${instance::class}")
        }
        components[componentClass] = instance
        return this
    }

    /**
     * Convenience method to register a component instance by its class. Any component instance passed to here can be
     * retrieved by its exact class.
     */
    fun <T : Any> registerComponent(instance: T): Environment {
        components[instance::class.java] = instance
        return this
    }

    /**
     * Registers a component that will be fetched first when it's [retrieved][get].
     */
    fun <T : Any> registerLazyComponent(componentClass: Class<T>, getter: (Environment) -> T): Environment {
        lazyComponents[componentClass] = getter
        return this
    }

    /**
     * Adds an [alias] for a component, which means the [target] instance will be referencable by the [alias].
     * For example:
     * ```kt
     * interface MyInterface
     * class MyClass : MyInterface
     *
     * env.registerComponent(MyClass())
     * env.get(MyInterface::class.java) // No - throws [ComponentNotFoundException]
     *
     * env.registerComponentAlias(MyInterface::class.java, MyClass::class.java)
     * env.get(MyInterface::class.java) // Returns the MyClass instance
     * ```
     */
    fun <TAlias : Any, TInstance : TAlias> registerComponentAlias(alias: Class<TAlias>, target: Class<TInstance>): Environment {
        componentAliases[alias] = target
        return this
    }

    /**
     * Check whether a component or an alias is resolvable and is registered to the environment.
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : Any> has(component: Class<T>): Boolean {
        val resolvedComponent = resolveComponent(component)
        if (components.containsKey(resolvedComponent)) return true
        if (lazyComponents.containsKey(resolvedComponent)) return true

        return false
    }

    /**
     * Gets an instance of the component.
     */
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

    /**
     * Registers a [HealthChecker]
     * @see healthCheckers
     */
    fun registerHealthChecker(checker: HealthChecker) {
        _healthCheckers.add(checker)
    }
}

/**
 * Kotlin extension to get some nicer-looking callbacks for [Environment.configure].
 * @see [Environment.configure]
 */
fun <T : Any> Environment.configure(component: Class<T>, configure: (T) -> Unit): Environment {
    configure(component, Consumer { c -> configure(c) })

    return this
}
