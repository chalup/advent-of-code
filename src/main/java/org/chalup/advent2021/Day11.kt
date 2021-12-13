package org.chalup.advent2021

import org.chalup.utils.Point
import java.util.LinkedList

object Day11 {
    fun task1(input: List<String>): Int = input
        .let(this::parseMap)
        .let(this::simulate)
        .drop(1)
        .take(100)
        .sumOf { it.count { (_, energyLevel) -> energyLevel == 0 } }

    fun task2(input: List<String>): Int = input
        .let(this::parseMap)
        .let(this::simulate)
        .drop(1)
        .takeWhile { map -> map.values.any { it > 0 } }
        .count()
        .let { it + 1 }

    private fun simulate(initialState: Map<Point, Int>) = generateSequence(initialState) { map ->
        buildMap {
            map.mapValuesTo(this) { (_, energyLevel) -> energyLevel + 1 }

            val fullyChargedOctopuses =
                mapNotNullTo(mutableSetOf()) { (point, energyLevel) -> point.takeIf { energyLevel == 10 } }
            val pendingFlashes = LinkedList(fullyChargedOctopuses)

            while (pendingFlashes.isNotEmpty()) {
                val blink = pendingFlashes.poll()

                blink
                    .affectedTiles()
                    .filter { it in this }
                    .filterNot { it in fullyChargedOctopuses }
                    .forEach { point ->
                        val energyLevel = getValue(point)

                        put(point, energyLevel + 1)

                        if (energyLevel + 1 == 10) {
                            pendingFlashes.add(point)
                            fullyChargedOctopuses.add(point)
                        }
                    }
            }

            fullyChargedOctopuses.forEach { put(it, 0) }
        }
    }

    private fun Point.affectedTiles(): Sequence<Point> = sequence {
        for (dx in -1..1)
            for (dy in -1..1)
                if (dx != 0 || dy != 0)
                    yield(Point(x + dx, y + dy))
    }

    private fun parseMap(input: List<String>) = buildMap<Point, Int> {
        input
            .mapIndexed { row, line ->
                line.mapIndexed { col, char ->
                    put(Point(col, row), char - '0')
                }
            }
    }
}
