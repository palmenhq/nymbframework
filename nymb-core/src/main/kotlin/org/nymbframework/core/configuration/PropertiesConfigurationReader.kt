package org.nymbframework.core.configuration

import java.util.Properties

class PropertiesConfigurationReader(
    configResourcePath: String,
    val properties: Properties = EnvVarAwareProperties(EnvFacade())
) : ConfigurationReader {
    init {
        properties.load(PropertiesConfigurationReader::class.java.getResourceAsStream(configResourcePath))
        if (get("app.mode") == null) {
            throw MissingAppModeConfigurationException("Missing required property app.mode (or environment variable APP_MODE)")
        }
    }

    override operator fun get(property: String): String? {
        return properties[property] as String?
    }

    fun getProperty(property: String): String? = properties.getProperty(property)
}
