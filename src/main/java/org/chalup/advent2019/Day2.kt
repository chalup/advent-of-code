package org.chalup.advent2019

object Day2 {
    class IntcodeInterpreter(initialProgram: List<Int>) {
        private val memory = initialProgram.toMutableList()

        fun execute() {
            var ip = 0

            while (true) {
                when (val opcode = memory[ip]) {
                    1 -> {
                        memory[memory[ip + 3]] = memory[memory[ip + 1]] + memory[memory[ip + 2]]
                        ip += 4
                    }
                    2 -> {
                        memory[memory[ip + 3]] = memory[memory[ip + 1]] * memory[memory[ip + 2]]
                        ip += 4
                    }
                    99 -> return
                    else -> throw IllegalStateException("Unknown opcode $opcode at $ip")
                }
            }
        }

        operator fun get(index: Int) = memory[index]
        operator fun set(index: Int, value: Int) = memory.set(index, value)

        fun dump(): List<Int> = memory
    }

    fun parseProgram(input: String) = IntcodeInterpreter(input.split(",").map { it.toInt() })

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