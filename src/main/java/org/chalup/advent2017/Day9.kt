package org.chalup.advent2017

object Day9 {
    fun task1(input: List<String>) = calculateStats(input.single()).let { (score, _) -> score }
    fun task2(input: List<String>) = calculateStats(input.single()).let { (_, cancelledGarbageChars) -> cancelledGarbageChars }

    private fun calculateStats(stream: String): Pair<Int, Int> {
        var score = 0
        var nestingLevel = 0
        var isInGarbage = false
        var cancelledGarbageChars = 0

        val i = stream.iterator()

        while (i.hasNext()) {
            val char = i.next()

            when {
                isInGarbage && char == '>' -> isInGarbage = false
                char == '!' -> i.next()
                isInGarbage -> cancelledGarbageChars++
                char == '{' -> nestingLevel += 1
                char == '}' -> {
                    score += nestingLevel
                    nestingLevel -= 1
                }

                char == '<' -> isInGarbage = true
            }
        }

        check(nestingLevel == 0)
        check(!isInGarbage)

        return score to cancelledGarbageChars
    }
}