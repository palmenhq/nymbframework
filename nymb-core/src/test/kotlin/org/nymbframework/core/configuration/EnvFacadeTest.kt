package org.nymbframework.core.configuration

import io.github.cdimascio.dotenv.Dotenv
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class EnvFacadeTest {
    @Test
    fun readsDotenv() {
        val dotenv = mockk<Dotenv>()
        val envFacade = EnvFacade(dotenv)

        every { dotenv["NON_EXISTING_ENV_VAR"] } returns null
        every { dotenv.get("NON_EXISTING_ENV_VAR", "default") } returns "default"
        every { dotenv["EXISTING_ENV_VAR"] } returns "foo"

        assertThat(envFacade.getenv("NON_EXISTING_ENV_VAR")).isNull()
        assertThat(envFacade.getenv("EXISTING_ENV_VAR")).isEqualTo("foo")
        assertThat(envFacade.getenv("NON_EXISTING_ENV_VAR", "default")).isEqualTo("default")
    }
}
