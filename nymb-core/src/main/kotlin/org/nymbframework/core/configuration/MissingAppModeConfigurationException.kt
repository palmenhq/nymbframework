package org.nymbframework.core.configuration

/**
 * Indicates the required configuration property `app.mode` is missing
 */
class MissingAppModeConfigurationException(message: String) : RuntimeException(message)
