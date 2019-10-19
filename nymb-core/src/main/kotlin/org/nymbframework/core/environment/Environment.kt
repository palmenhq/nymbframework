package org.nymbframework.core.environment

class Environment {
    private val components = mutableMapOf<Class<*>, Any>()

    fun <T : Any> registerComponent(componentClass: Class<T>, instance: T) {
        if (!componentClass.isInstance(instance)) {
            throw IllegalComponentException("Expected instance to be instance of the registered type $componentClass, but was really ${instance::class}")
        }
        components[componentClass] = instance as Any
    }

    operator fun <T> get(component: Class<T>): T = if (components.contains(component)) {
        components[component] as T
    } else {
        throw ComponentNotFoundException("Tried to locate component $component but it was not present")
    }
}
