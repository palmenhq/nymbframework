package org.nymbframework.core

import org.nymbframework.core.commandline.NymbCommand
import org.nymbframework.core.environment.Environment

abstract class Bundle(protected val environment: Environment) {
    open val commands: List<NymbCommand>
        get() = emptyList()
}
