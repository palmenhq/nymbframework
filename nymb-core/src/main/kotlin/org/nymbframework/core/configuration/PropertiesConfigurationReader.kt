package org.nymbframework.core.configuration

import java.util.Properties

/**
 * A [ConfigurationReader] that reads from [Properties] (the default implementation however defaults to
 * [EnvVarAwareProperties] with an [EnvFacade].
 */
class PropertiesConfigurationReader(
    /**
     * File to load with [Class.getResourceAsStream] on construction
     */
    configResourcePath: String,
    /**
     * The properties implementation to use. [configResourcePath] is loaded from [Class.getResourceAsStream] on
     * construction
     */
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
