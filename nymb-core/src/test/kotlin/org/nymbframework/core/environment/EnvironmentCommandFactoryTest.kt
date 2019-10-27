package org.nymbframework.core.environment

import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class EnvironmentCommandFactoryTest {
    @Test
    fun makesACommandWithNoArgs() {
        val environmentCommandFactory = EnvironmentCommandFactory(mockk())
        assertThat(environmentCommandFactory.create(NoArgsCommand::class.java)).isInstanceOf(NoArgsCommand::class.java)
    }

    @Test
    fun makesACommandWithEnvironmentConstructor() {
        val environment = mockk<Environment>()
        val environmentCommandFactory = EnvironmentCommandFactory(environment)
        val instance = environmentCommandFactory.create(EnvCommand::class.java)
        assertThat(instance).isInstanceOf(EnvCommand::class.java)
        assertThat(instance.environment).isEqualTo(environment)
    }

    class NoArgsCommand
    class EnvCommand(val environment: Environment)
}
