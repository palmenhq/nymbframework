package org.nymbframework.bundles.check

import org.nymbframework.core.commandline.NymbCommand
import org.slf4j.LoggerFactory.getLogger
import picocli.CommandLine

@CommandLine.Command(name = "check")
class CheckCommand(
    private val appMode: String,
    private val disableEmojis: Boolean
) : NymbCommand {

    @CommandLine.Option(names = ["disable-emojis"])
    var cliDisableEmojis: Boolean = false

    private val logger = getLogger(CheckCommand::class.java)
    override fun call(): Int {
        if (disableEmojis || cliDisableEmojis) {
            logger.info("All is good in appMode \"$appMode\"! :+1:")
        } else {
            logger.info("All is good in appMode \"$appMode\" 👍")
        }

        return 0
    }
}
