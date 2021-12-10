package org.chalup.advent2021

object Day6 {
    fun task1(input: String): Long = simulate(input, days = 80)
    fun task2(input: String): Long = simulate(input, days = 256)

    private fun simulate(input: String, days: Int): Long {
        val initialState = input
            .splitToSequence(',')
            .groupingBy { it.toInt() }
            .eachCount()
            .mapValues { (_, count) -> count.toLong() }

        val generations = generateSequence(initialState) { counts ->
            counts
                .mapKeysTo(mutableMapOf()) { (fishTimer, _) -> fishTimer - 1 }
                .apply {
                    val breedingFishCount = remove(-1)

                    if (breedingFishCount != null) {
                        put(6, getOrDefault(6, 0) + breedingFishCount)
                        put(8, breedingFishCount)
                    }
                }
        }

        val finalState = generations
            .drop(days)
            .take(1).single()

        return finalState.values.sum()
    }
}

