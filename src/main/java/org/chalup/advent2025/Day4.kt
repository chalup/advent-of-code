package org.chalup.advent2025

import org.chalup.utils.Point

object Day4 {
    fun task1(input: List<String>): Int {
        var result = 0

        input.forEachIndexed { row, line ->
            line.forEachIndexed { col, char ->
                if (char == '@') {
                    var neighbours = 0

                    for (dx in -1..1) {
                        for (dy in -1..1) {
                            if (dx == 0 && dy == 0) continue

                            if (input.getOrNull(row + dy)?.getOrNull(col + dx) == '@') {
                                neighbours++
                            }
                        }
                    }

                    if (neighbours < 4) {
                        result++
                    }
                }
            }
        }

        return result
    }

    fun task2(input: List<String>): Int {
        val rolls = mutableMapOf<Point, Int>().apply {
            input.forEachIndexed { row, line ->
                line.forEachIndexed { col, char ->
                    if (char == '@') {
                        var neighbours = 0

                        for (dx in -1..1) {
                            for (dy in -1..1) {
                                if (dx == 0 && dy == 0) continue

                                if (input.getOrNull(row + dy)?.getOrNull(col + dx) == '@') {
                                    neighbours++
                                }
                            }
                        }

                        put(Point(col, row), neighbours)
                    }
                }
            }
        }
        val initialRolls = rolls.size

        do {
            val removedRolls = rolls
                .filter { (_, neighbours) -> neighbours < 4 }
                .keys
                .onEach { rolls.remove(it) }

            for (r in removedRolls) {
                for (dx in -1..1) {
                    for (dy in -1..1) {
                        if (dx == 0 && dy == 0) continue

                        val neighbour = Point(r.x + dx, r.y + dy)
                        val previousNeighbourCount = rolls[neighbour]

                        if (previousNeighbourCount != null) {
                            rolls[neighbour] = previousNeighbourCount - 1
                        }
                    }
                }
            }
        } while (removedRolls.isNotEmpty())

        return initialRolls - rolls.size
    }
}