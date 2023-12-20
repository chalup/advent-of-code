package org.chalup.advent2023

import org.chalup.advent2018.lcm
import org.chalup.advent2023.Module.Broadcaster
import org.chalup.advent2023.Module.Conjunction
import org.chalup.advent2023.Module.FlipFlop
import java.util.LinkedList

object Day20 {
    fun task1(input: List<String>): Long = withCircuitry(input) { wires, modules ->
        simulationSequence(wires, modules)
            .drop(1)
            .take(1000)
            .map(ButtonClickResult::pulseStats)
            .fold(0L to 0L) { (accLows, accHighs), (lows, highs) ->
                (accLows + lows.size) to (accHighs + highs.size)
            }
            .let { (lows, highs) -> lows * highs }
    }

    fun task2(input: List<String>): Long = withCircuitry(input) { wires, modules ->
        // rx is connected to a single module
        val rxInput = wires.entries.single { (_, v) -> v == listOf("rx") }.key
        // this module is a conjunction module
        val rxInputInputs = (modules.getValue(rxInput) as Conjunction).memory.keys

        // when all its inputs are high, it will send low pulse to rx
        val inputCycleLengths = simulationSequence(wires, modules)
            .map(ButtonClickResult::pulseStats)
            .map { it.highs.toSet() }
            .runningFoldIndexed(rxInputInputs.associateWith<String, Int?> { null }) { i, acc, highs ->
                rxInputInputs.associateWith { input -> acc[input] ?: i.takeIf { input in highs } }
            }
            .mapNotNull { cycles -> cycles.values.filterNotNull() }
            .first { it.size == rxInputInputs.size }
            .map { it.toLong() }

        inputCycleLengths.reduce(::lcm)
    }

    private fun <T> withCircuitry(
        input: List<String>,
        block: (wires: Map<String, List<String>>, modules: Map<String, Module>) -> T
    ): T {
        val wires = input.associate { line ->
            val module = line.substringBefore(" -> ").trim('%', '&')
            val outputs = line.substringAfter(" -> ").split(", ")

            module to outputs
        }

        val modulesByName = input.associate { line ->
            val moduleSpec = line.substringBefore(" -> ")
            val moduleName = moduleSpec.trim('%', '&')

            val module = when {
                moduleName == Broadcaster.NAME -> Broadcaster
                moduleSpec.startsWith("%") -> FlipFlop(state = false)
                moduleSpec.startsWith("&") -> Conjunction(
                    memory = wires
                        .filterValues { outputs -> moduleName in outputs }
                        .mapValues { false }
                )

                else -> throw IllegalArgumentException(line)
            }

            moduleName to module
        }

        return block(wires, modulesByName)
    }
}

private fun simulationSequence(
    wires: Map<String, List<String>>,
    modules: Map<String, Module>,
) = generateSequence(ButtonClickResult(modules, PulseStats())) { (modulesState, _) ->
    onButtonClick(wires, modulesState)
}

private fun onButtonClick(
    wires: Map<String, List<String>>,
    modules: Map<String, Module>,
): ButtonClickResult {
    data class QueuedPulse(val source: String, val destination: String, val value: Boolean)

    val updatedModules = modules.toMutableMap()
    val lowPulses = mutableListOf<String>()
    val highPulses = mutableListOf<String>()

    val queue = LinkedList<QueuedPulse>().apply { add(QueuedPulse("button", Broadcaster.NAME, false)) }
    while (queue.isNotEmpty()) {
        val (src, dst, value) = queue.poll()

        (if (value) highPulses else lowPulses).add(src)

        val pulse = when (val module = updatedModules[dst]) {
            Broadcaster -> false
            is Conjunction -> {
                module.copy(memory = module.memory.toMutableMap().apply { put(src, value) })
                    .also { updatedModules[dst] = it }
                    .memory
                    .any { (_, input) -> !input }
            }

            is FlipFlop -> if (!value) {
                module.copy(state = !module.state)
                    .also { updatedModules[dst] = it }
                    .state
            } else {
                null
            }

            null -> null
        }

        if (pulse != null) {
            wires.getValue(dst).mapTo(queue) { QueuedPulse(dst, it, pulse) }
        }
    }

    return ButtonClickResult(updatedModules, PulseStats(lowPulses, highPulses))
}

private data class ButtonClickResult(
    val modules: Map<String, Module>,
    val pulseStats: PulseStats
)

private data class PulseStats(
    val lows: List<String> = emptyList(),
    val highs: List<String> = emptyList(),
)

private sealed interface Module {
    data object Broadcaster : Module {
        const val NAME = "broadcaster"
    }

    data class FlipFlop(val state: Boolean) : Module {
        override fun toString(): String = "%${if (state) 1 else 0}"
    }

    data class Conjunction(val memory: Map<String, Boolean>) : Module {
        override fun toString(): String = "&${memory.entries.sortedBy { (key, _) -> key }.joinToString(separator = "") { (_, value) -> if (value) "1" else "0" }}"
    }
}
