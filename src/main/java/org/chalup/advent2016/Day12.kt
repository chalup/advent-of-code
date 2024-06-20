package org.chalup.advent2016

object Day12 {
    fun task1(program: List<String>): Int = simulateCpu(program) { 0 }
    fun task2(program: List<String>): Int = simulateCpu(program) { if (it == 'c') 1 else 0 }

    private fun simulateCpu(program: List<String>, registersInitializer: (Char) -> Int): Int {
        val cpu = CpuState(registersInitializer)

        while (true) {
            val instruction = program.getOrNull(cpu.pc) ?: break

            when (instruction.substringBefore(' ')) {
                "cpy" -> cpu.registers[instruction.last()] = run {
                    val src = instruction.substringAfter(' ').substringBefore(' ')

                    src.toIntOrNull() ?: cpu.registers.getValue(src.single())
                }

                "inc" -> cpu.registers.computeIfPresent(instruction.last()) { _, value -> value + 1 }
                "dec" -> cpu.registers.computeIfPresent(instruction.last()) { _, value -> value - 1 }
                "jnz" -> {
                    val condition = instruction.substringAfter(' ').substringBefore(' ')

                    val conditionValue = condition.toIntOrNull() ?: cpu.registers.getValue(condition.single())

                    if (conditionValue != 0) {
                        cpu.pc += instruction.substringAfterLast(' ').toInt()
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
    }
}
