package org.nymbframework.core.configuration

import io.github.cdimascio.dotenv.Dotenv

/**
 * Wraps [Dotenv] and [System.getenv] in a single non-static method
 */
class EnvFacade {
    private val dotenv: Dotenv

    constructor(dotenv: Dotenv) {
        this.dotenv = dotenv
    }

    constructor() {
        dotenv = Dotenv.configure().ignoreIfMissing().ignoreIfMalformed().load()
    }

    fun getenv(variable: String, defaultValue: String): String {
        val variableValue = System.getenv(variable)
        return variableValue ?: dotenv.get(variable, defaultValue)
    }

    fun getenv(variable: String): String? {
        val variableValue = System.getenv(variable)
        return variableValue ?: dotenv[variable]
    }
}
