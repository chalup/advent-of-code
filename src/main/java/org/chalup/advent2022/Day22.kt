package org.chalup.advent2022

import org.chalup.advent2018.cycleNext
import org.chalup.advent2018.cyclePrev
import org.chalup.utils.Direction
import org.chalup.utils.Point
import org.chalup.utils.opposite
import org.chalup.utils.plus
import org.chalup.utils.textBlocks
import kotlin.math.sqrt

object Day22 {
    fun task1(input: List<String>) = input
        .let(::textBlocks)
        .let { (mapData, instructions) -> MonkeyMap(mapData, wrappedWorld(mapData)) to parseInstructions(instructions.single()) }
        .let { (map, instructions) -> followTheMap(map, instructions) }
        .let(::score)

    fun task2(input: List<String>) = input
        .let(::textBlocks)
        .let { (mapData, instructions) ->
            val map = MonkeyMap(mapData, cubeWorld(mapData))
            val instructions = parseInstructions(instructions.single())

            path.clear()
            followTheMap(map, instructions)
        }
        .let(::score)
}

private fun cubeWorld(data: List<String>): Map<LocationAndBearing, LocationAndBearing> {
    val sectorSize = sectorSize(data)

    data class SectorEdge(val sx: Int, val sy: Int, val xs: IntProgression, val ys: IntProgression) {
        fun points() = sequence<Point> {
            for (x in xs) {
                for (y in ys) {
                    yield(Point(x + sx * sectorSize, y + sy * sectorSize))
                }
            }
        }
    }

    fun hEdge(sx: Int, sy: Int, xs: IntProgression, y: Int) = SectorEdge(sx, sy, xs, y..y)
    fun vEdge(sx: Int, sy: Int, x: Int, ys: IntProgression) = SectorEdge(sx, sy, x..x, ys)

    fun MutableMap<LocationAndBearing, LocationAndBearing>.connect(e1: SectorEdge, d1: Direction, e2: SectorEdge, d2: Direction) {
        e1.points().zip(e2.points()).forEach { (from, to) ->
            put(LocationAndBearing(from, d1), LocationAndBearing(to, d2))
            put(LocationAndBearing(to, d2.opposite), LocationAndBearing(from, d1.opposite))
        }
    }

    /**
     * .12
     * .3.
     * 45.
     * 6..
     */

    return buildMap {
        // 2 bl br -> 3 tr br -> cw
        connect(
            hEdge(2, 0, 0 until sectorSize, sectorSize - 1), Direction.D,
            vEdge(1, 1, sectorSize - 1, 0 until sectorSize), Direction.L,
        )

        // 6 tr br -> 5 bl br -> cw * 3
        connect(
            vEdge(0, 3, sectorSize - 1, 0 until sectorSize), Direction.R,
            hEdge(1, 2, 0 until sectorSize, sectorSize - 1), Direction.U,
        )

        // 2 tr br -> 5 br tr -> cw * 2
        connect(
            vEdge(2, 0, sectorSize - 1, 0 until sectorSize), Direction.R,
            vEdge(1, 2, sectorSize - 1, (0 until sectorSize).reversed()), Direction.L,
        )

        // 4 tr tl -> 3 bl tl -> cw
        connect(
            hEdge(0, 2, 0 until sectorSize, 0), Direction.U,
            vEdge(1, 1, 0, 0 until sectorSize), Direction.R,
        )

        // 4 tl bl -> 1 bl tl -> cw * 2
        connect(
            vEdge(0, 2, 0, 0 until sectorSize), Direction.L,
            vEdge(1, 0, 0, (0 until sectorSize).reversed()), Direction.R,
        )

        // 6 tl bl -> 1 tl tr -> cw * 3
        connect(
            vEdge(0, 3, 0, 0 until sectorSize), Direction.L,
            hEdge(1, 0, 0 until sectorSize, 0), Direction.D,
        )

        // 6 bl br -> 2 tl tr -> 0
        connect(
            hEdge(0, 3, 0 until sectorSize, sectorSize - 1), Direction.D,
            hEdge(2, 0, 0 until sectorSize, 0), Direction.D,
        )
    }
}

private val path = mutableListOf<LocationAndBearing>()

