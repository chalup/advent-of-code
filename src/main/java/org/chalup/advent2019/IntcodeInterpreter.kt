package org.chalup.advent2019

import org.chalup.advent2019.IntcodeInterpreter.InterpreterStatus.EmittingOutput
import org.chalup.advent2019.IntcodeInterpreter.InterpreterStatus.Halted
import org.chalup.advent2019.IntcodeInterpreter.InterpreterStatus.InterpreterError
import org.chalup.advent2019.IntcodeInterpreter.InterpreterStatus.InterpreterError.InInstructionWithoutInput
import org.chalup.advent2019.IntcodeInterpreter.InterpreterStatus.InterpreterError.OutParamWithImmediateMode
import org.chalup.advent2019.IntcodeInterpreter.InterpreterStatus.InterpreterError.UnknownOpcode
import org.chalup.advent2019.IntcodeInterpreter.InterpreterStatus.Running
import org.chalup.advent2019.IntcodeInterpreter.ParameterMode.IMMEDIATE
import org.chalup.advent2019.IntcodeInterpreter.ParameterMode.POSITION
import org.chalup.advent2019.IntcodeInterpreter.ParameterMode.RELATIVE
import org.chalup.advent2019.IntcodeInterpreter.ProgramResult.ExecutionError
import org.chalup.advent2019.IntcodeInterpreter.ProgramResult.Finished
import org.chalup.advent2019.IntcodeInterpreter.ProgramResult.GeneratedOutput

class IntcodeInterpreter(memoryDump: Memory) {
    constructor(initialProgram: List<Long>) : this(Memory(initialProgram))

    private var ip: Long = 0
    private var relativeOffset: Long = 0
    private val inputs: MutableList<Long> = mutableListOf()
    private val memory: Memory = Memory(memoryDump)
    private var status: InterpreterStatus = Running

    fun sendInput(input: Long) = inputs.add(input)

    private operator fun List<Long>.get(at: Long) = get(at.toInt())

    private fun inParam(n: Int): Long = when (paramMode(n)) {
        IMMEDIATE -> memory[ip + n]
        POSITION -> memory[memory[ip + n]]
        RELATIVE -> memory[memory[ip + n] + relativeOffset]
    }

    private fun outParam(n: Int) = OutParam(n)

    private fun paramMode(n: Int) = memory[ip].let {
        var flag = it

        repeat(n + 1) { flag /= 10 }

        when (val mode = flag % 10) {
            0L -> POSITION
            1L -> IMMEDIATE
            2L -> RELATIVE
            else -> throw AssertionError("Unknown parameter mode $mode")
        }
    }

    private fun fetchOpcode(): Int = (memory[ip] % 100).toInt()

    private fun fetchInstruction(): Instruction? = fetchOpcode()
        .let { opcode ->
            Instruction.fromOpcode(opcode).also { if (it == null) status = UnknownOpcode(ip, opcode, memory) }
        }

    private inner class OutParam(val n: Int) {
        fun set(value: Long) {
            when (paramMode(n)) {
                POSITION -> memory[memory[ip + n]] = value
                IMMEDIATE -> status = OutParamWithImmediateMode(ip, fetchOpcode(), n, memory)
                RELATIVE -> memory[memory[ip + n] + relativeOffset] = value
            }
        }
    }

    data class Memory(private val data: MutableMap<Long, Long>) {
        constructor(memory: Memory) : this(memory.data.toMutableMap())
        constructor(initialValues: List<Long>) : this(mutableMapOf<Long, Long>().apply {
            initialValues.forEachIndexed { index, value -> put(index.toLong(), value) }
        })

        operator fun get(address: Long): Long {
            return data[address] ?: 0
        }

        operator fun set(address: Long, value: Long) {
            data[address] = value
        }

        override fun toString(): String = data.toString()
    }

    private enum class ParameterMode { IMMEDIATE, POSITION, RELATIVE }

