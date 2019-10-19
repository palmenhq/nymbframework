package org.nymbframework.core.configuration

import java.util.Properties

class ConfigurationReader(
    private val configResourcePath: String,
    private val envFacade: EnvFacade = EnvFacade()
) {
    private val properties = Properties()

    init {
        properties.load(ConfigurationReader::class.java.getResourceAsStream(configResourcePath))
    }

    operator fun get(property: String): String? {
        return envFacade.getenv(property.toUpperCase().replace('.', '_'))
            ?: return properties[property] as String?
    }
    fun getProperty(property: String): String? = properties.getProperty(property)
    fun getBoolean(property: String): Boolean? = get(property)?.toBoolean()
    fun getInt(property: String): Int? = get(property)?.toInt()
    fun getLong(property: String): Long? = get(property)?.toLong()
    fun getFloat(property: String): Float? = get(property)?.toFloat()
    fun getDouble(property: String): Double? = get(property)?.toDouble()
}
