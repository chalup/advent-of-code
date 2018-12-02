package org.chalup.advent2018

object Day2 {
    enum class BoxIdCategory {
        DOUBLE, TRIPLE
    }

    fun categorizeBoxId(id: String) = id.groupingBy { it }.eachCount().values.toSet()
        .let {
            mutableListOf<BoxIdCategory>().apply {
                if (it.contains(2)) add(BoxIdCategory.DOUBLE)
                if (it.contains(3)) add(BoxIdCategory.TRIPLE)
            }
        }

    fun checksum(ids: List<String>) = ids.flatMap { categorizeBoxId(it) }.groupingBy { it }.eachCount().let {
        it.getOrDefault(BoxIdCategory.DOUBLE, 0) * it.getOrDefault(BoxIdCategory.TRIPLE, 0)
    }

    private fun idsMatch(one: String, other: String): Boolean {
        check(one.length == other.length)

        val numberOfDifferences = one.zip(other) { a, b -> a != b }.count { it }

        return numberOfDifferences == 1
    }

    private fun commonPart(one: String, other: String): String {
        check(one.length == other.length)

        return one.zip(other) { a, b -> a.takeIf { a == b } }.filterNotNull().fold("") { string, char -> string + char }
    }

    private fun <T> List<T>.pairs(): List<Pair<T, T>> = this
        .mapIndexed { index, element -> subList(index + 1, size).map { otherElement -> element to otherElement } }
        .flatten()

    fun commonLettersOfMatchingBoxes(input: List<String>): String =
        input
            .pairs()
            .first { (one, other) -> idsMatch(one, other) }
            .let { (one, other) -> commonPart(one, other) }

    fun efficientCommonLettersOfMatchingBoxes(input: List<String>): String =
        input
            .first()
            .indices
            .flatMap { index ->
                input
                    .groupingBy { it.toMutableList().apply { removeAt(index) }.joinToString("") }
                    .eachCount()
                    .filter { (_, count) -> count == 2 }
                    .keys
            }
            .first()
}