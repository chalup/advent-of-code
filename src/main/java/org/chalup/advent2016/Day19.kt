package org.chalup.advent2016

object Day19 {
    fun task1(input: List<String>): Int {
        var elves = List(input.first().toInt()) { it + 1 }

        while (elves.size > 1) {
            val removeFirstElfAfterwards = elves.size % 2 == 1

            elves = elves.filterIndexedTo(mutableListOf()) { i, _ -> i % 2 == 0 }
                .apply { if (removeFirstElfAfterwards) removeFirst() }
        }

        return elves.single()
    }

    fun task2(input: List<String>): Int {
        val count = input.first().toInt()
        val elves = MutableList(count) { it + 1 }

        var currentIndex = 0
        while (elves.size > 1) {
            println("$currentIndex: ${elves.size}")
            val eliminatedElf = (currentIndex + elves.size / 2) % elves.size
            elves.removeAt(eliminatedElf)
            currentIndex = (currentIndex + (if (eliminatedElf > currentIndex) 1 else 0)) % elves.size
        }

        return elves.single()
    }
}
