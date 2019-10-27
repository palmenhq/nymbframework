package org.nymbframework.core.configuration

import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class EnvVarAwarePropertiesTest {
    @Test
    fun readsProperties() {
        val envFacade = mockk<EnvFacade>()

        every { envFacade.getenv(any(), any()) } answers { arg(1) }
        every { envFacade.getenv("NYMB_TEST") } answers { "from env" }
        every { envFacade.getenv("NYMB_BOOL") } answers { null }
        every { envFacade.getenv("NON_EXISTING_PROP") } answers { null }

        val props = EnvVarAwareProperties(envFacade)
        props.load(EnvVarAwareProperties::class.java.getResourceAsStream("/test-config.properties"))
        assertThat(props.getProperty("nymb.test")).isEqualTo("from env")
        assertThat(props.getProperty("nymb.bool")).isEqualTo("true")
        assertThat(props.getProperty("non.existing.prop")).isNull()
        assertThat(props.getProperty("non.existing.prop", "default")).isEqualTo("default")
    }
}
