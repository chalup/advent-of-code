package org.chalup.advent2019

object Day8 {
    fun task1(imageData: String, width: Int, height: Int) =
        layers(imageData, width, height)
            .minByOrNull { layer -> layer.count('0') }!!
            .let { layer -> layer.count('1') * layer.count('2') }

    fun task2(imageData: String, width: Int, height: Int): String =
        layers(imageData, width, height)
            .reduce { upper, lower -> upper.zip(lower) { u, l -> if (u == '2') l else u } }
            .let { layer ->
                layer
                    .chunked(width)
                    .joinToString(separator = "\n", transform = { row ->
                        row.joinToString(separator = "", transform = { pixel ->
                            when(pixel) {
                                '0' -> " "
                                '1' -> "#"
                                '2' -> "."
                                else -> "!"
                            }
                        })
                    })
                    .let { "\n$it" }
            }

    private fun layers(imageData: String, width: Int, height: Int): List<List<Char>> {
        return imageData
            .toList()
            .chunked(width * height)
    }

    private fun List<Char>.count(c: Char) = count { it == c }
}