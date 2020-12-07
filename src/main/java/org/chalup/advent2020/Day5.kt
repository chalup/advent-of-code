package org.chalup.advent2020

object Day5 {
    fun task1(input: List<String>): Int = input
        .map { seatNumber(it) }
        .max()!!

    fun task2(input: List<String>): Int = input
        .mapTo(mutableSetOf()) { seatNumber(it) }
        .let<Set<Int>, Int> { seatNumbers ->
            val seatNumbersRange = seatNumbers.min()!!..seatNumbers.max()!!

            seatNumbersRange.first { it !in seatNumbers }
        }

    private fun seatNumber(boardingPass: String) = boardingPass
        .replace('B', '1')
        .replace('F', '0')
        .replace('R', '1')
        .replace('L', '0')
        .toInt(radix = 2)
}