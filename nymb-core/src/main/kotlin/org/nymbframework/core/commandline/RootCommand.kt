package org.nymbframework.core.commandline

import java.io.PrintWriter
import org.nymbframework.core.environment.Environment
import org.nymbframework.core.environment.EnvironmentCommandFactory
import picocli.CommandLine

@CommandLine.Command
class RootCommand(private val environment: Environment) : NymbCommand {
    lateinit var commandLine: CommandLine

    override fun call(): Int {
        // Noop
        CommandLine(this, environment[EnvironmentCommandFactory::class.java]).usage(PrintWriter(System.err))
        return 0
    }
}
