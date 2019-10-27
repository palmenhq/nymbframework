package org.nymbframework.core

import org.nymbframework.bundles.check.CheckBundle
import org.nymbframework.bundles.server.ServerBundle
import org.nymbframework.core.commandline.RootCommand
import org.nymbframework.core.configuration.PropertiesAndEnvConfigurationReader
import org.nymbframework.core.environment.Environment
import picocli.CommandLine

class NymbApplication(
    val environment: Environment
) {
    fun run(vararg args: String): Int {
        val rootCommand = RootCommand()
        val commandLine = CommandLine(rootCommand)
        rootCommand.commandLine = commandLine
        environment.bundles
            .flatMap { bundle -> bundle.commands }
            .forEach { command -> commandLine.addSubcommand(command) }

        return commandLine.execute(*args)
    }

    companion object {
        @JvmStatic
        fun create(filePath: String): NymbApplication {
            val environment = Environment(PropertiesAndEnvConfigurationReader(filePath))
            environment.registerBundle(CheckBundle::class.java)
            environment.registerBundle(ServerBundle::class.java)
            return NymbApplication(environment)
        }
    }
}
