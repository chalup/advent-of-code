package org.chalup.advent2017

object Day15 {
    fun task1(input: List<String>) = withGenerators(input) { a, b ->
        a.zip(b) { va, vb -> va % 65536 == vb % 65536 }
            .take(40_000_000)
            .count { it }
    }

    fun task2(input: List<String>) = withGenerators(input) { a, b ->
        a.filter { it % 4L == 0L }.zip(b.filter { it % 8L == 0L }) { va, vb -> va % 65536 == vb % 65536 }
            .take(5_000_000)
            .count { it }
    }

    private fun withGenerators(input: List<String>, block: (Sequence<Long>, Sequence<Long>) -> Int): Int {
        val (a, b) = input.map { it.split(" ").mapNotNull(String::toLongOrNull).last() }

        fun generatorSequence(initialValue: Long, multiplier: Long) = generateSequence(initialValue) { prev ->
            (prev * multiplier) % 2147483647
        }.drop(1)


        return block(generatorSequence(a, 16807), generatorSequence(b, 48271))
    }
}