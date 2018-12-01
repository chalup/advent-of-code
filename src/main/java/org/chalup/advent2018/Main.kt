package org.chalup.advent2018

import org.chalup.advent2018.Day1.detectCycle
import java.io.File

fun main(args: Array<String>) {
    println(detectCycle(File(args[0]).readLines()))
}
