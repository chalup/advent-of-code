package org.chalup.advent2023

import org.chalup.advent2018.lcm

object Day8 {
    fun task1(input: List<String>): Int = input
        .let(::parseMap)
        .let { desertMap -> movesSequence(desertMap, "AAA") }
        .first { (node, _) -> node == "ZZZ" }
        .let { (_, stepsTaken) -> stepsTaken }

    fun task2(input: List<String>): Long = input
        .let(::parseMap)
        .let { desertMap ->
            val startingNodes = desertMap.nodes.keys.filter { it.endsWith('A') }

            val routes = startingNodes.map {
                val steps = mutableMapOf<Step, Int>()
                var loop: Pair<Step, Int> = Step(it, 0) to 0

                for ((node, stepsTaken) in movesSequence(desertMap, it)) {
                    val step = Step(node, stepsTaken % desertMap.directions.size)
                    loop = step to stepsTaken
                    if (step in steps) break

                    steps[step] = stepsTaken
                }

                RouteInfo(steps, loop)
            }

            val cycles = routes.map { r ->
                // it seems there is exactly one Z node for each A node
                val zNode = r.steps.filterKeys { (node, _) -> node.endsWith('Z') }.entries.single().value
                val loopNode = r.steps.entries.single { (node, _) -> node == r.loop.first }.value
                val loopSize = zNode - loopNode + r.loop.second - zNode
                // it also seems that the loop size matches the number of steps to reach Z node for the first time
                check(zNode == loopSize)

                zNode.toLong()
            }

            cycles.reduce(::lcm)
        }

    private fun movesSequence(desertMap: DesertMap, startingNode: String) =
        generateSequence(startingNode to 0) { (node, stepsTaken) ->
            val direction = desertMap.directions[stepsTaken % desertMap.directions.size]

            val nextNode = desertMap.nodes.getValue(node).let { (left, right) ->
                when (direction) {
                    Direction.LEFT -> left
                    Direction.RIGHT -> right
                }
            }

            nextNode to (stepsTaken + 1)
        }
}

private enum class Direction(val symbol: Char) {
    LEFT('L'),
    RIGHT('R'),
}

private data class RouteInfo(
    val steps: Map<Step, Int>,
    val loop: Pair<Step, Int>
)

private data class Step(
    val node: String,
    val directionsOffset: Int,
)

private data class DesertMap(
    val directions: List<Direction>,
    val nodes: Map<String, Pair<String, String>>
)

private fun parseMap(input: List<String>): DesertMap {
    val directions = input.first().map { char -> Direction.values().first { it.symbol == char } }
    val nodes = input.drop(2).associate {
        val node = it.substringBefore('=').trim()
        val left = it.substringAfter('(').substringBefore(',').trim()
        val right = it.substringAfter(',').substringBefore(')').trim()

        node to (left to right)
    }

    return DesertMap(directions, nodes)
}