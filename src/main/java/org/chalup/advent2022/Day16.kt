package org.chalup.advent2022

import org.chalup.utils.matchRegex
import java.util.LinkedList
import java.util.PriorityQueue

object Day16 {
    fun task1(input: List<String>) = input
        .let(::prepareVolcanoData)
        .let { volcano ->
            data class TraversalState(
                val timeLeft: Int = 30,
                val location: String = "AA",
                val releasedPressure: Int = 0,
                val openValves: Set<String>
            )

            val queue = LinkedList<TraversalState>().apply { add(TraversalState(openValves = volcano.valves)) }

            var maxReleasedPressure = 0
            var maxQueueSize = 0

            while (queue.isNotEmpty()) {
                maxQueueSize = maxOf(queue.size, maxQueueSize)
                val state = queue.remove()

                maxReleasedPressure = maxOf(state.releasedPressure, maxReleasedPressure)


                for (valve in state.openValves) {
                    val timeLeftAfterGoingAndOpeningTheValve = state.timeLeft - volcano.getDistance(state.location, valve) - 1

                    if (timeLeftAfterGoingAndOpeningTheValve > 0) {
                        val newState = TraversalState(
                            location = valve,
                            timeLeft = timeLeftAfterGoingAndOpeningTheValve,
                            releasedPressure = state.releasedPressure + timeLeftAfterGoingAndOpeningTheValve * volcano.getFlowRate(valve),
                            openValves = state.openValves - valve
                        )

                        queue.add(newState)
                    }
                }
            }

            maxReleasedPressure to maxQueueSize
        }

    private fun prepareVolcanoData(input: List<String>): Volcano {
        data class Specs(
            val valveId: String,
            val flowRate: Int,
            val paths: List<String>,
        )

        val data = input.map {
            it.matchRegex("""Valve (.*?) has flow rate=(.*?); tunnels? leads? to valves? (.*)""") { (id, rate, paths) ->
                Specs(valveId = id, flowRate = rate.toInt(), paths = paths.split(", "))
            }
        }

        val flowRatesByNode = data.filter { it.flowRate > 0 }.associate { it.valveId to it.flowRate }

        val distances = buildMap {
                val nodes = flowRatesByNode.keys + "AA"
                val pathsByLocation = data.associate { it.valveId to it.paths }

                for (startingPoint in nodes) {
                    val visitedNodes = mutableSetOf(startingPoint)
                    val paths = PriorityQueue<List<String>>(compareBy { it.size }).apply { add(listOf(startingPoint)) }

                    while (paths.isNotEmpty()) {
                        val path = paths.remove()

                        put("$startingPoint${path.last()}", path.size - 1)
                        put("${path.last()}$startingPoint", path.size - 1)

                        for (nextStep in pathsByLocation.getValue(path.last())) {
                            if (visitedNodes.add(nextStep)) {
                                paths += (path + nextStep)
                            }
                        }
                    }
                }
            }

        return Volcano(flowRatesByNode, distances)
    }

    @JvmStatic
    fun main(args: Array<String>) {
        task1(
            """
            Valve AA has flow rate=0; tunnels lead to valves DD, II, BB
            Valve BB has flow rate=13; tunnels lead to valves CC, AA
            Valve CC has flow rate=2; tunnels lead to valves DD, BB
            Valve DD has flow rate=20; tunnels lead to valves CC, AA, EE
            Valve EE has flow rate=3; tunnels lead to valves FF, DD
            Valve FF has flow rate=0; tunnels lead to valves EE, GG
            Valve GG has flow rate=0; tunnels lead to valves FF, HH
            Valve HH has flow rate=22; tunnel leads to valve GG
            Valve II has flow rate=0; tunnels lead to valves AA, JJ
            Valve JJ has flow rate=21; tunnel leads to valve II
        """.trimIndent().lines()
        ).also(::println)
    }
}

private data class Volcano(
    private val flowRatesByValve: Map<String, Int>,
    private val distances: Map<String, Int>
) {
    val valves = flowRatesByValve.keys

    fun getDistance(from: String, to: String): Int = distances.getValue("$from$to")
    fun getFlowRate(valve: String) = flowRatesByValve.getValue(valve)
}

