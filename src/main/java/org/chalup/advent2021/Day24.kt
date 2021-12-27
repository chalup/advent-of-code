package org.chalup.advent2021

import org.chalup.utils.match
import java.util.PriorityQueue

object Day24 {
    fun task1(input: List<String>): Long = input
        .map(::parseOpcode)
        .let { program -> findModelNumber(program, order = compareByDescending { it.modelNumber }) }

    fun task2(input: List<String>): Long = input
        .map(::parseOpcode)
        .let { program -> findModelNumber(program, order = compareByDescending<Execution> { it.ic }.thenBy { it.modelNumber }) }
}

private fun findModelNumber(program: List<Opcode>, order: Comparator<Execution>): Long {
    val triedCombinations = mutableSetOf<Pair<Int, Long>>()
    val queue = PriorityQueue(order).apply { add(Execution(program)) }

    while (queue.isNotEmpty()) {
        val execution = queue.poll()!!

        if (!triedCombinations.add(execution.key())) continue

        for (i in 1..9) {
            execution
                .runUntilNextInput(i.toLong())
                .also { nextStep ->
                    when (nextStep.getResult()) {
                        null -> queue.add(nextStep)
                        0L -> return nextStep.modelNumber
                        else -> Unit
                    }
                }
        }
    }

    throw IllegalStateException()
}

private data class Execution(
    val program: List<Opcode>,
    val modelNumber: Long = 0L,
    val ic: Int = 0,
    val registers: Map<Register, Long> = emptyMap(),
) {
    fun getResult(): Long? = registers
        .getOrDefault(Register.Z, 0L)
        .takeIf { ic == program.size }

    fun key() = ic to registers.getOrDefault(Register.Z, 0L)

    fun runUntilNextInput(input: Long): Execution {
        check(program[ic] is Opcode.Input)

        var newIc = ic
        val newRegisters = registers.toMutableMap()

        fun Operand.getValue() = when (this) {
            is Literal -> value
            is Register -> newRegisters[this] ?: 0
        }

        do {
            when (val opcode = program[newIc++]) {
                is Opcode.Input -> newRegisters[opcode.register] = input
                is Opcode.Add -> newRegisters[opcode.register] = opcode.register.getValue() + opcode.operand.getValue()
                is Opcode.Div -> newRegisters[opcode.register] = opcode.register.getValue() / opcode.operand.getValue()
                is Opcode.Eq -> newRegisters[opcode.register] = if (opcode.register.getValue() == opcode.operand.getValue()) 1 else 0
                is Opcode.Mod -> newRegisters[opcode.register] = opcode.register.getValue() % opcode.operand.getValue()
                is Opcode.Mul -> newRegisters[opcode.register] = opcode.register.getValue() * opcode.operand.getValue()
            }
        } while (newIc != program.size && program[newIc] !is Opcode.Input)

        return copy(
            modelNumber = modelNumber * 10 + input,
            ic = newIc,
            registers = newRegisters
        )
    }
}

private fun parseOpcode(line: String) = match<Opcode>(line) {
    pattern("""inp (.)""") { (register) -> Opcode.Input(Register.from(register)) }
    pattern("""add (.*?) (.*?)""") { (a, b) -> Opcode.Add(Register.from(a), Operand.from(b)) }
    pattern("""mul (.*?) (.*?)""") { (a, b) -> Opcode.Mul(Register.from(a), Operand.from(b)) }
    pattern("""div (.*?) (.*?)""") { (a, b) -> Opcode.Div(Register.from(a), Operand.from(b)) }
    pattern("""mod (.*?) (.*?)""") { (a, b) -> Opcode.Mod(Register.from(a), Operand.from(b)) }
    pattern("""eql (.*?) (.*?)""") { (a, b) -> Opcode.Eq(Register.from(a), Operand.from(b)) }
}

private sealed interface Operand {
    companion object {
        fun from(input: String) = input
            .toLongOrNull()
            ?.let { Literal(it) }
            ?: Register.from(input)
    }
}

private enum class Register : Operand {
    X, Y, Z, W;

    companion object {
        fun from(register: String) = values().first { it.name.lowercase() == register }
    }
}

private data class Literal(val value: Long) : Operand

private sealed class Opcode {
    data class Input(val register: Register) : Opcode()
    data class Add(val register: Register, val operand: Operand) : Opcode()
    data class Mul(val register: Register, val operand: Operand) : Opcode()
    data class Div(val register: Register, val operand: Operand) : Opcode()
    data class Mod(val register: Register, val operand: Operand) : Opcode()
    data class Eq(val register: Register, val operand: Operand) : Opcode()
}
