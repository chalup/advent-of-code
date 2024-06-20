package org.chalup.advent2016

import org.chalup.advent2015.Day4.md5

object Day14 {
    fun task1(input: List<String>): Int = getIndexFor64thPadKeys(
        salt = input.first(),
        useKeyStretching = false
    )

    fun task2(input: List<String>): Int = getIndexFor64thPadKeys(
        salt = input.first(),
        useKeyStretching = true
    )

    private fun getIndexFor64thPadKeys(salt: String, useKeyStretching: Boolean): Int {
        val hashes = mutableMapOf<Int, String>()
        fun getHash(index: Int) = hashes.getOrPut(index) {
            (0..(if (useKeyStretching) 2016 else 0)).fold(salt + index.toString()) { acc, _ -> acc.md5() }
        }

        val indicesOfKeys = mutableListOf<Int>()
        var currentIndex = 0

        while (indicesOfKeys.size < 64) {
            val hashForIndex = getHash(currentIndex)

            val triplet = hashForIndex.indices.firstNotNullOfOrNull { i -> hashForIndex.substring(i, (i + 3).coerceAtMost(hashForIndex.length)).takeIf { it.length == 3 && it.toSet().size == 1 } }
            if (triplet != null) {
                val quintuplet = triplet.first().toString().repeat(5)

                if ((1..1000).any { di -> quintuplet in getHash(currentIndex + di) }) {
                    indicesOfKeys += currentIndex
                }
            }

            currentIndex++
        }

        return indicesOfKeys.last()
    }
}
