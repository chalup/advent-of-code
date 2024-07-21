package org.chalup.advent2019

import org.chalup.advent2018.cycleNext
import org.chalup.advent2018.cyclePrev
import org.chalup.utils.Direction
import org.chalup.utils.Point
import org.chalup.utils.plus

object Day17 {
    fun task1(input: List<String>): Int {
        val output = IntcodeInterpreter.runProgram(input.single())

        val map = output
            .joinToString(separator = "") { Char(it.toInt()).toString() }
            .split("\n")

        val intersections = buildSet {
            map.forEachIndexed { y, line ->
                line.forEachIndexed { x, char ->
                    if (char == '#' && Direction.entries.all { map.getOrNull(y + it.vector.dy)?.getOrNull(x + it.vector.dx) == '#' })
                        add(Point(x, y))
                }
            }
        }

        return intersections.sumOf { (x, y) -> x * y }
    }

    fun task2(input: List<String>): Long {
        val output = IntcodeInterpreter.runProgram(input.single())

        val map = output
            .joinToString(separator = "") { Char(it.toInt()).toString() }
            .split("\n")

        operator fun List<String>.get(point: Point) = this.getOrNull(point.y)?.getOrNull(point.x)

        val initialRobotPosition = map
            .flatMapIndexed { y, line ->
                line.mapIndexedNotNull { x, char ->
                    when (char) {
                        '>', '<', 'v', '^' -> Point(x, y)
                        else -> null
                    }
                }
            }
            .single()

        val firstScaffoldTileDirection = Direction
            .entries
            .single { d -> map[initialRobotPosition + d.vector] == '#' }

        val initialRobotDirection = when (val robot = map[initialRobotPosition]) {
            '>' -> Direction.R
            '<' -> Direction.L
            'v' -> Direction.D
            '^' -> Direction.U
            else -> throw IllegalStateException("Unexpected robot tile $robot at $initialRobotPosition")
        }

        val headingCorrection = listOf(
            generateSequence(initialRobotDirection) { it.takeUnless { it == firstScaffoldTileDirection }?.cycleNext() }
                .drop(1)
                .joinToString(separator = "") { "R" },
            generateSequence(initialRobotDirection) { it.takeUnless { it == firstScaffoldTileDirection }?.cyclePrev() }
                .drop(1)
                .joinToString(separator = "") { "L" }
        ).minBy { it.length }

        val route = buildString {
            append(headingCorrection)

            var currentState = initialRobotPosition to firstScaffoldTileDirection
            while (true) {
                val (p, d) = currentState
                if (map[p + d.vector] == '#') {
                    append('F')
                    currentState = (p + d.vector) to d
                } else if (map[p + d.cycleNext().vector] == '#') {
                    append('R')
                    currentState = p to d.cycleNext()
                } else if (map[p + d.cyclePrev().vector] == '#') {
                    append('L')
                    currentState = p to d.cyclePrev()
                } else {
                    break
                }
            }
        }

        fun shortenSegment(segment: String) = buildString {
            var forwardCount = 0

            fun appendForwardSteps() {
                if (forwardCount > 0) {
                    append(forwardCount.toString())
                    append(',')
                    forwardCount = 0
                }
            }

            segment.forEach {
                when (it) {
                    'F' -> forwardCount++
                    else -> {
                        appendForwardSteps()
                        append(it)
                        append(',')
                    }
                }
            }

            appendForwardSteps()
        }.trim(',')

        fun Char.isSimpleCommand() = when (this) {
            'L', 'R', 'F' -> true
            else -> false
        }

        fun possibleSegments(route: String) = (1..route.length)
            .asSequence()
            .map { route.trim { c -> !c.isSimpleCommand() }.take(it) }
            .filter { segment -> segment.all { it.isSimpleCommand() } }
            .takeWhile { shortenSegment(it).length <= 20 }
            .toList()
            .reversed()

        possibleSegments(route).forEach { segmentA ->
            val routeA = route.replace(segmentA, "A")

            possibleSegments(routeA).forEach { segmentB ->
                val routeAB = routeA.replace(segmentB, "B")

                possibleSegments(routeAB).forEach { segmentC ->
                    val routeABC = routeAB.replace(segmentC, "C")
                    val mainProgram = routeABC.toList().joinToString(",")

                    if (routeABC.all { it in 'A'..'C' } && mainProgram.length <= 20) {
                        val fullProgram = buildString {
                            appendLine(mainProgram)
                            appendLine(shortenSegment(segmentA))
                            appendLine(shortenSegment(segmentB))
                            appendLine(shortenSegment(segmentC))
                            appendLine("n")
                        }.map { it.code.toLong() }

                        return IntcodeInterpreter
                            .runProgram(input.single(), fullProgram) {
                                it.toMutableList().apply { set(0, 2) }
                            }
                            .last()
                    }
                }
            }
        }

        throw IllegalStateException("Could not find the solution :(")
    }
}