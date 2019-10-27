package org.nymbframework.bundles.database.commands

import java.io.PrintWriter
import liquibase.Liquibase
import org.nymbframework.core.commandline.NymbCommand
import org.nymbframework.core.environment.Environment
import picocli.CommandLine

@CommandLine.Command(
    name = "migrate",
    aliases = ["up"],
    description = ["Run pending migrations"],
    mixinStandardHelpOptions = true
)
class MigrateCommand constructor(private val environment: Environment) : NymbCommand {
    @CommandLine.Option(
        names = ["-c", "--count"],
        description = ["Count of migration steps to run"]
    )
    private val count: Int? = null

    @CommandLine.Option(
        names = ["--dry-run"],
        description = ["Don't run pending migrations on the database, just print them to STDOUT"]
    )
    private var dryRun: Boolean = false

    override fun call(): Int {
        val liquibase = environment[Liquibase::class.java]
        if (dryRun) {
            if (count == null) {
                liquibase.update("", PrintWriter(System.out))
            } else {
                liquibase.update(count, "", PrintWriter(System.out))
            }
            return 0
        }

        if (count == null) {
            liquibase.update("")
        } else {
            liquibase.update(count, "")
        }
        return 0
    }
}
