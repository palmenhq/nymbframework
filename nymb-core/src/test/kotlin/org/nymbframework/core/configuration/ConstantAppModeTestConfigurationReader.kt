package org.nymbframework.core.configuration

class ConstantAppModeTestConfigurationReader : ConfigurationReader {
    override fun get(property: String): String? {
        if (property == "app.mode") {
            return "test"
        }

        return null
    }
}
