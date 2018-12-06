package org.chalup.advent2015

object Day5 {
    private val VOWELS = "aeiou".toSet()
    private val NAUGHTY_PAIRS = setOf("ab", "cd", "pq", "xy")

    fun String.isNice() = when {
        count { it in VOWELS } < 3 -> false
        zipWithNext { a, b -> "$a$b" }.any { it in NAUGHTY_PAIRS } -> false
        zipWithNext().none { (a, b) -> a == b } -> false
        else -> true
    }

    fun String.isReallyNice(): Boolean {
        val containsTwoNonOverlappingPairsOfTheSameLetters: Boolean by lazy {
            zipWithNext()
                .mapIndexed { index, pair -> index to pair }
                .groupBy { (_, pair) -> pair }
                .values
                .flatMap { it.map { (index, _) -> index }.zipWithNext { a, b -> b - a } }
                .any { it > 1 }
        }

        val containsPalindromeTriplet: Boolean by lazy {
            zipWithNext()
                .zipWithNext()
                .any { (ab, bc) -> ab.first == bc.second }
        }

        return containsTwoNonOverlappingPairsOfTheSameLetters && containsPalindromeTriplet
    }


    fun countNiceStrings(strings: List<String>) = strings.count { it.isNice() }
    fun countReallyNiceStrings(strings: List<String>) = strings.count { it.isReallyNice() }
}