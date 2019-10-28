package org.nymbframework.core.configuration

/**
 * Reads configuration for different data structures.
 *
 * @see EnvVarAwareProperties for example
 */
interface ConfigurationReader {
    operator fun get(property: String): String?
    fun getBoolean(property: String): Boolean? = get(property)?.toBoolean()
    fun getInt(property: String): Int? = get(property)?.toInt()
    fun getLong(property: String): Long? = get(property)?.toLong()
    fun getFloat(property: String): Float? = get(property)?.toFloat()
    fun getDouble(property: String): Double? = get(property)?.toDouble()
}
