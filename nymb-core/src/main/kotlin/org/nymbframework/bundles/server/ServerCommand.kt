package org.nymbframework.bundles.server

import io.javalin.Javalin
import org.nymbframework.core.commandline.NymbCommand
import org.nymbframework.core.environment.Environment
import picocli.CommandLine

/**
 * Run the web server
 */
@CommandLine.Command(name = "server")
class ServerCommand(private val environment: Environment) : NymbCommand {
    @CommandLine.Option(
        names = ["--port", "-p"],
        description = ["Override the configured port"]
    )
    var port: Int? = null

    override fun call(): Int {
        val javalin = environment[Javalin::class.java]

        val realPort = port ?: environment.configurationReader.getInt("server.port")
        if (realPort != null) {
            javalin.start(realPort)
        } else {
            javalin.start()
        }

        return 0
    }
}
