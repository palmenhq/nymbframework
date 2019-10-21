package org.nymbframework.core.configuration

import java.util.Properties

class PropertiesAndEnvConfigurationReader(
    private val configResourcePath: String,
    private val envFacade: EnvFacade = EnvFacade(),
    private val properties: Properties = Properties()
) : ConfigurationReader {
    init {
        properties.load(PropertiesAndEnvConfigurationReader::class.java.getResourceAsStream(configResourcePath))
        if (get("app.mode") == null) {
            throw MissingAppModeConfigurationException("Missing required property app.mode (or environment variable APP_MODE)")
        }
    }

    override operator fun get(property: String): String? {
        return envFacade.getenv(property.toUpperCase().replace('.', '_'))
            ?: return properties[property] as String?
    }

    fun getProperty(property: String): String? = properties.getProperty(property)
}
