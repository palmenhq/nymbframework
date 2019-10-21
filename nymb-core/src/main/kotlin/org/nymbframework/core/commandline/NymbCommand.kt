package org.nymbframework.core.commandline

import java.util.concurrent.Callable

interface NymbCommand : Callable<Int>
