package org.chalup.advent2016

object Day25 {
    fun task1(program: List<String>): Int {
        return generateSequence(0) { it + 1 }
            .first { a -> isClockSignalGenerator(program, a) }
    }

    private fun isClockSignalGenerator(program: List<String>, initialValueOfRegisterA: Int): Boolean {
        var cpu = CpuState(initialValueOfRegisterA)

        val knownStates = mutableSetOf<CpuState>()
        while (true) {
            // encountered a loop, check if we emitted at least one clock cycle
            if (!knownStates.add(cpu)) return cpu.emittedZero && cpu.emittedOne

            cpu = cpu.executeStep(program) ?: return false
        }
    }

    data class CpuState(
        val registers: Map<Char, Int>,
        val pc: Int = 0,
        val expectedOutput: Int = 0,
        val emittedZero: Boolean = false,
        val emittedOne: Boolean = false,
    ) {
        constructor(initialValueOfRegisterA: Int) : this(
            ('a'..'d').associateWithTo(mutableMapOf()) { if (it == 'a') initialValueOfRegisterA else 0 }
        )

        fun executeStep(program: List<String>) : CpuState? {
            val instruction = program.getOrNull(pc) ?: return null

            return when (instruction.substringBefore(' ')) {
                "cpy" -> copy(
                    registers = registers.toMutableMap().apply {
                        put(
                            instruction.last(),
                            run {
                                val src = instruction.substringAfter(' ').substringBefore(' ')

                                src.toIntOrNull() ?: registers.getValue(src.single())
                            }
                        )
                    },
                    pc = pc + 1
                )

                "inc" -> copy(
                    registers = registers.toMutableMap().apply {
                        put(
                            instruction.last(),
                            registers.getValue(instruction.last()) + 1
                        )
                    },
                    pc = pc + 1
                )

                "dec" -> copy(
                    registers = registers.toMutableMap().apply {
                        put(
                            instruction.last(),
                            registers.getValue(instruction.last()) - 1
                        )
                    },
                    pc = pc + 1
                )
                "jnz" -> {
                    val condition = instruction.substringAfter(' ').substringBefore(' ')
                    val conditionValue = condition.toIntOrNull() ?: registers.getValue(condition.single())

                    if (conditionValue != 0) {
                        copy(pc = pc + instruction.substringAfterLast(' ').toInt())
                    } else {
                        copy(pc = pc + 1)
                    }
                }
                "out" -> {
                    val outputValue = registers.getValue(instruction.last())

                    when {
                        outputValue == 0 && expectedOutput == 0 -> copy(
                            expectedOutput = 1,
                            emittedZero = true,
                            pc = pc + 1
                        )
                        outputValue == 1 && expectedOutput == 1 -> copy(
                            expectedOutput = 0,
                            emittedOne = true,
                            pc = pc + 1
                        )
                        else -> return null
                    }
                }
                else -> throw IllegalArgumentException("Don't know how to handle $instruction")
            }
        }
    }
}
