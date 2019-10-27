package org.nymbframework.core

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.nymbframework.bundles.check.CheckBundle
import org.nymbframework.core.configuration.PropertiesConfigurationReader
import org.nymbframework.core.environment.Environment

class NymbApplicationTest {
    @Test
    fun executesCheck() {
        val environment = Environment(PropertiesConfigurationReader("/test-config.properties"))
        environment.registerBundle(CheckBundle::class.java)

        val app = NymbApplication(environment)

        val result = app.run("check")

        assertThat(result).isEqualTo(0)
    }
}
