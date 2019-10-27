package org.nymbframework.bundles.database.commands

import java.io.PrintStream
import liquibase.Liquibase
import org.nymbframework.core.commandline.NymbCommand
import org.nymbframework.core.environment.Environment
import org.slf4j.LoggerFactory.getLogger
import picocli.CommandLine

@CommandLine.Command(name = "rollback", aliases = ["down"], description = ["Undo migrations"], mixinStandardHelpOptions = true)
class RollbackCommand(private val environment: Environment) : NymbCommand {
    @CommandLine.Option(names = ["-t", "--tag"], description = ["Rollback to this change set's tag"])
    private var tag: String? = null

    @CommandLine.Option(
        names = ["-c", "--count"],
        description = ["Rollback the last <count> change sets."]
    )
    private val count: Int? = null

    override fun call(): Int {
        val liquibase = environment[Liquibase::class.java]

        if (tag != null && count != null) {
            logger.error("Only one rollback point can be provided (received both --tag and --count)")
            CommandLine.usage(this, PrintStream(System.err))
            return 1
        }

        if (tag != null) {
            liquibase.rollback(tag, "")
            return 0
        }

        if (count != null) {
            liquibase.rollback(count, "")
            return 0
        }

        logger.error("No rollback point given (must provide either --tag or --count)")
        CommandLine.usage(this, PrintStream(System.err))
        return 1
    }

    companion object {
        private val logger = getLogger(RollbackCommand::class.java)
    }
}
