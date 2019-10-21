package org.nymbframework.core.environment

import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.nymbframework.core.Bundle
import org.nymbframework.core.configuration.ConstantAppModeTestConfigurationReader

class EnvironmentTest {
    @Test
    fun registersAndFindsComponent() {
        val environment = Environment(ConstantAppModeTestConfigurationReader())

        environment.registerComponent(MyComponent::class.java, MyComponent("foo"))

        assertThat(environment[MyComponent::class.java].prop).isEqualTo("foo")
    }

    @Test(expected = ComponentNotFoundException::class)
    fun throwsOnUnregisteredComponent() {
        val environment = Environment(mockk())
        environment[MyComponent::class.java]
    }

    @Test
    fun registersAndInstantiatesBundle() {
        val environment = Environment(ConstantAppModeTestConfigurationReader())
        environment.registerBundle(MyBundle::class.java)
        assertThat(environment.getBundle(MyBundle::class.java)?.getEnvironment_()).isEqualTo(environment)
    }

    @Test
    fun registersBundle() {
        val environment = Environment(ConstantAppModeTestConfigurationReader())
        environment.registerBundle(MyBundle(environment))
        assertThat(environment.getBundle(MyBundle::class.java)?.getEnvironment_()).isEqualTo(environment)
    }

    class MyComponent(
        val prop: String
    )

    class MyBundle(environment: Environment) : Bundle(environment) {
        fun getEnvironment_(): Environment = environment
    }
}
