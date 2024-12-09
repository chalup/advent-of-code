package org.chalup.advent2024

import org.chalup.utils.textBlocks

object Day5 {
    fun task1(input: List<String>): Int {
        val (rules, updates) = textBlocks(input)

        val printingRules = PrintingRules(rules)

        return updates
            .map { it.split(",").map(String::toInt) }
            .filter(printingRules::isValidUpdate)
            .sumOf { it.middleElement() }
    }

    fun task2(input: List<String>): Int {
        val (rules, updates) = textBlocks(input)

        val printingRules = PrintingRules(rules)

        return updates
            .map { it.split(",").map(String::toInt) }
            .filterNot(printingRules::isValidUpdate)
            .map(printingRules::fixupUpdate)
            .sumOf { it.middleElement() }
    }

    private class PrintingRules(rules: List<String>): Comparator<Int> {
        val successorsById = rules
            .asSequence()
            .map { it.split('|').map(String::toInt) }
            .map { (a, b) -> a to b }
            .groupBy(
                keySelector = { (a, _) -> a },
                valueTransform = { (_, b) -> b }
            )

        val predecessorsById = rules
            .asSequence()
            .map { it.split('|').map(String::toInt) }
            .map { (a, b) -> a to b }
            .groupBy(
                keySelector = { (_, b) -> b },
                valueTransform = { (a, _) -> a }
            )

        fun fixupUpdate(pages: List<Int>) = pages.sortedWith(this)

        fun isValidUpdate(pages: List<Int>): Boolean {
            val printedPages = mutableSetOf<Int>()
            val pagesThatMustNotAppear = mutableSetOf<Int>()
            for (page in pages) {
                if (page in pagesThatMustNotAppear) return false
                if (successorsById[page].orEmpty().any { it in printedPages }) return false

                printedPages.add(page)
                pagesThatMustNotAppear.addAll(predecessorsById[page].orEmpty())
            }

            return true
        }

        override fun compare(a: Int, b: Int): Int {
            if (a in predecessorsById[b].orEmpty()) return -1
            if (a in successorsById[b].orEmpty()) return +1
            if (b in predecessorsById[a].orEmpty()) return +1
            if (b in successorsById[a].orEmpty()) return -1
            return 0
        }
    }

    private fun List<Int>.middleElement() = this[size / 2]
}