package org.chalup.advent2023

object Day1 {
    fun task1(input: List<String>): Int = input
        .sumOf { line ->
            val tens = line.first { it.isDigit() }.digitToInt()
            val ones = line.last { it.isDigit() }.digitToInt()

            tens * 10 + ones
        }

    fun task2(input: List<String>): Int = input
        .sumOf { line ->
            val tens = line
                .indices
                .firstNotNullOf { i ->
                    line[i]
                        .takeIf { it.isDigit() }
                        ?.digitToInt()
                        ?: run {
                            Digit.values()
                                .find {
                                    line.substring(i).startsWith(it.name, ignoreCase = true)
                                }
                                ?.ordinal?.inc()
                        }
                }

            val ones = line
                .indices
                .reversed()
                .firstNotNullOf { i ->
                    line[i]
                        .takeIf { it.isDigit() }
                        ?.digitToInt()
                        ?: run {
                            Digit.values()
                                .find {
                                    line.substring(0..i)
                                        .reversed()
                                        .startsWith(it.name.reversed(), ignoreCase = true)
                                }
                                ?.ordinal?.inc()
                        }
                }

            tens * 10 + ones
        }
}

private enum class Digit {
    ONE,
    TWO,
    THREE,
    FOUR,
    FIVE,
    SIX,
    SEVEN,
    EIGHT,
    NINE
}