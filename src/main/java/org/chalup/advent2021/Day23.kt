package org.chalup.advent2021

import org.chalup.advent2021.Day5.rangeTo
import org.chalup.utils.Point
import org.chalup.utils.manhattanDistance
import java.util.PriorityQueue

object Day23 {
    private val FOLDED_MAP_LINES = """
        #############
        #...........#
        ###.#.#.#.###
          #D#C#B#A#  
          #D#B#A#C#  
          #.#.#.#.#  
          #########  
    """.trimIndent().lines()

    private val HALLWAY_XS = setOf(1, 2, 4, 6, 8, 10, 11)

    fun task1(input: List<String>): Long = input
        .let(this::parseInitialPosition)
        .let(this::calculateOptimalEnergyCostForAmphipodsRearrangement)

    fun task2(input: List<String>): Long = input
        .let {
            it.toMutableList().apply {
                add(3, FOLDED_MAP_LINES[3])
                add(4, FOLDED_MAP_LINES[4])
            }
        }
        .let(this::parseInitialPosition)
        .let { this.calculateOptimalEnergyCostForAmphipodsRearrangement(it, roomSize = 4) }

    private fun calculateOptimalEnergyCostForAmphipodsRearrangement(initialPosition: Position, roomSize: Int = 2): Long {
        val processedLocations = mutableSetOf<Map<Point, Char>>()
        val queue = PriorityQueue<Position>(compareBy { it.energyUsed }).apply {
            add(initialPosition)
        }

        while (queue.isNotEmpty()) {
            val position = queue.poll()

            if (!processedLocations.add(position.amphipodsLocations)) continue

            if (position.isFinal()) return position.energyUsed

            fun Point.isClear() = this !in position.amphipodsLocations

            for ((point, amphipod) in position.amphipodsLocations) {
                // if amphipod is in the room, try to move it to the hallway, otherwise, try to move it
                // to the correct room

                fun enqueueMoveTo(to: Point) = position
                    .move(from = point, to = to)
                    .takeUnless { it.amphipodsLocations in processedLocations }
                    ?.run { queue.add(this) }

                if (point.y > 1) {
                    // check if the ampiphod is already sitting in the correct room
                    if (point.x == roomXForChar(amphipod)) {
                        val shouldStay = (point.y rangeTo 1 + roomSize).all {
                            position.amphipodsLocations[Point(point.x, it)] == amphipod
                        }

                        if (shouldStay) continue
                    }

                    val isRoomExitClear = (point.y rangeTo 1).all { y ->
                        y == point.y || Point(point.x, y).isClear()
                    }

                    // check if the amphipod is not blocked by other amphipod in the same room
                    if (isRoomExitClear) {
                        val ranges = listOf(
                            point.x rangeTo 1,
                            point.x rangeTo 11,
                        )

                        for (range in ranges) {
                            for (x in range) {
                                val hallwayPoint = Point(x, 1)

                                if (!hallwayPoint.isClear()) break
                                if (hallwayPoint.x in HALLWAY_XS) enqueueMoveTo(hallwayPoint)
                            }
                        }
                    }
                } else {
                    val roomX = roomXForChar(amphipod)

                    val isHallwayClear = (point.x rangeTo roomX).all { x ->
                        x == point.x || Point(x, 1).isClear()
                    }

                    if (!isHallwayClear) continue

                    for (y in (1 + roomSize rangeTo 2)) {
                        val roomTile = Point(roomX, y)

                        when (position.amphipodsLocations[roomTile]) {
                            amphipod -> continue
                            null -> {
                                enqueueMoveTo(roomTile)
                                break
                            }
                            else -> break
                        }
                    }
                }
            }
        }

        throw IllegalStateException()
    }

    private fun parseInitialPosition(input: List<String>) = Position(
        amphipodsLocations = buildMap {
            input.forEachIndexed { y, line ->
                line.forEachIndexed { x, c ->
                    if (c.isLetter()) {
                        put(Point(x, y), c)
                    }
                }
            }
        }
    )

    private data class Position(
        val amphipodsLocations: Map<Point, Char>,
        val energyUsed: Long = 0
    ) {
        fun isFinal() = amphipodsLocations.all { (point, char) ->
            point.y > 1 && point.x == roomXForChar(char)
        }

        fun move(from: Point, to: Point) = Position(
            amphipodsLocations = amphipodsLocations
                .toMutableMap()
                .apply { put(to, remove(from)!!) },
            energyUsed = energyUsed + (manhattanDistance(from, to) * energyPerMove(amphipodsLocations.getValue(from)))
        )
    }

    private fun energyPerMove(c: Char) = when (c) {
        'A' -> 1
        'B' -> 10
        'C' -> 100
        'D' -> 1000
        else -> throw IllegalArgumentException("energy cost for $c")
    }

    private fun roomXForChar(c: Char) = 3 + (c - 'A') * 2
}
