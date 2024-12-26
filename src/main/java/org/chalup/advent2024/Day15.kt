package org.chalup.advent2024

import org.chalup.utils.Direction
import org.chalup.utils.Point
import org.chalup.utils.plus
import org.chalup.utils.textBlocks

object Day15 {
    fun task1(input: List<String>): Int {
        val (mapBlock, directionsBlock) = textBlocks(input)

        val map = mutableMapOf<Point, Char>().apply {
            mapBlock.forEachIndexed { y, line ->
                line.forEachIndexed { x, c ->
                    put(Point(x, y), c)
                }
            }
        }

        var robot = map
            .entries
            .first { (_, c) -> c == '@' }
            .key

        val directions = directionsBlock
            .flatMap { line ->
                line.map {
                    when (it) {
                        '^' -> Direction.U
                        'v' -> Direction.D
                        '<' -> Direction.L
                        '>' -> Direction.R
                        else -> throw IllegalArgumentException("$it")
                    }
                }
            }

        fun findFreeTile(origin: Point, direction: Direction): Point? {
            var tile = origin
            do {
                tile += direction.vector
            } while (tile in map && map[tile] == 'O')

            return tile.takeIf { map[it] == '.' }
        }

        for (d in directions) {
            val freeTile = findFreeTile(robot, d)

            if (freeTile != null) {
                check(map[freeTile] == '.')

                var tile = robot
                do {
                    tile += d.vector
                    map[tile] = 'O'
                } while (tile != freeTile)

                map[robot] = '.'
                robot += d.vector
                map[robot] = '@'
            }
        }

        return map
            .filterValues { it == 'O' }
            .keys
            .sumOf { (x, y) -> y * 100 + x }
    }
}