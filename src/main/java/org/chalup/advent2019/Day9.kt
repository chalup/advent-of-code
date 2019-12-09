package org.chalup.advent2019

import org.chalup.advent2019.IntcodeInterpreter.Companion.runProgram

object Day9 {
    fun testBoostProgram(program: String) = runProgram(program, 1)
    fun runBoostProgram(program: String) = runProgram(program, 2)
}