    private enum class Instruction(private val opcode: Int, private val action: IntcodeInterpreter.() -> Unit) {
        ADD(opcode = 1, action = { outParam(3).set(inParam(1) + inParam(2)).also { ip += 4 } }),
        MUL(opcode = 2, action = { outParam(3).set(inParam(1) * inParam(2)).also { ip += 4 } }),
        IN(opcode = 3, action = {
            if (inputs.isEmpty()) {
                status = InInstructionWithoutInput(ip = ip, dump = memory)
            } else outParam(1).set(inputs.removeAt(0)).also { ip += 2 }
        }),
        OUT(opcode = 4, action = {
            status = EmittingOutput(output = inParam(1))
            ip += 2
        }),
        JUMP_TRUE(opcode = 5, action = {
            if (inParam(1) != 0L) ip = inParam(2)
            else ip += 3
        }),
        JUMP_FALSE(opcode = 6, action = {
            if (inParam(1) == 0L) ip = inParam(2)
            else ip += 3
        }),
        LESS_THAN(opcode = 7, action = { outParam(3).set(if (inParam(1) < inParam(2)) 1 else 0).also { ip += 4 } }),
        EQUALS(opcode = 8, action = { outParam(3).set(if (inParam(1) == inParam(2)) 1 else 0).also { ip += 4 } }),
        ADJUST_RELATIVE_OFFSET(opcode = 9, action = { relativeOffset += inParam(1); ip += 2 }),
        HALT(opcode = 99, action = { status = Halted });

        fun execute(interpreter: IntcodeInterpreter) = action(interpreter)

        companion object {
            fun fromOpcode(opcode: Int) = values().firstOrNull { it.opcode == opcode }
        }
    }

    sealed class InterpreterStatus {
        object Running : InterpreterStatus()
        object Halted : InterpreterStatus()
        data class EmittingOutput(val output: Long) : InterpreterStatus()

        sealed class InterpreterError : InterpreterStatus() {
            data class OutParamWithImmediateMode(val ip: Long, val opcode: Int, val n: Int, val dump: Memory) : InterpreterError()
            data class UnknownOpcode(val ip: Long, val opcode: Int, val dump: Memory) : InterpreterError()
            data class InInstructionWithoutInput(val ip: Long, val dump: Memory) : InterpreterError()
        }
    }

    fun run(): ProgramResult {
        if (status is EmittingOutput) status = Running

        while (true) {
            when (val status = status) {
                Running -> fetchInstruction()?.execute(this)
                is EmittingOutput -> return GeneratedOutput(status.output)
                Halted -> return Finished(finalState = memory)
                is InterpreterError -> return ExecutionError(error = status)
            }
        }
    }

    fun dump() = memory

    sealed class ProgramResult {
        data class ExecutionError(val error: InterpreterError) : ProgramResult() {
            fun getErrorMessage(): String = when (error) {
                is OutParamWithImmediateMode -> "Found outparam in immediate mode [param #${error.n} in opcode ${error.opcode} at ${error.ip}]. Full dump: ${error.dump}"
                is UnknownOpcode -> "Unknown opcode ${error.opcode} at ${error.ip} in ${error.dump}"
                is InInstructionWithoutInput -> "Found IN instruction (opcode 3) at ${error.ip}, but the input was not provided to the interpreter. Full dump: ${error.dump}"
            }
        }

        data class GeneratedOutput(val output: Long) : ProgramResult()
        data class Finished(val finalState: Memory) : ProgramResult()
    }

    companion object {
        fun parseProgram(input: String) = input.split(",").map { it.toLong() }

        fun runProgram(programInput: String, input: Long) = runProgram(programInput, listOf(input))

        fun runProgram(
            programInput: String,
            inputs: List<Long> = emptyList(),
            transformProgram: (List<Long>) -> List<Long> = { it }
        ): List<Long> {
            val interpreter = IntcodeInterpreter(parseProgram(programInput).let(transformProgram)).apply { inputs.forEach { sendInput(it) } }
            val outputs = mutableListOf<Long>()

            while (true) {
                when (val result = interpreter.run()) {
                    is ExecutionError -> throw IllegalStateException(result.getErrorMessage())
                    is GeneratedOutput -> outputs += result.output
                    is Finished -> return outputs
                }
            }
        }
    }
}
