package org.chalup.advent2015

import org.chalup.advent2015.Day7.CircuitElement.BinaryOperator
import org.chalup.advent2015.Day7.CircuitElement.BinaryOperator.And
import org.chalup.advent2015.Day7.CircuitElement.BinaryOperator.LeftShift
import org.chalup.advent2015.Day7.CircuitElement.BinaryOperator.Or
import org.chalup.advent2015.Day7.CircuitElement.BinaryOperator.RightShift
import org.chalup.advent2015.Day7.CircuitElement.Input
import org.chalup.advent2015.Day7.CircuitElement.Input.Signal
import org.chalup.advent2015.Day7.CircuitElement.Input.Wire
import org.chalup.advent2015.Day7.CircuitElement.Not
import org.chalup.utils.match

@OptIn(ExperimentalUnsignedTypes::class)
object Day7 {
    sealed class CircuitElement {
        sealed class Input : CircuitElement() {
            data class Wire(val id: String) : Input()
            data class Signal(val value: UShort) : Input()
        }

        data class Not(val input: Input) : CircuitElement()

        sealed class BinaryOperator : CircuitElement() {
            abstract val left: Input
            abstract val right: Input

            data class And(override val left: Input, override val right: Input) : BinaryOperator()
            data class Or(override val left: Input, override val right: Input) : BinaryOperator()
            data class LeftShift(override val left: Input, override val right: Input) : BinaryOperator()
            data class RightShift(override val left: Input, override val right: Input) : BinaryOperator()
        }
    }

    data class Connection(val element: CircuitElement, val wire: Wire)

    private fun parseInput(data: String): Input = match(data) {
        pattern("""(\d+)""") { (numbers) -> Signal(numbers.toUShort()) }
        pattern("""([a-z]+)""") { (letters) -> Wire(letters) }
    }

    private fun parseElement(data: String): CircuitElement = match(data) {
        pattern("""NOT (.*?)""") { (input) -> Not(parseInput(input)) }
        pattern("""(.*?) AND (.*?)""") { (left, right) -> And(parseInput(left), parseInput(right)) }
        pattern("""(.*?) OR (.*?)""") { (left, right) -> Or(parseInput(left), parseInput(right)) }
        pattern("""(.*?) LSHIFT (.*?)""") { (left, right) -> LeftShift(parseInput(left), parseInput(right)) }
        pattern("""(.*?) RSHIFT (.*?)""") { (left, right) -> RightShift(parseInput(left), parseInput(right)) }
        parser { parseInput(it) }
    }

    private fun parseInstruction(instruction: String): Connection =
        """(.*?) -> (.*)"""
            .toRegex()
            .matchEntire(instruction)!!
            .destructured
            .let { (element, wireId) -> Connection(parseElement(element), Wire(wireId)) }


    private fun Connection.simulate(inputs: Map<Wire, Signal>): Pair<Wire, Signal>? {
        fun resolveBinaryInputs(operator: BinaryOperator): Pair<UShort, UShort>? {
            fun resolveInput(input: Input): UShort? = when (input) {
                is Wire -> inputs[input]?.value
                is Signal -> input.value
            }

            val left = resolveInput(operator.left) ?: return null
            val right = resolveInput(operator.right) ?: return null

            return left to right
        }

        val signal = when (element) {
            is Wire -> inputs[element]
            is Signal -> element
            is Not -> inputs[element.input]?.let { signal -> Signal(signal.value.inv()) }
            is BinaryOperator -> {
                resolveBinaryInputs(element)?.let { (left, right) ->
                    Signal(
                        when (element) {
                            is And -> left and right
                            is Or -> left or right
                            is LeftShift -> (left.toInt() shl right.toInt()).toUShort()
                            is RightShift -> (left.toInt() shr right.toInt()).toUShort()
                        }
                    )
                }
            }
        }

        return signal?.let { wire to it }
    }

    fun simulateCircuit(connections: List<Connection>): Map<Wire, Signal> = mutableMapOf<Wire, Signal>().apply {
        val connectionsToSimulate = connections.toMutableList()
        while (connectionsToSimulate.isNotEmpty()) {
            connectionsToSimulate
                .mapNotNull { it.simulate(this) }
                .ifEmpty { throw IllegalStateException("Ugh, I got stuck :(") }
                .also {
                    it
                        .map { (wire, _) -> wire }.toSet()
                        .also { simulatedWires ->
                            connectionsToSimulate.removeAll { connection -> connection.wire in simulatedWires }
                        }
                    putAll(it)
                }
        }
    }

    fun simulateInstructionsThenOverride(instructions: List<String>, override: Map<Wire, Wire>): Map<Wire, Signal> = instructions
        .map { parseInstruction(it) }
        .let { connections ->
            val simulation = simulateCircuit(connections)

            val newWiring = connections.map {
                when {
                    it.wire in override.keys -> Connection(simulation.getValue(override.getValue(it.wire)), it.wire)
                    else -> it
                }
            }

            simulateCircuit(newWiring)
        }

    fun simulateInstructions(instructions: List<String>): Map<Wire, Signal> = instructions
        .map { parseInstruction(it) }
        .let { simulateCircuit(it) }
}