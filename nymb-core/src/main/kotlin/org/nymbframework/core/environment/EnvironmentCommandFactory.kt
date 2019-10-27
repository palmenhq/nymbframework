package org.nymbframework.core.environment

import picocli.CommandLine

/**
 * Instantiates [org.nymbframework.core.commandline.NymbCommand] based on [Environment] or no-arg constructors.
 * Constructors should take either 0 arguments or 1 argument of type [Environment].
 */
class EnvironmentCommandFactory(private val environment: Environment) : CommandLine.IFactory {
    @Suppress("UNCHECKED_CAST")
    override fun <K : Any> create(cls: Class<K>): K {
        val envConstructor = cls.declaredConstructors.firstOrNull { constructor ->
            constructor.parameterCount == 1 && constructor.parameterTypes[0] == Environment::class.java
        }

        if (envConstructor != null) {
            return envConstructor.newInstance(environment) as K
        }

        val noArgConstructor = cls.declaredConstructors.firstOrNull { constructor ->
            constructor.parameterCount == 0
        }

        if (noArgConstructor != null) {
            return noArgConstructor.newInstance() as K
        }

        throw IllegalArgumentException("No suitable constructor detected for class $cls (checked for environment and no-arg constructors)")
    }
}
