package org.chalup.advent2016

object Day7 {
    fun task1(input: List<String>): Int = input
        .count(this::supportsTls)

    fun task2(input: List<String>): Int = input
        .count(this::supportsSsl)

    private fun supportsSsl(address: String): Boolean {
        val (outsideBrackets, insideBrackets) = address
            .split('[', ']')
            .withIndex()
            .partition { (index, _) -> index % 2 == 0 }

        val possibleBab = outsideBrackets
            .asSequence()
            .map { (_, part) -> part }
            .flatMap { it.windowedSequence(size = 3) }
            .filter { chunk -> chunk[0] == chunk[2] && chunk[0] != chunk[1] }
            .mapTo(mutableSetOf()) { chunk -> "${chunk[1]}${chunk[0]}${chunk[1]}" }

        if (possibleBab.isEmpty()) return false

        return insideBrackets
            .asSequence()
            .map { (_, part) -> part }
            .flatMap { it.windowedSequence(size = 3) }
            .any { it in possibleBab }
    }

    private fun supportsTls(address: String): Boolean {
        val (outsideBrackets, insideBrackets) = address
            .split('[', ']')
            .withIndex()
            .partition { (index, _) -> index % 2 == 0 }

        return insideBrackets.none { (_, part) -> containsAbba(part) } && outsideBrackets.any { (_, part) -> containsAbba(part) }
    }

    private fun containsAbba(string: String): Boolean = string
        .windowedSequence(size = 4)
        .any { chunk -> chunk[0] == chunk[3] && chunk[1] == chunk[2] && chunk[0] != chunk[1] }
}