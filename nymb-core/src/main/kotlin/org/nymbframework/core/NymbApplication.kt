package org.nymbframework.core

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import org.nymbframework.bundles.check.CheckBundle
import org.nymbframework.bundles.server.ServerBundle
import org.nymbframework.core.commandline.RootCommand
import org.nymbframework.core.configuration.PropertiesConfigurationReader
import org.nymbframework.core.environment.Environment
import org.slf4j.LoggerFactory
import picocli.CommandLine

class NymbApplication(
    val environment: Environment
) {
    fun run(vararg args: String): Int {
        val logLevel = environment.configurationReader["log.level"]
        if (logLevel != null) {
            (LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as Logger).level = Level.valueOf(logLevel)
        }

        val rootCommand = RootCommand()
        val commandLine = CommandLine(rootCommand)
        rootCommand.commandLine = commandLine
        environment.bundles
            .flatMap { bundle -> bundle.commands }
            .forEach { command -> commandLine.addSubcommand(command) }

        environment.bundles.forEach { bundle ->
            bundle.preRun()
        }

        val result = commandLine.execute(*args)
        environment.bundles.forEach { bundle ->
            bundle.postRun()
        }
        return result
    }

    companion object {
        @JvmStatic
        fun create(filePath: String): NymbApplication {
            val environment = Environment(PropertiesConfigurationReader(filePath))
            environment.registerBundle(CheckBundle(environment))
            environment.registerBundle(ServerBundle(environment))
            return NymbApplication(environment)
        }
    }
}
