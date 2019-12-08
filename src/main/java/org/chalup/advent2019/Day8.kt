package org.chalup.advent2019

object Day8 {
    fun task1(imageData: String, width: Int, height: Int) =
        imageData
            .toList()
            .chunked(width * height)
            .minBy { layer -> layer.count('0') }!!
            .let { layer -> layer.count('1') * layer.count('2') }

    private fun List<Char>.count(c: Char) = count { it == c }
}