package org.chalup.advent2025

object Day3 {
    fun task1(input: List<String>): Long = input.sumOf { findJoltage(it, length = 2) }
    fun task2(input: List<String>): Long = input.sumOf { findJoltage(it, length = 12) }

    private fun findJoltage(bank: String, length: Int): Long {
        fun findJoltage(bank: String, startIndex: Int, length: Int): String? {
            if (length == 0) return ""
            if (startIndex == bank.length) return null

            for (c in '9' downTo '0') {
                when (val headIndex = bank.indexOf(c, startIndex)) {
                    -1 -> continue
                    else -> findJoltage(bank, headIndex + 1, length - 1)
                        ?.run { return c + this }
                }
            }

            return null
        }

        return findJoltage(bank, 0, length)!!.toLong()
    }
}