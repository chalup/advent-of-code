package org.chalup.advent2015

object Day17 {
    fun task1(input: List<String>) = possibleContainersCombination(input).size
    fun task2(input: List<String>) = possibleContainersCombination(input)
        .let { combinations ->
            val minimumNumberOfContainers = combinations.minOf { it.size }
            combinations.count { it.size == minimumNumberOfContainers }
        }

    private fun possibleContainersCombination(input: List<String>): Set<Set<Int>> {
        val containers = input.map(String::toInt).withIndex().associate { (i, value) -> i to value }

        fun possibleCombinations(usedContainers: Set<Int>): Set<Set<Int>> {
            val eggnogAmount = 150 - usedContainers.sumOf(containers::getValue)

            return if (eggnogAmount == 0) {
                setOf(usedContainers)
            } else {
                containers
                    .filterKeys { it !in usedContainers }
                    .filterValues { it <= eggnogAmount }
                    .keys
                    .flatMapTo(mutableSetOf()) { possibleCombinations(usedContainers + it) }
            }
        }

        return possibleCombinations(emptySet())
    }
}
