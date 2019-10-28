package org.nymbframework.bundles.server

import io.javalin.Javalin
import org.nymbframework.core.Bundle
import org.nymbframework.core.commandline.NymbCommand
import org.nymbframework.core.environment.Environment

/**
 * The bundle powering [Javalin](https://javalin.io) 's web server
 *
 * Valid configuration properties:
 *
 * - `server.port` - lets you specify the standard port to run the web server on. If not specified it'll default
 *   to [Javalin]'s default port
 */
class ServerBundle(environment: Environment) : Bundle(environment) {
    override val commands: List<NymbCommand> = listOf(ServerCommand(environment))

    override fun registerComponents() {
        val javalin = Javalin.create { config ->
            config.logIfServerNotStarted = false
        }
        environment.registerComponent(Javalin::class.java, javalin)
    }
}
