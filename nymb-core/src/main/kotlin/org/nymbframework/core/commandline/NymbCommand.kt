package org.nymbframework.core.commandline

import java.util.concurrent.Callable

/**
 * Convenience interface to implement [PicoCLI's CommandLine][picocli.CommandLine] commands.
 *
 * If registered with [picocli.CommandLine.Command.subcommands] it'll, by default, be instatiated with
 * [org.nymbframework.core.environment.EnvironmentCommandFactory] which means it should have no-args constructor or
 * a constructor of type ([org.nymbframework.core.environment.Environment]).
 */
interface NymbCommand : Callable<Int>
