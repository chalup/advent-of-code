package org.chalup.advent2017

object Day5 {
    fun task1(input: List<String>) = input
        .mapTo(mutableListOf()) { it.toInt() }
        .let { maze ->
            generateSequence(0) { i ->
                if (i in maze.indices) {
                    val jumpOffset = maze[i]
                    maze[i] += 1
                    (i + jumpOffset)
                } else null
            }
        }
        .drop(1) // dropping the initial position
        .count()

    fun task2(input: List<String>) = input
        .mapTo(mutableListOf()) { it.toInt() }
        .let { maze ->
            generateSequence(0) { i ->
                if (i in maze.indices) {
                    val jumpOffset = maze[i]
                    if (jumpOffset >= 3) {
                        maze[i] -= 1
                    } else {
                        maze[i] += 1
                    }
                    (i + jumpOffset)
                } else null
            }
        }
        .drop(1) // dropping the initial position
        .count()

}