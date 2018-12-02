package org.chalup.advent2018

import java.io.File

fun main(args: Array<String>) {
    println(Day2.checksum(File(args[0]).readLines()))
}
