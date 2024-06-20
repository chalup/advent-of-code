package org.chalup.advent2016

object Day16 {
    fun task1(input: List<String>): String = calculateChecksum(input.first(), desiredLength = 272)
    fun task2(input: List<String>): String = calculateChecksum(input.first(), desiredLength = 35651584)

    private fun calculateChecksum(initialDataSequence: String, desiredLength: Int): String {
        val dataGenerationSequence = generateSequence(initialDataSequence) { acc ->
            buildString {
                append(acc)
                append('0')
                acc.toList().asReversed().forEach {
                    when (it) {
                        '0' -> append('1')
                        '1' -> append('0')
                        else -> throw IllegalStateException()
                    }
                }
            }
        }

        val data = dataGenerationSequence
            .first { it.length >= desiredLength }
            .take(desiredLength)

        val checksumSequence = generateSequence(data) { acc ->
            if (acc.length % 2 == 0) {
                acc.chunked(2)
                    .joinToString(separator = "") {
                        when (it) {
                            "00" -> "1"
                            "11" -> "1"
                            else -> "0"
                        }
                    }
            } else {
                null
            }
        }

        return checksumSequence.last()
    }
}
