package org.chalup.advent2023

object Day2 {
    fun task1(input: List<String>): Int = input
        .map(::parse)
        .filter {
            it.sets.none { set ->
                set.any { (color, count) -> count > color.limit }
            }
        }
        .sumOf { it.id }

    fun task2(input: List<String>): Int = input
        .map(::parse)
        .map { it.sets }
        .sumOf { sets ->
            sets
                .flatMap { it.entries }
                .groupingBy { (color, _) -> color }
                .fold(
                    initialValueSelector = { _, (_, count) -> count },
                    operation = { _, max, (_, count) -> maxOf(max, count) }
                )
                .values
                .fold(1, Int::times)
        }

    private fun parse(line: String): Game {
        val gameId = line.substringAfter(' ').substringBefore(':').toInt()

        val sets = line
            .substringAfter(':')
            .trim()
            .split(';')
            .map { setSpecs ->
                setSpecs
                    .split(',')
                    .map { it.trim() }
                    .map { cubesCount ->
                        val count = cubesCount.substringBefore(' ').toInt()
                        val color = cubesCount.substringAfter(' ').let { colorName ->
                            Color.values().first { it.name.equals(colorName, ignoreCase = true) }
                        }

                        color to count
                    }
                    .toMap()
            }

        return Game(gameId, sets)
    }
}

private data class Game(
    val id: Int,
    val sets: List<Map<Color, Int>>
)

private enum class Color(val limit: Int) {
    RED(12),
    GREEN(13),
    BLUE(14)
}