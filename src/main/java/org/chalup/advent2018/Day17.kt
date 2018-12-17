package org.chalup.advent2018

import org.chalup.advent2018.Day17.Direction.LEFT
import org.chalup.advent2018.Day17.Direction.RIGHT
import org.chalup.advent2018.Day17.Tile.CLAY
import org.chalup.advent2018.Day17.Tile.EMPTY
import org.chalup.advent2018.Day17.Tile.RUNNING_WATER
import org.chalup.advent2018.Day17.Tile.STILL_WATER
import org.chalup.advent2018.Day17.WaterFlow.BLOCKED
import org.chalup.advent2018.Day17.WaterFlow.FLOWING
import org.chalup.advent2018.Day17.WaterFlow.FLOWS_OVER_THE_EDGE
import org.chalup.utils.Point
import org.chalup.utils.Rect
import org.chalup.utils.Vector
import org.chalup.utils.bounds
import org.chalup.utils.contains
import org.chalup.utils.plus
import org.chalup.utils.points

object Day17 {
    private val veinRegex = """(?:(.)=((\d+)\.\.(\d+)|\d+)(?:, )?)""".toRegex()
    private val SPRING = Point(500, 0)

    private fun parseInput(input: List<String>) = input
        .map { line ->
            val constraints = veinRegex
                .findAll(line)
                .map { it.groupValues }
                .map { (_, dimension, fullRange, rangeStart, rangeEnd) ->
                    val range =
                        if (rangeStart.isNotBlank() && rangeEnd.isNotBlank()) rangeStart to rangeEnd
                        else fullRange to fullRange

                    dimension to range
                }
                .toMap()

            val (minX, maxX) = constraints.getValue("x")
            val (minY, maxY) = constraints.getValue("y")

            Rect(minX.toInt(), minY.toInt(), maxX.toInt(), maxY.toInt())
        }

    fun parseWorld(input: List<String>) =
        parseInput(input)
            .flatMap { it.points() }
            .let { clayTiles ->
                World(clay = clayTiles.toSet(),
                      stillWater = emptySet(),
                      flowingWater = setOf(SPRING))
            }

    data class World(val clay: Set<Point>,
                     val stillWater: Set<Point>,
                     val flowingWater: Set<Point>) {
        val bounds = clay.bounds().let { (tl, br) ->
            // expand to account for overflowing of the leftmost/rightmost basins
            Rect(tl + Vector(-1, 0),
                 br + Vector(+1, 0))
        }

        operator fun get(x: Int, y: Int) = when (Point(x, y)) {
            in clay -> CLAY
            in stillWater -> STILL_WATER
            in flowingWater -> RUNNING_WATER
            else -> EMPTY
        }
    }

    enum class Tile {
        CLAY,
        STILL_WATER,
        RUNNING_WATER,
        EMPTY
    }

    fun World.print() {
        val drawingBounds = bounds
            .let { (tl, br) -> Rect(Point(tl.x, 0), br) } // expand to make the SPRING visible

        with(drawingBounds) {
            for (y in topLeft.y..bottomRight.y) {
                for (x in topLeft.x..bottomRight.x) {
                    val tile = when (Point(x, y)) {
                        SPRING -> '+'
                        in clay -> '#'
                        in stillWater -> '~'
                        in flowingWater -> '|'
                        else -> '.'
                    }
                    print(tile)
                }
                println()
            }
        }
    }

    private enum class Direction(val dx: Int) {
        LEFT(-1),
        RIGHT(+1)
    }

    private enum class WaterFlow {
        BLOCKED,
        FLOWS_OVER_THE_EDGE,
        FLOWING
    }

    fun simulate(startingWorld: World) = generateSequence(startingWorld) { world ->
        val newFlowingWater = mutableSetOf<Point>()
        val newStillWater = world.stillWater.toMutableSet()

        for ((x, y) in world.flowingWater) {
            when (world[x, y + 1]) {
                CLAY, STILL_WATER -> {
                    fun testWaterFlow(direction: Direction) = generateSequence(x) { it + direction.dx }
                        .map {
                            val state = when {
                                world[it, y] == CLAY -> BLOCKED
                                world[it, y] == STILL_WATER -> BLOCKED
                                world[it, y + 1] == EMPTY -> FLOWS_OVER_THE_EDGE
                                world[it, y + 1] == RUNNING_WATER -> FLOWS_OVER_THE_EDGE
                                else -> FLOWING
                            }

                            it to state
                        }
                        .dropWhile { (_, state) -> state == FLOWING }
                        .first()
                        .let { (x, state) ->
                            val flowsUntil = x - (if (state == BLOCKED) direction.dx else 0)
                            flowsUntil to state
                        }

                    val (leftX, leftState) = testWaterFlow(LEFT)
                    val (rightX, rightState) = testWaterFlow(RIGHT)
                    val water = (leftX..rightX).map { Point(it, y) }

                    if (leftState == BLOCKED && rightState == BLOCKED) {
                        newStillWater.addAll(water)
                    } else {
                        newFlowingWater.addAll(water)
                    }
                }
                RUNNING_WATER -> newFlowingWater.add(Point(x, y))
                EMPTY -> {
                    newFlowingWater.add(Point(x, y))
                    if (y != world.bounds.bottom) {
                        newFlowingWater.add(Point(x, y + 1))
                    }
                }
            }
        }

        World(world.clay,
              newStillWater,
              newFlowingWater).takeIf { it != world }
    }

    fun part1(input: List<String>) =
        simulate(parseWorld(input))
            .last()
            .let { world -> (world.flowingWater + world.stillWater).count { it in world.bounds } }

    fun part2(input: List<String>) =
        simulate(parseWorld(input))
            .last()
            .let { world -> world.stillWater.size }
}