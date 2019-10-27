package org.nymbframework.bundles.database.commands

import java.io.PrintWriter
import org.nymbframework.core.commandline.NymbCommand
import org.nymbframework.core.environment.Environment
import org.nymbframework.core.environment.EnvironmentCommandFactory
import picocli.CommandLine

@CommandLine.Command(
    name = "db",
    aliases = ["database"],
    description = ["Manage the database using Liquibase"],
    subcommands = [MigrateCommand::class, RollbackCommand::class, StatusCommand::class],
    mixinStandardHelpOptions = true
)
class RootDatabaseCommand(private val environment: Environment) : NymbCommand {
    override fun call(): Int {
        CommandLine(this, environment[EnvironmentCommandFactory::class.java]).usage(PrintWriter(System.err))
        return 0
    }
}
