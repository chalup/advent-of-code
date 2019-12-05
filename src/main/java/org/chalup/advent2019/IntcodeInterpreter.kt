package org.chalup.advent2019

import org.chalup.advent2019.IntcodeInterpreter.InterpreterStatus.Halted
import org.chalup.advent2019.IntcodeInterpreter.InterpreterStatus.Running
import org.chalup.advent2019.IntcodeInterpreter.ProgramResult.ExecutionError.UnknownOpcode
import org.chalup.advent2019.IntcodeInterpreter.ProgramResult.Finished

object IntcodeInterpreter {
    private class State(var ip: Int,
                        val memory: MutableList<Int>,
                        var status: InterpreterStatus = Running) {

        fun inParam(n: Int): Int = memory[memory[ip + n]]
        fun outParam(n: Int): OutParam = OutParam(memory[ip + n])

        private fun fetchOpcode(): Int = memory[ip] % 100

        fun fetchInstruction(): Instruction? = fetchOpcode()
            .let { opcode ->
                Instruction.fromOpcode(opcode).also { if (it == null) status = InterpreterStatus.UnknownOpcode(ip, opcode, memory) }
            }

        inner class OutParam(val address: Int) {
            fun set(value: Int) {
                memory[address] = value
            }
        }

        enum class Instruction(private val opcode: Int, private val action: State.() -> Unit) {
            ADD(opcode = 1, action = { outParam(3).set(inParam(1) + inParam(2)).also { ip += 4 } }),
            MUL(opcode = 2, action = { outParam(3).set(inParam(1) * inParam(2)).also { ip += 4 } }),
            HALT(opcode = 99, action = { status = Halted });

            fun execute(state: State) = action(state)

            companion object {
                fun fromOpcode(opcode: Int) = values().firstOrNull { it.opcode == opcode }
            }
        }
    }

    sealed class InterpreterStatus {
        object Running : InterpreterStatus()
        object Halted : InterpreterStatus()
        data class UnknownOpcode(val ip: Int, val opcode: Int, val dump: List<Int>) : InterpreterStatus()
    }

    fun execute(program: List<Int>): ProgramResult {
        val state = State(ip = 0, memory = program.toMutableList())

        while (true) {
            when (val status = state.status) {
                Running -> state.fetchInstruction()?.execute(state)
                Halted -> return Finished(finalState = state.memory)
                is InterpreterStatus.UnknownOpcode -> return UnknownOpcode(status.ip, status.opcode, status.dump)
            }
        }
    }

    sealed class ProgramResult {
        sealed class ExecutionError : ProgramResult() {
            data class UnknownOpcode(val ip: Int, val opcode: Int, val dump: List<Int>) : ExecutionError()
        }

        data class Finished(val finalState: List<Int>) : ProgramResult()
    }
}