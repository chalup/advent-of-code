package org.chalup.advent2017

import org.chalup.advent2018.cycleNext
import org.chalup.advent2018.cyclePrev
import org.chalup.utils.Direction
import org.chalup.utils.Point
import org.chalup.utils.opposite
import org.chalup.utils.plus

object Day22 {
    fun task1(input: List<String>): Int {
        val map = mutableSetOf<Point>().apply {
            input.forEachIndexed { y, line ->
                line.forEachIndexed { x, c ->
                    if (c == '#') add(Point(x, y))
                }
            }
        }
        var position = Point(x = input.first().length / 2, input.size / 2)
        var direction = Direction.U

        var infections = 0

        repeat(10_000) {
            if (position in map) {
                direction = direction.cycleNext()
                map.remove(position)
            } else {
                direction = direction.cyclePrev()
                map.add(position)
                infections++
            }

            position += direction.vector
        }

        return infections
    }

    private enum class NodeStatus {
        CLEAN,
        WEAKENED,
        INFECTED,
        FLAGGED
    }

    fun task2(input: List<String>): Int {
        val map = mutableMapOf<Point, NodeStatus>()
            .withDefault { NodeStatus.CLEAN }
            .apply {
                input.forEachIndexed { y, line ->
                    line.forEachIndexed { x, c ->
                        if (c == '#') put(Point(x, y), NodeStatus.INFECTED)
                    }
                }
            }
        var position = Point(x = input.first().length / 2, input.size / 2)
        var direction = Direction.U

        var infections = 0

        repeat(10_000_000) {
            when (map.getValue(position)) {
                NodeStatus.CLEAN -> {
                    direction = direction.cyclePrev()
                    map[position] = NodeStatus.WEAKENED
                }
                NodeStatus.WEAKENED -> {
                    map[position] = NodeStatus.INFECTED
                    infections++
                }
                NodeStatus.INFECTED -> {
                    direction = direction.cycleNext()
                    map[position] = NodeStatus.FLAGGED
                }
                NodeStatus.FLAGGED -> {
                    direction = direction.opposite
                    map.remove(position)
                }
            }

            position += direction.vector
        }

        return infections
    }
}