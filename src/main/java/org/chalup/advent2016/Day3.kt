package org.chalup.advent2016

object Day3 {
    fun task1(input: List<String>): Int = input
        .flatMap(this::parseNumbers)
        .chunked(3, this::listToTriple)
        .countValidTriangles()

    fun task2(input: List<String>): Int = input
        .flatMap(this::parseNumbers)
        .chunked(9)
        .flatMap { chunk ->
            sequence {
                repeat(3) { offset ->
                    yield(
                        listToTriple(
                            buildList {
                                repeat(3) { i -> add(chunk[offset + i * 3]) }
                            }
                        )
                    )
                }
            }
        }
        .countValidTriangles()

    private fun Iterable<Triple<Int, Int, Int>>.countValidTriangles() = count { (a, b, c) ->
        (a + b > c) && (a + c > b) && (b + c > a)
    }

    private fun <T> listToTriple(list: List<T>) = list
        .also { check(it.size == 3) }
        .let { (a, b, c) -> Triple(a, b, c) }

    private fun parseNumbers(input: String) =
        input
            .trim()
            .splitToSequence(WHITESPACE_REGEX)
            .map(String::trim)
            .map(String::toInt)

    private val WHITESPACE_REGEX = """\s+""".toRegex()
}