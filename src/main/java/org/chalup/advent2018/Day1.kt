package org.chalup.advent2018

object Day1 {
    fun calculateFinalFrequency(changes: List<String>) = changes.map { it.toInt() }.sum()

    fun detectCycle(changes: List<String>): Int = changes
        .map { it.toInt() }
        .let { frequencyChanges ->
            val observedFrequencies = mutableSetOf(0)
            var currentFrequency = 0

            frequencyChanges.cycle { change ->
                currentFrequency += change
                if (!observedFrequencies.add(currentFrequency)) return currentFrequency
            }
        }

    private inline fun <T> Collection<T>.cycle(block: (T) -> Unit): Nothing {
        if (isEmpty()) throw NoSuchElementException()

        while (true) {
            forEach(block)
        }
    }

    data class FrequencyChanges(val currentFrequency: Int = 0,
                                val previousFrequencies: Set<Int> = emptySet()) {
        operator fun plus(change: Int) = FrequencyChanges(
            currentFrequency + change,
            previousFrequencies + currentFrequency
        )
    }

    fun functionalDetectCycle(changes: List<String>): Int = changes
        .map { it.toInt() }
        .cycle()
        .scan(FrequencyChanges()) { frequencyChanges, change -> frequencyChanges + change }
        .asSequence()
        .dropWhile { it.currentFrequency !in it.previousFrequencies }
        .first()
        .currentFrequency
}
