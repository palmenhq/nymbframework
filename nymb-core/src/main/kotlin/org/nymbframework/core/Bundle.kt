package org.nymbframework.core

import org.nymbframework.core.commandline.NymbCommand
import org.nymbframework.core.environment.Environment

/**
 * A bundle of components, commands and pre and post run actions.
 */
abstract class Bundle(protected val environment: Environment) {
    /**
     * Commands to register to the [org.nymbframework.core.commandline.RootCommand]. Will be added to the runtime of
     * [NymbApplication].
     */
    open val commands: List<NymbCommand>
        get() = emptyList()

    /**
     * Register any components needed by the [Environment].
     */
    open fun registerComponents() {
        // noop
    }

    /**
     * Called before the matching commands are executed but after all components are registered. Useful for i.e.
     * startup messages or actions.
     */
    open fun preRun() {
        // noop
    }

    /**
     * Called after the matching commands are executed. Please note that if dangling threads are started this might run
     * simultaneously (i.e. for the [org.nymbframework.bundles.server.ServerCommand] this will NOT run after
     * [io.javalin.Javalin] stops but rather right after it's started because it does not run on the main thread).
     */
    open fun postExec() {
        // noop
    }
}
