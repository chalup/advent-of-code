package org.chalup.advent2016

import java.util.PriorityQueue

object Day11 {
    fun task1(input: List<String>): Int = stepsNeededToTakeEverythingToTheLastFloor(
        initialState = State(
            elevator = 0,
            floors = input.map(::parseDevices)
        )
    )

    fun task2(input: List<String>): Int = stepsNeededToTakeEverythingToTheLastFloor(
        initialState = State(
            elevator = 0,
            floors = input.map(::parseDevices)
                .toMutableList()
                .apply {
                    this[0].addAll(
                        listOf("elerium", "dilithium").flatMap {
                            listOf(Device.Generator(it), Device.Microchip(it))
                        }
                    )
                }
        )
    )

    private fun stepsNeededToTakeEverythingToTheLastFloor(initialState: State): Int {
        val visited = mutableSetOf<State>()
        val queue = PriorityQueue(
            compareBy<Pair<State, Int>> { (_, steps) -> steps }
                .thenByDescending { (state, _) -> state.floors.mapIndexed { i, devices -> (i + 1) * devices.size }.sum() }
        ).apply { add(initialState to 0) }

        while (queue.isNotEmpty()) {
            val (state, steps) = queue.poll()
            if (!visited.add(state)) continue

            if (state.floors.dropLast(1).all { it.isEmpty() }) return steps

            queue.addAll(state.possibleFutureStates().map { it to (steps + 1) })
        }

        throw IllegalStateException("Could not find the solution :(")
    }

    private fun State.possibleFutureStates() = floors[elevator]
        .let { devicesOnTheCurrentFloor ->
            devicesOnTheCurrentFloor
                .flatMapIndexed { i, device ->
                    buildList {
                        devicesOnTheCurrentFloor
                            .drop(i + 1)
                            .mapNotNullTo(this) { otherDevice ->
                                setOf(device, otherDevice).takeIf(::nothingGetsFried)
                            }

                        add(setOf(device))
                    }
                }
                .flatMap { devicesThatWillBeTaken ->
                    val devicesLeft = devicesOnTheCurrentFloor - devicesThatWillBeTaken
                    if (nothingGetsFried(devicesLeft)) {
                        listOf(elevator - 1, elevator + 1)
                            .mapNotNull { floor ->
                                floors
                                    .getOrNull(floor)
                                    ?.plus(devicesThatWillBeTaken)
                                    ?.takeIf(::nothingGetsFried)
                                    ?.let {
                                        State(
                                            elevator = floor,
                                            floors = floors.toMutableList().apply {
                                                this[elevator] = devicesLeft
                                                this[floor] = it
                                            }
                                        )
                                    }
                            }
                    } else {
                        emptyList()
                    }
                }
        }

    private fun nothingGetsFried(devices: Set<Device>): Boolean {
        if (devices.isEmpty()) return true
        if (devices.none { it is Device.Generator }) return true

        return devices
            .groupBy { it.element }
            .values
            .all { it.size == 2 || it.single() is Device.Generator }
    }

    private fun parseDevices(line: String) = line
        .substringAfter("contains")
        .split(" a ")
        .mapNotNullTo(mutableSetOf()) { text ->
            when {
                "microchip" in text -> Device.Microchip(element = text.substringBefore("-compatible").trim())
                "generator" in text -> Device.Generator(element = text.substringBefore("generator").trim())
                else -> null
            }
        }

    private sealed class Device {
        abstract val element: String

        data class Microchip(override val element: String) : Device()
        data class Generator(override val element: String) : Device()
    }

    private data class State(
        val elevator: Int,
        val floors: List<Set<Device>>
    )
}
