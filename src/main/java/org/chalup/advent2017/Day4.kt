package org.chalup.advent2017

object Day4 {
    fun task1(input: List<String>) = input.count { passphrase ->
        passphrase.split("""\s+""".toRegex())
            .let { it.count() == it.asSequence().distinct().count() }
    }

    fun task2(input: List<String>) = input.count { passphrase ->
        passphrase.split("""\s+""".toRegex())
            .let { it.count() == it.asSequence().distinctBy { word -> word.toList().sorted() }.count() }
    }
}