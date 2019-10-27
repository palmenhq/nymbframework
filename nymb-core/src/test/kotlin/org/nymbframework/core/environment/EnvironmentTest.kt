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
        assertThat(environment[Checker::class.java].state).isTrue()
    }

    @Test
    fun registersBundle() {
        val environment = Environment(ConstantAppModeTestConfigurationReader())
        environment.registerBundle(MyBundle(environment))
        assertThat(environment.getBundle(MyBundle::class.java)?.getEnvironment_()).isEqualTo(environment)
        assertThat(environment[Checker::class.java].state).isTrue()
    }

    @Test
    fun configures() {
        val environment = Environment(ConstantAppModeTestConfigurationReader())
        environment.registerComponent(Checker::class.java, Checker())
        environment.configure(Checker::class.java) { checker ->
            checker.state = false
        }
        assertThat(environment[Checker::class.java].state).isFalse()
    }

    @Test
    fun registersLazyComponents() {
        val environment = Environment(ConstantAppModeTestConfigurationReader())
        val callCounter = Counter()
        environment.registerLazyComponent(Counter::class.java) {
            callCounter.count += 1
            callCounter
        }
        assertThat(environment[Counter::class.java].count).isEqualTo(1)
        assertThat(environment[Counter::class.java].count).isEqualTo(1)
    }

    @Test
    fun registersLazyComponentsWithAliases() {
        val environment = Environment(ConstantAppModeTestConfigurationReader())
        val callCounter = Counter2()
        environment.registerLazyComponent(Counter2::class.java) {
            callCounter.count += 1
            callCounter
        }
        environment.registerComponentAlias(Counter::class.java, Counter2::class.java)
        assertThat(environment[Counter::class.java].count).isEqualTo(1)
        assertThat(environment[Counter::class.java].count).isEqualTo(1)
        assertThat(environment[Counter2::class.java].count).isEqualTo(1)
    }

    class MyComponent(
        val prop: String
    )

    class MyBundle(environment: Environment) : Bundle(environment) {
        override fun registerComponents() {
            environment.registerComponent(Checker::class.java, Checker())
        }

        fun getEnvironment_(): Environment = environment
    }

    class Checker {
        var state = true
    }

    open class Counter {
        var count = 0
    }
    class Counter2 : Counter()
}
