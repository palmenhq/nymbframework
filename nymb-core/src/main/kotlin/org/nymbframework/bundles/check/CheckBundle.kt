package org.nymbframework.bundles.check

import org.nymbframework.core.Bundle
import org.nymbframework.core.commandline.NymbCommand
import org.nymbframework.core.environment.Environment

class CheckBundle(environment: Environment) : Bundle(environment) {
    override val commands: List<NymbCommand>
        get() = listOf(
            CheckCommand(
                healthChecker = AppHealthChecker(environment.getHealthCheckers()),
                appMode = environment.configurationReader["app.mode"]!!,
                disableEmojis = environment.configurationReader.getBoolean("nymb.emojis") == false
            )
        )
}
