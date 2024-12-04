package org.chalup.advent2024

object Day3 {
    fun task1(input: List<String>): Int = input
        .sumOf { program ->
            val regex = """mul\((\d{1,3}),(\d{1,3})\)""".toRegex()

            regex.findAll(program)
                .map { it.destructured.toList().map(String::toInt) }
                .sumOf { (a, b) -> a * b }
        }

    fun task2(input: List<String>): Int = input
        .flatMap { program ->
            val regex = """(mul)\((\d{1,3}),(\d{1,3})\)|(do)\(\)|(don't)\(\)""".toRegex()

            regex.findAll(program)
                .map {
                    when {
                        it.groupValues[1] == "mul" -> {
                            val (lhs, rhs) = it.destructured.toList().drop(1).take(2).map(String::toInt)
                            ProgramInstruction.Mul(lhs, rhs)
                        }
                        it.groupValues[4] == "do" -> ProgramInstruction.Do
                        it.groupValues[5] == "don't" -> ProgramInstruction.Dont
                        else -> throw IllegalStateException("Unexpected match result: ${it.destructured.toList()}")
                    }
                }
        }
        .fold(ExecutionState()) { state, op ->
            when (op) {
                ProgramInstruction.Do -> state.copy(enabled = true)
                ProgramInstruction.Dont -> state.copy(enabled = false)
                is ProgramInstruction.Mul -> if (state.enabled) {
                    state.copy(acc = state.acc + (op.lhs * op.rhs))
                } else {
                    state
                }
            }
        }
        .acc

    private data class ExecutionState(val acc: Int = 0, val enabled: Boolean = true)

    private sealed interface ProgramInstruction {
        data class Mul(val lhs: Int, val rhs: Int) : ProgramInstruction
        data object Do : ProgramInstruction
        data object Dont : ProgramInstruction
    }
}