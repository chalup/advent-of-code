package org.chalup.advent2018

object Day21 {
    fun part1(input: List<String>) {
        fun Cpu.printRegister5AndCrash(): Boolean {
            println(registers[5])
            return true
        }

        Cpu(numberOfRegisters = 6)
            .apply {
                executeProgram(
                    Cpu.parseProgram(input).copy(
                        breakPoints = mapOf(28 to Cpu::printRegister5AndCrash)
                    )
                )
            }
    }

    fun part2(input: List<String>) {
        val haltingValues = mutableSetOf<Int>()
        var lastValue: Int = -1

        fun Cpu.findLoop(): Boolean {
            val haltAt = registers[5]

            if (haltingValues.add(haltAt)) {
                lastValue = haltAt
                return false
            } else {
                println(lastValue)
                return true
            }
        }

        Cpu(numberOfRegisters = 6)
            .apply {
                executeProgram(
                    Cpu.parseProgram(input).copy(
                        breakPoints = mapOf(28 to Cpu::findLoop)
                    )
                )
            }
    }
}
