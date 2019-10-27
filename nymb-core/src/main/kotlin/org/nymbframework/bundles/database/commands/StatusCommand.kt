package org.nymbframework.bundles.database.commands

import java.io.PrintWriter
import liquibase.Liquibase
import org.nymbframework.core.commandline.NymbCommand
import org.nymbframework.core.environment.Environment
import picocli.CommandLine

@CommandLine.Command(
    name = "status",
    description = ["Report the current db status"]
)
class StatusCommand(private val environment: Environment) : NymbCommand {
    @CommandLine.Option(names = ["-v", "--verbose"])
    private var verbose: Boolean = false

    override fun call(): Int {
        environment[Liquibase::class.java].reportStatus(verbose, "", PrintWriter(System.out))

        return 0
    }
}
