package com.csgopoison.plugins.official.allplugins

import java.io.File
import java.util.concurrent.CopyOnWriteArrayList
import java.time.LocalDateTime
import kotlin.io.path.Path
import kotlin.io.path.appendLines
import kotlin.io.path.appendText
import kotlin.io.path.writeLines

//val console = Console()

val shutDown = Thread {
    val file = Path(Console.consoleLogFile)
    val newTo = Console.consoleBuffer

    file.appendLines(newTo.lineSequence())
}

Runtime.getRuntime().addShutdownHook(shutDown)