package org.chalup.advent2022

import org.chalup.advent2018.cycleNext
import org.chalup.advent2018.cyclePrev
import org.chalup.utils.Direction
import org.chalup.utils.Point
import org.chalup.utils.plus
import org.chalup.utils.textBlocks
import kotlin.math.sqrt

object Day22 {
    fun task1(input: List<String>) = input
        .let(::textBlocks)
        .let { (mapData, instructions) -> MonkeyMap(mapData, wrappedWorld(mapData)) to parseInstructions(instructions.single()) }
        .let { (map, instructions) ->
            instructions
                .scan(LocationAndBearing(map.startingPosition, Direction.R)) { lb, move ->
                    when (move) {
                        Move.TURN_LEFT -> lb.copy(direction = lb.direction.cyclePrev())
                        Move.TURN_RIGHT -> lb.copy(direction = lb.direction.cycleNext())
                        Move.FORWARD -> map.moveForward(lb)
                            .takeIf { (p, _) -> map.isPassable(p) }
                            ?: lb
                    }
                }
                .last()
                .let(::score)
        }
}

private fun parseInstructions(input: String) = """(L|R|\d+)""".toRegex()
    .findAll(input)
    .flatMap {
        when (it.value) {
            "L" -> sequenceOf(Move.TURN_LEFT)
            "R" -> sequenceOf(Move.TURN_RIGHT)
            else -> sequence { repeat(it.value.toInt()) { yield(Move.FORWARD) } }
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

private enum class Move {
    FORWARD, TURN_LEFT, TURN_RIGHT
}

private data class LocationAndBearing(
    val point: Point,
    val direction: Direction,
) {
    fun moveForward() = copy(point = point + direction.vector)
}

private fun wrappedWorld(data: List<String>): Map<LocationAndBearing, LocationAndBearing> {
    fun isValidTile(char: Char?) = char == '.' || char == '#'

    val sectorSize = data
        .sumOf { line -> line.count(::isValidTile) }
        .let { tilesCount ->
            sqrt(tilesCount / 6.0).toInt().also { check(tilesCount == it * it * 6) }
        }

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