private fun followTheMap(map: MonkeyMap, instructions: Sequence<Move>) =
    instructions
        .scan(LocationAndBearing(map.startingPosition, Direction.R)) { lb, move ->
            path += lb

            when (move) {
                Move.TurnLeft -> lb.copy(direction = lb.direction.cyclePrev())
                Move.TurnRight -> lb.copy(direction = lb.direction.cycleNext())
                is Move.MoveForward -> {
                    var intermediate = lb
                    repeat(move.steps) {
                        intermediate = map.moveForward(intermediate)
                            .takeIf { (p, _) -> map.isPassable(p) }
                            ?.also { path += it }
                            ?: intermediate
                    }
                    intermediate
                }
            }
        }
        .last()

private fun parseInstructions(input: String) = """(L|R|\d+)""".toRegex()
    .findAll(input)
    .map {
        when (it.value) {
            "L" -> Move.TurnLeft
            "R" -> Move.TurnRight
            else -> Move.MoveForward(it.value.toInt())
        }
    }

private fun score(locationAndBearing: LocationAndBearing): Int {
    val (p, d) = locationAndBearing

    val rowScore = 1000 * (p.y + 1)
    val colScore = 4 * (p.x + 1)
    val dirScore = when (d) {
        Direction.U -> 3
        Direction.R -> 0
        Direction.D -> 1
        Direction.L -> 2
    }

    return rowScore + colScore + dirScore
}

sealed class Move {
    object TurnLeft : Move()
    object TurnRight : Move()
    data class MoveForward(val steps: Int) : Move()
}

private data class LocationAndBearing(
    val point: Point,
    val direction: Direction,
) {
    fun moveForward() = copy(point = point + direction.vector)
}

private fun isValidTile(char: Char?) = char == '.' || char == '#'

private fun sectorSize(data: List<String>) = data
    .sumOf { line -> line.count(::isValidTile) }
    .let { tilesCount ->
        sqrt(tilesCount / 6.0).toInt().also { check(tilesCount == it * it * 6) }
    }

private fun wrappedWorld(data: List<String>): Map<LocationAndBearing, LocationAndBearing> {
    val sectorSize = sectorSize(data)

    val width = data.maxOf { it.length }
    val height = data.size

    fun hasTile(point: Point) = data.getOrNull(point.y)?.getOrNull(point.x).let(::isValidTile)
    fun topLeft(sx: Int, sy: Int) = Point(sx * sectorSize, sy * sectorSize)
    fun hasSector(sx: Int, sy: Int) = hasTile(topLeft(sx, sy))
    fun edge(sx: Int, sy: Int, d: Direction) = when (d) {
        Direction.U -> topLeft(sx, sy).let { (x, y) -> (x until x + sectorSize).asSequence().map { Point(it, y) } }
        Direction.R -> topLeft(sx, sy).let { (x, y) -> (y until y + sectorSize).asSequence().map { Point(x + sectorSize - 1, it) } }
        Direction.D -> topLeft(sx, sy).let { (x, y) -> (x until x + sectorSize).asSequence().map { Point(it, y + sectorSize - 1) } }
        Direction.L -> topLeft(sx, sy).let { (x, y) -> (y until y + sectorSize).asSequence().map { Point(x, it) } }
    }

    return buildMap<LocationAndBearing, LocationAndBearing> {
        for (sy in 0 until (height / sectorSize)) {
            for (sx in 0 until (width / sectorSize)) {
                if (!hasSector(sx, sy)) continue

                for (d in Direction.values()) {
                    val (dx, dy) = d.vector

                    if (!hasSector(sx + dx, sy + dy)) {
                        for (p in edge(sx, sy, d)) {
                            val destination = generateSequence(p) { (it + d.vector).let { (x, y) -> Point((x + width) % width, (y + height) % height) } }
                                .drop(1)
                                .take(maxOf(width, height))
                                .first { hasTile(it) }

                            put(LocationAndBearing(p, d), LocationAndBearing(destination, d))
                        }
                    }
                }
            }
        }
    }
}

private class MonkeyMap(
    private val data: List<String>,
    private val connections: Map<LocationAndBearing, LocationAndBearing>
) {
    val startingPosition = Point(
        x = data.first().indexOf('.'),
        y = 0
    )

    fun isPassable(point: Point): Boolean = data
        .getOrNull(point.y)
        ?.getOrNull(point.x)
        .let { it == '.' }

    fun moveForward(locationAndBearing: LocationAndBearing): LocationAndBearing =
        connections[locationAndBearing] ?: locationAndBearing.moveForward()
}