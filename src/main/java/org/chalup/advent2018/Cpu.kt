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

data class Cpu(val registers: MutableList<Int> = MutableList(4) { 0 }) {
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
}