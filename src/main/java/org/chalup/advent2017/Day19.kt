package org.chalup.advent2017

import org.chalup.utils.Direction
import org.chalup.utils.Point
import org.chalup.utils.plus

object Day19 {
    fun task1(map: List<String>) = traverseMap(map).first
    fun task2(map: List<String>) = traverseMap(map).second

    private fun traverseMap(map: List<String>): Pair<String, Int> {
        operator fun List<String>.get(point: Point) = this[point.y][point.x]

        var stepsTaken = 0

        return buildString {
            var position = Point(x = map.first().indexOf('|'), y = 0)
            var direction = Direction.D

            while (true) {
                stepsTaken++

                position += direction.vector
                direction = when (val tile = map[position]) {
                    '-', '|' -> direction
                    '+' -> when (direction) {
                        Direction.D, Direction.U -> listOf(Direction.R, Direction.L).single { d -> map[position + d.vector] != ' ' }
                        Direction.R, Direction.L -> listOf(Direction.U, Direction.D).single { d -> map[position + d.vector] != ' ' }
                    }

                    ' ' -> break
                    else -> direction.also { append(tile) }
                }
            }
        } to stepsTaken
    }
}