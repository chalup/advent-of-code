package org.chalup.advent2016

object Day23 {
    fun task1(program: List<String>): Int = simulateCpu(program.toMutableList()) { if (it == 'a') 7 else 0 }

    fun task2(program: List<String>): Long {
        var a = 12L
        var b = a - 1
        do {
            val d = a
            a = 0
            val c = b

            a += c * d
            b--
        } while (b > 1)

        a += 71 * 75
        return a
    }

    private fun simulateCpu(program: MutableList<String>, registersInitializer: (Char) -> Int): Int {
        val cpu = CpuState(registersInitializer)

        while (true) {
            val instruction = program.getOrNull(cpu.pc) ?: break

            when (instruction.substringBefore(' ')) {
                "tgl" -> {
                    val src = instruction.substringAfter(' ')
                    val offset = src.toIntOrNull() ?: cpu.registers.getValue(src.single())

                    val toggledInstruction = program.getOrNull(cpu.pc + offset)
                    if (toggledInstruction != null) {
                        val arguments = toggledInstruction.substringAfter(' ')

                        program[cpu.pc + offset] = when (toggledInstruction.substringBefore(' ')) {
                            "tgl" -> "inc $arguments"
                            "inc" -> "dec $arguments"
                            "dec" -> "inc $arguments"
                            "jnz" -> "cpy $arguments"
                            "cpy" -> "jnz $arguments"
                            else -> throw IllegalArgumentException("Don't know how to toggle $toggledInstruction")
                        }
                    }
                }

                "cpy" -> {
                    val src = instruction.substringAfter(' ').substringBefore(' ')
                    val value = src.toIntOrNull() ?: cpu.registers.getValue(src.single())
                    val dst = instruction.last().takeIf { it in 'a'..'d' }

                    if (dst != null) cpu.registers[dst] = value
                }

                "inc" -> {
                    val register = instruction.last().takeIf { it in 'a'..'d' }
                    if (register != null) cpu.registers[register] = cpu.registers.getValue(register) + 1
                }

                "dec" -> {
                    val register = instruction.last().takeIf { it in 'a'..'d' }
                    if (register != null) cpu.registers[register] = cpu.registers.getValue(register) - 1
                }

                "jnz" -> {
                    val condition = instruction.substringAfter(' ').substringBefore(' ')

                    val conditionValue = condition.toIntOrNull() ?: cpu.registers.getValue(condition.single())

                    if (conditionValue != 0) {
                        val jump = instruction.substringAfterLast(' ')
                        cpu.pc += jump.toIntOrNull() ?: cpu.registers.getValue(jump.single())
                        continue
                    }
                }

                else -> throw IllegalArgumentException("Don't know how to handle $instruction")
            }

            cpu.pc++
        }

        return cpu.registers.getValue('a')
    }

    class CpuState(registersInitializer: (Char) -> Int) {
        val registers: MutableMap<Char, Int> = ('a'..'d').associateWithTo(mutableMapOf(), registersInitializer)
        var pc: Int = 0

        override fun toString(): String = buildString {
            append("PC=$pc, ")
            append(registers.entries.joinToString { (k, v) -> "${k.uppercase()}=$v" })
        }
    }
}
