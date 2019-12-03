package org.chalup.advent2019

object Day2 {
    class IntcodeProgram(initialProgram: List<Int>) {
        private val program = initialProgram.toMutableList()

        fun execute() {
            var pc = 0

            while (true) {
                when (val opcode = program[pc]) {
                    1 -> {
                        program[program[pc + 3]] = program[program[pc + 1]] + program[program[pc + 2]]
                        pc += 4
                    }
                    2 -> {
                        program[program[pc + 3]] = program[program[pc + 1]] * program[program[pc + 2]]
                        pc += 4
                    }
                    99 -> return
                    else -> throw IllegalStateException("Unknown opcode $opcode at $pc")
                }
            }
        }

        operator fun get(index: Int) = program[index]
        operator fun set(index: Int, value: Int) = program.set(index, value)

        fun dump(): List<Int> = program
    }

    fun parseProgram(input: String) = IntcodeProgram(input.split(",").map { it.toInt() })

    fun task1(input: String): Int = parseProgram(input).run {
        set(1, 12)
        set(2, 2)

        execute()

        get(0)
    }

    fun task2(input: String): Int {
        for (noun in 0..99) {
            for (verb in 0..99) {
                runCatching {
                    parseProgram(input).run {
                        set(1, noun)
                        set(2, verb)

                        execute()

                        if (get(0) == 19690720) return noun * 100 + verb
                    }
                }
            }
        }

        throw IllegalArgumentException("Cannot find the noun & verb for the supplied program and desired output")
    }
}