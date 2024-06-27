package org.chalup.utils

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