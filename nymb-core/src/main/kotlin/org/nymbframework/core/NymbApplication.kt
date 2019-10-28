package org.nymbframework.core

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import org.nymbframework.bundles.check.CheckBundle
import org.nymbframework.bundles.server.ServerBundle
import org.nymbframework.core.commandline.RootCommand
import org.nymbframework.core.configuration.EnvFacade
import org.nymbframework.core.configuration.EnvVarAwareProperties
import org.nymbframework.core.configuration.PropertiesConfigurationReader
import org.nymbframework.core.environment.Environment
import org.nymbframework.core.environment.EnvironmentCommandFactory
import org.slf4j.LoggerFactory
import picocli.CommandLine

/**
 * The entry point for a Nymb application.
 * @see Environment for a more in-depth detail of how to configure Nymb
 */
class NymbApplication(
    val environment: Environment
) {
    fun run(vararg args: String): Int {
        val logLevel = environment.configurationReader["log.level"]
        if (logLevel != null) {
            (LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as Logger).level = Level.valueOf(logLevel)
        }

        val environmentCommandFactory = EnvironmentCommandFactory(environment)
        environment.registerComponent(environmentCommandFactory)
        val rootCommand = RootCommand(environment)
        val commandLine = CommandLine(rootCommand, environmentCommandFactory)
        rootCommand.commandLine = commandLine
        environment.bundles
            .flatMap { bundle -> bundle.commands }
            .forEach { command -> commandLine.addSubcommand(command) }
        environment.commands.forEach { command ->
            commandLine.addSubcommand(command)
        }

        environment.bundles.forEach { bundle ->
            bundle.preRun()
        }

        val result = commandLine.execute(*args)
        environment.bundles.forEach { bundle ->
            bundle.postExec()
        }
        return result
    }

    companion object {
        /**
         * Factory method for a standard [NymbApplication].
         *
         * @param filePath Resource path to a configuration file that, at least, contains the property `app.mode`. This file
         * must be included with the built JAR.
         */
        @JvmStatic
        fun create(filePath: String): NymbApplication {
            val envFacade = EnvFacade()
            val environment = Environment(
                PropertiesConfigurationReader(filePath, properties = EnvVarAwareProperties(envFacade)),
                envFacade
            )
            environment.registerBundle(CheckBundle(environment))
            environment.registerBundle(ServerBundle(environment))
            return NymbApplication(environment)
        }
    }
}
