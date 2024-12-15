package org.chalup.advent2024

object Day11 {
    fun task1(input: List<String>): Int = input
        .single()
        .split(" ")
        .sumOf { stoneSequence(it).drop(25).first().size }

    private fun stoneSequence(initialStone: String) = generateSequence(listOf(initialStone)) { stones ->
        buildList {
            for (stone in stones) {
                when {
                    stone == "0" -> add("1")
                    stone.length % 2 == 0 -> {
                        add(stone.take(stone.length / 2))
                        add(stone.drop(stone.length / 2).trimStart('0').padStart(1, padChar = '0'))
                    }
                    else -> add((stone.toLong() * 2024).toString())
                }
            }
        }
    }
}