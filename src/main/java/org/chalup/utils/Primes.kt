package org.chalup.utils

import com.google.common.collect.Iterators
import org.chalup.advent2018.sqrt

fun primes(max: Int) = MutableList(max + 1) { it > 1 }
    .apply {
        for (i in 2..(max.sqrt() + 1)) {
            if (this[i]) {
                generateSequence(i * i) { it + i }
                    .takeWhile { it <= max }
                    .forEach { this[it] = false }
            }
        }
    }
    .mapIndexedNotNull { index, b -> index.takeIf { b } }


fun primeFactors(n: Int, primes: List<Int>): List<Int> {
    val primesIterator = Iterators.peekingIterator(primes.iterator())
    var reminder = n

    return buildList<Int> {
        while (reminder > 1) {
            while (primesIterator.peek() <= reminder && reminder % primesIterator.peek() != 0) {
                primesIterator.next()
            }

            val primeFactor = primesIterator.peek()

            add(primeFactor)
            reminder /= primeFactor
        }
    }.also { check(it.fold(1, Int::times) == n) }
}

fun allDividers(n: Int, primes: List<Int>): Set<Int> {
    fun helper(n: Int, primeFactors: Counter<Int>): Set<Int> = buildSet {
        add(n)

        primeFactors.flatMapTo(this) { (i, _) ->
            helper(n * i, primeFactors - i)
        }
    }

    return helper(1, Counter(primeFactors(n, primes)))
}
