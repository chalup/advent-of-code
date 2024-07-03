package org.chalup.advent2018

import org.chalup.advent2018.Day22.Cave.Companion.CAVE_MOUTH
import org.chalup.utils.Direction
import org.chalup.utils.Point
import org.chalup.utils.Rect
import org.chalup.utils.parseNumbers
import org.chalup.utils.plus
import org.chalup.utils.points
import java.util.PriorityQueue

object Day22 {
    enum class TerrainType(val riskLevel: Int) {
        ROCKY(0),
        WET(1),
        NARROW(2)
    }

    class Cave(
        private val depth: Int,
        val target: Point
    ) {

        private val geoIndexMap: MutableMap<Point, Int> = mutableMapOf()
        private val erosionLevels: MutableMap<Point, Int> = mutableMapOf()

        private fun geoIndex(location: Point): Int = geoIndexMap.getOrPut(location) {
            when {
                location == CAVE_MOUTH -> 0
                location == target -> 0
                location.y == 0 -> location.x * 16807
                location.x == 0 -> location.y * 48271
                else -> erosionLevel(Point(location.x, location.y - 1)) *
                        erosionLevel(Point(location.x - 1, location.y))
            }
        }

        private fun erosionLevel(location: Point): Int = erosionLevels.getOrPut(location) {
            (geoIndex(location) + depth) % 20183
        }

        operator fun get(location: Point) = TerrainType.entries[erosionLevel(location) % 3]

        companion object {
            val CAVE_MOUTH = Point(0, 0)
        }
    }

    fun task1(input: List<String>): Int = parseCave(input)
        .let { cave ->
            Rect(topLeft = CAVE_MOUTH, bottomRight = cave.target)
                .points()
                .sumOf { cave[it].riskLevel }
        }

    fun task2(input: List<String>): Int {
        val cave = parseCave(input)

        data class State(
            val position: Point,
            val equipment: Equipment
        )

        val initialState = State(CAVE_MOUTH, Equipment.TORCH)
        val targetState = State(cave.target, Equipment.TORCH)

        val queue = PriorityQueue<Pair<State, Int>>(compareBy { (_, time) -> time }).apply {
            add(initialState to 0)
        }
        val visited = mutableSetOf<State>()
        while (queue.isNotEmpty()) {
            val (state, time) = queue.poll()

            if (!visited.add(state)) continue
            if (state == targetState) return time

            Direction.entries
                .mapNotNullTo(queue) { direction ->
                    val p = state.position + direction.vector

                    when {
                        p.x < 0 -> null
                        p.y < 0 -> null
                        state.equipment isValidFor cave[p] -> state.copy(position = p) to time + 1
                        else -> null
                    }
                }

            Equipment.entries
                .mapNotNullTo(queue) { newGear ->
                    when {
                        newGear == state.equipment -> null
                        newGear isValidFor cave[state.position] -> state.copy(equipment = newGear) to time + 7
                        else -> null
                    }
                }
        }

        throw IllegalStateException("Could not find the solution :(")
    }

    private fun parseCave(input: List<String>): Cave {
        val inputData = input.map(::parseNumbers)
        val (depth) = inputData[0]
        val (x, y) = inputData[1]

        return Cave(depth, Point(x, y))
    }

    private infix fun Equipment.isValidFor(terrainType: TerrainType) = when (terrainType) {
        TerrainType.ROCKY -> this != Equipment.NEITHER
        TerrainType.WET -> this != Equipment.TORCH
        TerrainType.NARROW -> this != Equipment.CLIMBING_GEAR
    }

    enum class Equipment { TORCH, CLIMBING_GEAR, NEITHER }
}