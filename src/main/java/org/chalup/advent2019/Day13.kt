package org.chalup.advent2019

import org.chalup.advent2019.IntcodeInterpreter.Companion.runProgram

object Day13 {
    fun task1(pongProgram: String): Int =
        runProgram(pongProgram).chunked(3).count { (_, _, tile) -> tile == 2L }
}