package org.chalup.advent2015

import com.google.common.hash.Hashing

object Day4 {
    private fun naturalNumbersSequence() = generateSequence(1) { it + 1 }

    @Suppress("UnstableApiUsage", "DEPRECATION")
    fun String.md5(): String = Hashing.md5().hashBytes(toByteArray()).toString()

    fun mineAdventCoin(secretKey: String, difficulty: Int): Int = naturalNumbersSequence()
        .map { it to "$secretKey$it".md5() }
        .dropWhile { (_, md5) -> !md5.startsWith("0".repeat(difficulty)) }
        .map { (i, _) -> i }
        .first()
}