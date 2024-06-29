package org.chalup.advent2015

object Day23 {
    fun task1(input: List<String>): Long = runProgram(input) { 0L }
    fun task2(input: List<String>): Long = runProgram(input) { if (it == "a") 1L else 0L }

    private fun runProgram(program: List<String>, registersInitializer: (String) -> Long): Long {
        var pc = 0
        val registers = mutableMapOf<String, Long>().withDefault(registersInitializer)

        while (true) {
            val i = program.getOrNull(pc)?.split(" ") ?: break

            when (i[0]) {
                "hlf" -> registers[i[1]] = registers.getValue(i[1]) / 2
                "tpl" -> registers[i[1]] = registers.getValue(i[1]) * 3
                "inc" -> registers[i[1]] = registers.getValue(i[1]) + 1
                "jmp" -> {
                    pc += i[1].toInt()
                    continue
                }

                "jie" -> if (registers.getValue(i[1].trim(',')) % 2 == 0L) {
                    pc += i[2].toInt()
                    continue
                }

                "jio" -> if (registers.getValue(i[1].trim(',')) == 1L) {
                    pc += i[2].toInt()
                    continue
                }
            }

            pc++
        }

        return registers.getValue("b")
    }
}
