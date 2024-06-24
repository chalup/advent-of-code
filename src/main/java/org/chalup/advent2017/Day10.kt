package org.chalup.advent2017

object Day10 {
    fun task1(input: List<String>): Int = listOrder(input.single().split(",").map(String::toInt))
        .let { (first, second) -> first * second }

    fun task2(input: List<String>): String = knotHash(input.single())

    fun knotHash(input: String) = listOrder(input.map { it.code }, hashingRounds = 64)
        .chunked(16) { it.reduce(Int::xor) }
        .joinToString(separator = "") { it.toString(radix = 16).padStart(length = 2, padChar = '0') }

    private fun listOrder(inputLengths: List<Int>, hashingRounds: Int = 1): List<Int> {
        val initialList = List(256) { it }

        val lengths = buildList {
            repeat(hashingRounds) {
                addAll(inputLengths)
                if (hashingRounds > 1) {
                    addAll(arrayOf(17, 31, 73, 47, 23))
                }
            }
        }

        val reorderedList = lengths.foldIndexed(initialList) { index, acc, length ->
            (acc.take(length).reversed() + acc.drop(length))
                .let {
                    val i = (length + index) % initialList.size
                    it.drop(i) + it.take(i)
                }
        }

        val indexOfFirstItem = lengths.withIndex().sumOf { (i, l) -> i + l }
            .let { -it }
            .let { it % initialList.size }
            .let { it + initialList.size }

        return reorderedList.drop(indexOfFirstItem) + reorderedList.take(indexOfFirstItem)
    }
}