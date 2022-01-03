package org.chalup.advent2016

import org.chalup.advent2015.Day4.md5

object Day5 {
    fun task1(input: List<String>): String = input
        .single()
        .let { roomName ->
            val indexSequence = generateSequence(0) { it + 1 }

            indexSequence.map { index -> "$roomName$index".md5() }
        }
        .filter { it.startsWith("00000") }
        .take(8)
        .map { it[5] }
        .joinToString(separator = "")

    fun task2(input: List<String>): String = input
        .single()
        .let { roomName ->
            val indexSequence = generateSequence(0) { it + 1 }

            indexSequence.map { index -> "$roomName$index".md5() }
        }
        .filter { it.startsWith("00000") }
        .filter { it[5] in '0'..'7' }
        .onEach { println(it) }
        .map { (it[5] - '0') to it[6] }
        .distinctBy { (position, _) -> position }
        .take(8)
        .sortedBy { (position, _) -> position }
        .map { (_, char) -> char }
        .joinToString(separator = "")
}