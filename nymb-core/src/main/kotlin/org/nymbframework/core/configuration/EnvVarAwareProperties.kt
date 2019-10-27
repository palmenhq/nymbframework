package org.nymbframework.core.configuration

import java.util.Properties

class EnvVarAwareProperties(val envFacade: EnvFacade) : Properties() {
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

    override fun get(key: Any?): Any? {
        if (key !is String) {
            return super.get(key)
        }

        return envFacade.getenv(transformPropertyNameToEnvVar(key)) ?: super.get(key)
    }

    override fun getProperty(key: String): String? {
        return envFacade.getenv(transformPropertyNameToEnvVar(key)) ?: super.getProperty(key)
    }

    override fun getProperty(key: String, defaultValue: String): String? {
        return envFacade.getenv(transformPropertyNameToEnvVar(key)) ?: super.getProperty(key, defaultValue)
    }

    private fun transformPropertyNameToEnvVar(key: String) = key.toUpperCase().replace('.', '_')
}
