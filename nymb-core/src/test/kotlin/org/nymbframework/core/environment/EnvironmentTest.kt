package org.nymbframework.core.environment

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class EnvironmentTest {
    @Test
    fun registersAndFindsComponent() {
        val environment = Environment()

        environment.registerComponent(MyComponent::class.java, MyComponent("foo"))

        assertThat(environment[MyComponent::class.java].prop).isEqualTo("foo")
    }

    @Test(expected = ComponentNotFoundException::class)
    fun throwsOnUnregisteredComponent() {
        val environment = Environment()
        environment[MyComponent::class.java]
    }

    class MyComponent(
        val prop: String
    )
}
