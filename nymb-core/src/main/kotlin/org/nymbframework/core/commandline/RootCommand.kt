package org.nymbframework.core.commandline

import java.io.PrintWriter
import org.nymbframework.core.environment.Environment
import picocli.CommandLine

@CommandLine.Command
class RootCommand(private val environment: Environment) : NymbCommand {
    lateinit var commandLine: CommandLine

    override fun call(): Int {
        commandLine.usage(PrintWriter(System.err))
        return 0
    }
}
