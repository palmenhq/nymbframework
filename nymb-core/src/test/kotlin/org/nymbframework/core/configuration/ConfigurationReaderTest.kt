package org.nymbframework.core.configuration

import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class ConfigurationReaderTest {
    @Test
    fun readsAPropertyFile() {
        val envFacade = mockk<EnvFacade>()
        val configurationReader = ConfigurationReader("/test-config.properties", envFacade)

        every { envFacade.getenv(any()) } returns null
        every { envFacade.getenv("NYMB_TEST_ENV") } returns "an env test"

        assertThat(configurationReader["nymb.test.env"]).isEqualTo("an env test")
        assertThat(configurationReader["nymb.test"]).isEqualTo("a test")
        assertThat(configurationReader["nymb.bool"]).isEqualTo("true")
        assertThat(configurationReader.getBoolean("nymb.bool")).isEqualTo(true)
        assertThat(configurationReader["non.existing.prop"]).isNull()
    }
}
