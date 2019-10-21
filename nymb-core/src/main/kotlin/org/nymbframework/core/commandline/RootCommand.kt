package org.nymbframework.core.commandline

import picocli.CommandLine

@CommandLine.Command
class RootCommand : NymbCommand {
    lateinit var commandLine: CommandLine

    override fun call(): Int {
        // Noop
        commandLine.usage(System.out)
        return 0
    }
}
