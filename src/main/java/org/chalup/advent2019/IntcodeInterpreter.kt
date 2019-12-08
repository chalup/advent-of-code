package org.chalup.advent2019

import org.chalup.advent2019.IntcodeInterpreter.InterpreterStatus.Halted
import org.chalup.advent2019.IntcodeInterpreter.InterpreterStatus.InterpreterError
import org.chalup.advent2019.IntcodeInterpreter.InterpreterStatus.InterpreterError.InInstructionWithoutInput
import org.chalup.advent2019.IntcodeInterpreter.InterpreterStatus.InterpreterError.OutParamWithImmediateMode
import org.chalup.advent2019.IntcodeInterpreter.InterpreterStatus.InterpreterError.UnknownOpcode
import org.chalup.advent2019.IntcodeInterpreter.InterpreterStatus.Running
import org.chalup.advent2019.IntcodeInterpreter.ProgramResult.ExecutionError
import org.chalup.advent2019.IntcodeInterpreter.ProgramResult.Finished
import org.chalup.advent2019.IntcodeInterpreter.State.ParameterMode.IMMEDIATE
import org.chalup.advent2019.IntcodeInterpreter.State.ParameterMode.POSITION
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue

object IntcodeInterpreter {
    private class State(var ip: Int,
                        val inputs: BlockingQueue<Int>,
                        val memory: MutableList<Int>,
                        val outputs: BlockingQueue<Int>,
                        var status: InterpreterStatus = Running) {

        fun inParam(n: Int): Int = when (paramMode(n)) {
            IMMEDIATE -> memory[ip + n]
            POSITION -> memory[memory[ip + n]]
        }

        fun outParam(n: Int) = OutParam(n)

        private fun paramMode(n: Int) = memory[ip].let {
            var flag = it

            repeat(n + 1) { flag /= 10 }

            if (flag % 10 == 0) POSITION else IMMEDIATE
        }

        private fun fetchOpcode(): Int = memory[ip] % 100

        fun fetchInstruction(): Instruction? = fetchOpcode()
            .let { opcode ->
                Instruction.fromOpcode(opcode).also { if (it == null) status = UnknownOpcode(ip, opcode, memory) }
            }

        inner class OutParam(val n: Int) {
            fun set(value: Int) {
                when (paramMode(n)) {
                    POSITION -> memory[memory[ip + n]] = value
                    IMMEDIATE -> status = OutParamWithImmediateMode(ip, fetchOpcode(), n, memory)
                }
            }
        }

        private enum class ParameterMode { IMMEDIATE, POSITION }

        enum class Instruction(private val opcode: Int, private val action: State.() -> Unit) {
            ADD(opcode = 1, action = { outParam(3).set(inParam(1) + inParam(2)).also { ip += 4 } }),
            MUL(opcode = 2, action = { outParam(3).set(inParam(1) * inParam(2)).also { ip += 4 } }),
            IN(opcode = 3, action = {
                outParam(1).set(inputs.take()).also { ip += 2 }
            }),
            OUT(opcode = 4, action = {
                outputs.put(inParam(1))
                ip += 2
            }),
            JUMP_TRUE(opcode = 5, action = {
                if (inParam(1) != 0) ip = inParam(2)
                else ip += 3
            }),
            JUMP_FALSE(opcode = 6, action = {
                if (inParam(1) == 0) ip = inParam(2)
                else ip += 3
            }),
            LESS_THAN(opcode = 7, action = { outParam(3).set(if (inParam(1) < inParam(2)) 1 else 0).also { ip += 4 } }),
            EQUALS(opcode = 8, action = { outParam(3).set(if (inParam(1) == inParam(2)) 1 else 0).also { ip += 4 } }),
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

        sealed class InterpreterError : InterpreterStatus() {
            data class OutParamWithImmediateMode(val ip: Int, val opcode: Int, val n: Int, val dump: List<Int>) : InterpreterError()
            data class UnknownOpcode(val ip: Int, val opcode: Int, val dump: List<Int>) : InterpreterError()
            data class InInstructionWithoutInput(val ip: Int, val dump: List<Int>) : InterpreterError()
        }
    }

    fun execute(program: List<Int>,
                inputs: BlockingQueue<Int> = LinkedBlockingQueue(),
                outputs: BlockingQueue<Int> = LinkedBlockingQueue()): ProgramResult {
        val state = State(ip = 0,
                          inputs = inputs,
                          outputs = outputs,
                          memory = program.toMutableList())

        while (true) {
            when (val status = state.status) {
                Running -> state.fetchInstruction()?.execute(state)
                Halted -> return Finished(finalState = state.memory)
                is InterpreterError -> return ExecutionError(error = status)
            }
        }
    }

    sealed class ProgramResult {
        data class ExecutionError(val error: InterpreterError) : ProgramResult() {
            fun getErrorMessage(): String = when (error) {
                is OutParamWithImmediateMode -> "Found outparam in immediate mode [param #${error.n} in opcode ${error.opcode} at ${error.ip}]. Full dump: ${error.dump}"
                is UnknownOpcode -> "Unknown opcode ${error.opcode} at ${error.ip} in ${error.dump}"
                is InInstructionWithoutInput -> "Found IN instruction (opcode 3) at ${error.ip}, but the input was not provided to the interpreter. Full dump: ${error.dump}"
            }
        }

        data class Finished(val finalState: List<Int>) : ProgramResult()
    }
}