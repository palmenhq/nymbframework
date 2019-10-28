package org.nymbframework.core.commandline

import java.io.PrintWriter
import org.nymbframework.core.environment.Environment
import picocli.CommandLine

/**
 * Internal command used as the base of all commands registered to Nymb/pico
 *
 * @see Environment.commands
 */
@CommandLine.Command
class RootCommand(private val environment: Environment) : NymbCommand {
    lateinit var commandLine: CommandLine

    override fun call(): Int {
        commandLine.usage(PrintWriter(System.err))
        return 0
    }
}
