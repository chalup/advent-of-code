package org.chalup.advent2022

import org.chalup.advent2018.lcm
import org.chalup.utils.Direction
import org.chalup.utils.Point
import org.chalup.utils.Vector
import org.chalup.utils.manhattanDistance
import org.chalup.utils.plus
import org.chalup.utils.times
import java.util.PriorityQueue

object Day24 {
    fun task1(input: List<String>) = input
        .let(::BlizzardMap)
        .let { map -> navigate(map, startingPoint = map.entrance, destination = map.exit, startingStep = 0) }

    fun task2(input: List<String>) = input
        .let(::BlizzardMap)
        .let { map ->
            val timeToReachTheExit = navigate(map, startingPoint = map.entrance, destination = map.exit, startingStep = 0)
            val timeToGoBackToTheEntrance = navigate(map, startingPoint = map.exit, destination = map.entrance, startingStep = timeToReachTheExit)
            val timeToReachTheExitAgain = navigate(map, startingPoint = map.entrance, destination = map.exit, startingStep = timeToGoBackToTheEntrance)

            timeToReachTheExitAgain
        }

    private fun navigate(map: BlizzardMap, startingPoint: Point, destination: Point, startingStep: Int): Int {
        data class State(val step: Int, val position: Point) {
            val distanceToDestination = manhattanDistance(position, destination)

            fun move(direction: Direction?) = State(
                step = step + 1,
                position = position + (direction?.vector ?: Vector(0, 0)),
            )
        }

        val queue = PriorityQueue<State>(compareBy<State> { it.step }.thenBy { it.distanceToDestination }).apply {
            add(State(step = startingStep, position = startingPoint))
        }

        val possibleMoves = buildList<Direction?> {
            add(null)
            addAll(Direction.values())
        }

        val visitedStates = mutableSetOf<Pair<Int, Point>>(0 to map.entrance)
        while (queue.isNotEmpty()) {
            val state = queue.remove()

            if (state.distanceToDestination == 0) return state.step

            possibleMoves.mapNotNullTo(queue) { d ->
                state
                    .move(d)
                    .takeIf { map.isPassable(it.position, it.step) }
                    ?.takeIf { visitedStates.add((it.step % map.stepMod) to it.position) }
            }
        }

        throw IllegalStateException("Couldn't navigate through the blizzard!")
    }
}

private class BlizzardMap(private val data: List<String>) {
    private val blizzardsByDirection: Map<Direction, Set<Point>> = Direction.values().associateWith { d ->
        buildSet {
            data.forEachIndexed { y, line ->
                line.forEachIndexed { x, c ->
                    if (c == d.blizzardChar) {
                        add(Point(x - 1, y - 1))
                    }
                }
            }
        }
    }

    val entrance = Point(0, -1)
    val exit = Point(data.last().lastIndex - 2, data.lastIndex - 1)

    fun isPassable(p: Point, step: Int): Boolean {
        if (p == entrance) return true
        if (p == exit) return true
        if (p.x !in 0 until blizzardsWidth) return false
        if (p.y !in 0 until blizzardsHeight) return false

        for ((d, blizzards) in blizzardsByDirection) {
            if ((p + (d.vector * (-step))).wrapToBlizzardArea() in blizzards) return false
        }

        return true
    }

    private val blizzardsWidth = data.first().length - 2
    private val blizzardsHeight = data.size - 2
    val stepMod = lcm(blizzardsWidth, blizzardsHeight)
    private fun Point.wrapToBlizzardArea() = Point(fixIndex(x, blizzardsWidth), fixIndex(y, blizzardsHeight))

    private fun fixIndex(index: Int, mod: Int) =
        if (index >= 0) index % mod
        else {
            (index + ((-index / mod) + 1) * mod) % mod
        }

    private val Direction.blizzardChar: Char
        get() = when (this) {
            Direction.U -> '^'
            Direction.R -> '>'
            Direction.D -> 'v'
            Direction.L -> '<'
        }
}
