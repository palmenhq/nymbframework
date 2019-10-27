package org.nymbframework.bundles.server

import io.javalin.Javalin
import org.nymbframework.core.commandline.NymbCommand
import org.nymbframework.core.environment.Environment
import picocli.CommandLine

@CommandLine.Command(name = "server")
class ServerCommand(private val environment: Environment) : NymbCommand {
    @CommandLine.Option(names = ["--port", "-p"])
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
