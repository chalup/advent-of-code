package org.chalup.advent2024

import org.chalup.utils.Point
import org.chalup.utils.Vector
import org.chalup.utils.parseNumbers
import org.chalup.utils.plus

private const val W = 101
private const val H = 103

object Day14 {
    fun task1(input: List<String>): Int {
        val robots = input.map {
            parseNumbers(it).let { (x, y, vx, vy) ->
                Robot(Point(x, y), Vector(vx, vy))
            }
        }.also { println(it) }

        return robots
            .map { robot ->
                generateSequence(robot.p) {
                    val (x, y) = it + robot.v

                    Point(
                        x = (x + W) % W,
                        y = (y + H) % H
                    )
                }.drop(100).first()
            }
            .filterNot { it.x == W / 2 || it.y == H / 2 }
            .groupingBy {
                (it.x < W / 2) to (it.y < H / 2)
            }
            .eachCount()
            .values
            .fold(1, Int::times)
    }

    fun task2(input: List<String>): Int {
        val initialRobots = input.map {
            parseNumbers(it).let { (x, y, vx, vy) ->
                Robot(Point(x, y), Vector(vx, vy))
            }
        }.also { println(it) }

        val positionsSequence = generateSequence(initialRobots) { robots ->
            robots.map { robot ->
                val (x, y) = robot.p + robot.v

                robot.copy(
                    p =
                    Point(
                        x = (x + W) % W,
                        y = (y + H) % H
                    )
                )
            }
        }

        return positionsSequence
            .withIndex()
            .first { (_, robots) ->
                robots
                    .groupBy { it.p.y }
                    .values
                    .map { lineRobots ->
                        val xs = lineRobots.mapTo(mutableSetOf()) { it.p.x }
                        buildString {
                            for (x in 0 until W) {
                                append(if (x in xs) 'X' else '.')
                            }
                        }
                    }
                    .any { "X".repeat(25) in it }
            }
            .let { (i, _) -> i }
    }
}

data class Robot(
    val p: Point,
    val v: Vector
)