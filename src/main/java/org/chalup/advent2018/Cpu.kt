package org.chalup.advent2018

import org.chalup.advent2018.Cpu.Opcode.ADDI
import org.chalup.advent2018.Cpu.Opcode.ADDR
import org.chalup.advent2018.Cpu.Opcode.BANI
import org.chalup.advent2018.Cpu.Opcode.BANR
import org.chalup.advent2018.Cpu.Opcode.BORI
import org.chalup.advent2018.Cpu.Opcode.BORR
import org.chalup.advent2018.Cpu.Opcode.EQIR
import org.chalup.advent2018.Cpu.Opcode.EQRI
import org.chalup.advent2018.Cpu.Opcode.EQRR
import org.chalup.advent2018.Cpu.Opcode.GTIR
import org.chalup.advent2018.Cpu.Opcode.GTRI
import org.chalup.advent2018.Cpu.Opcode.GTRR
import org.chalup.advent2018.Cpu.Opcode.MULI
import org.chalup.advent2018.Cpu.Opcode.MULR
import org.chalup.advent2018.Cpu.Opcode.SETI
import org.chalup.advent2018.Cpu.Opcode.SETR

data class Cpu(val registers: MutableList<Int> = MutableList(4) { 0 },
               private var instructionPointer: Int = 0) {
    constructor(numberOfRegisters: Int) : this(MutableList(numberOfRegisters) { 0 })

    fun execute(opcode: Opcode, params: List<Int>) {
        val (inA, inB, out) = params

        registers[out] = when (opcode) {
            ADDR -> registers[inA] + registers[inB]
            ADDI -> registers[inA] + inB
            MULR -> registers[inA] * registers[inB]
            MULI -> registers[inA] * inB
            BANR -> registers[inA] and registers[inB]
            BANI -> registers[inA] and inB
            BORR -> registers[inA] or registers[inB]
            BORI -> registers[inA] or inB
            SETR -> registers[inA]
            SETI -> inA
            GTIR -> if (inA > registers[inB]) 1 else 0
            GTRI -> if (registers[inA] > inB) 1 else 0
            GTRR -> if (registers[inA] > registers[inB]) 1 else 0
            EQIR -> if (inA == registers[inB]) 1 else 0
            EQRI -> if (registers[inA] == inB) 1 else 0
            EQRR -> if (registers[inA] == registers[inB]) 1 else 0
        }
    }

    enum class Opcode {
        ADDR, ADDI,
        MULR, MULI,
        BANR, BANI,
        BORR, BORI,
        SETR, SETI,
        GTIR, GTRI, GTRR,
        EQIR, EQRI, EQRR
    }

    data class Instruction(val opcode: Opcode, val params: List<Int>)

    data class Program(val instructions: List<Instruction>,
                       val instructionPointerBinding: Int,
                       val specialHandlers: Map<Int, Cpu.() -> Unit> = emptyMap(),
                       val breakPoints: Map<Int, Cpu.() -> Boolean> = emptyMap())

    fun executeProgram(program: Program) = with(program) {
        instructionPointer = 0

        while (instructionPointer in program.instructions.indices) {
            registers[instructionPointerBinding] = instructionPointer

            val halt = breakPoints[instructionPointer]?.invoke(this@Cpu) ?: false
            if (halt) return@with

            val specialHandler = specialHandlers[instructionPointer]

            if (specialHandler != null) {
                specialHandler.invoke(this@Cpu)
            } else {
                val (opcode, params) = instructions[instructionPointer]
                execute(opcode, params)
            }

            instructionPointer = registers[instructionPointerBinding] + 1
        }
    }

    fun dump() {
        println("IP: ${instructionPointer.toString().padStart(10)} | ${registers.joinToString(separator = " ") { "$it".padStart(10) }}")
    }

    companion object {
        fun parseProgram(input: List<String>) =
            Program(instructions = input.drop(1).map { parseInstruction(it) },
                    instructionPointerBinding = parseBinding(input.first()))

        private fun parseBinding(bindingInput: String) = bindingInput.split(' ').last().toInt()

        private fun parseInstruction(instructionInput: String) = instructionInput.split(' ').let { elements ->
            val opcode = Opcode.valueOf(elements.first().toUpperCase())
            val params = elements.drop(1).map { it.toInt() }

            Instruction(opcode, params)
        }
    }
}