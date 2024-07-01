package org.chalup.advent2016

import java.util.LinkedList

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

        val elves = LinkedList<Int>().apply { repeat(count) { add(it + 1) } }
        var i = elves.listIterator()

        fun proceed() {
            i = (i.takeIf { it.hasNext() } ?: elves.listIterator()).also { it.next() }
        }

        repeat(elves.size / 2 + 1) { i.next() }

        repeat(count - 1) {
            i.remove()
            proceed()
            if (elves.size % 2 == 0) proceed()
        }

        return elves.single()
    }
}
