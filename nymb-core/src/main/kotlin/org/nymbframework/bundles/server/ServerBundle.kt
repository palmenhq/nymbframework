package org.nymbframework.bundles.server

import io.javalin.Javalin
import org.nymbframework.core.Bundle
import org.nymbframework.core.commandline.NymbCommand
import org.nymbframework.core.environment.Environment

class ServerBundle(environment: Environment) : Bundle(environment) {
    override val commands: List<NymbCommand> = listOf(ServerCommand(environment))

    override fun registerComponents() {
        val javalin = Javalin.create { config ->
            config.logIfServerNotStarted = false
        }
        environment.registerComponent(Javalin::class.java, javalin)
    }
}
