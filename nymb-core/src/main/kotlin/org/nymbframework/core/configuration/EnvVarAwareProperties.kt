package org.nymbframework.core.configuration

import java.util.Properties

/**
 * Extends [Properties] to allow overriding environment variables matching the property names, except dots are
 * replaced with underscores and casing is transformed to uppercase - i.e. `my.var` as a property will be searched
 * for as an environment variable `MY_VAR`. The precedence is:
 *
 * 1. Environment variables
 * 1. Properties
 * 1. Null or default value
 */
class EnvVarAwareProperties(private val envFacade: EnvFacade) : Properties() {
    @Synchronized
    override fun put(key: Any, value: Any): Any? {
        val realValue = if (key is String) {
            envFacade.getenv(transformPropertyNameToEnvVar(key), value as String)
        } else {
            key
        }

        return super.put(key, realValue)
    }

    @Synchronized
    override fun putAll(from: Map<*, *>) {
        val realFrom: Map<*, *> = from.map { entry ->
            if (entry.key is String) {
                entry.key to envFacade.getenv(
                    transformPropertyNameToEnvVar(entry.key as String),
                    entry.value as String
                )
            } else {
                entry.key to entry.value
            }
        }
            .toMap()

        return super.putAll(from)
    }

    /**
     * Get a property as environment variable, property or null
     */
    override fun get(key: Any?): Any? {
        if (key !is String) {
            return super.get(key)
        }

        return envFacade.getenv(transformPropertyNameToEnvVar(key)) ?: super.get(key)
    }

    /**
     * Get a property as environment variable, property or null
     */
    override fun getProperty(key: String): String? {
        return envFacade.getenv(transformPropertyNameToEnvVar(key)) ?: super.getProperty(key)
    }

    /**
     * Get a property as environment variable, property or [defaultValue]
     */
    override fun getProperty(key: String, defaultValue: String): String? {
        return envFacade.getenv(transformPropertyNameToEnvVar(key)) ?: super.getProperty(key, defaultValue)
    }

    private fun transformPropertyNameToEnvVar(key: String) = key.toUpperCase().replace('.', '_')
}
