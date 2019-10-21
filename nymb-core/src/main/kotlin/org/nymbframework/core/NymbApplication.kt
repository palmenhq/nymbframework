package org.nymbframework.core

import org.nymbframework.core.commandline.RootCommand
import org.nymbframework.core.environment.Environment
import picocli.CommandLine

class NymbApplication(
    private val environment: Environment
) {
    fun start(vararg args: String): Int {
        val rootCommand = RootCommand()
        val commandLine = CommandLine(rootCommand)
        rootCommand.commandLine = commandLine
        environment.bundles
            .flatMap { bundle -> bundle.commands }
            .forEach { command -> commandLine.addSubcommand(command) }

        return commandLine.execute(*args)
    }
}
