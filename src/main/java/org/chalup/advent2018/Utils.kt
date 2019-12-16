package org.chalup.advent2018

import org.chalup.utils.Vector

fun <T> Collection<T>.cycle(): Iterable<T> = object : Iterable<T> {
    override fun iterator() = object : Iterator<T> {
        var iterator: Iterator<T> = this@cycle.iterator()

        override fun hasNext(): Boolean = isNotEmpty()

        override fun next(): T {
            if (isEmpty()) throw NoSuchElementException()

            if (!iterator.hasNext())
                iterator = this@cycle.iterator()

            return iterator.next()
        }
    }
}

fun <T, R> Iterable<T>.scan(initial: R, operation: (R, T) -> R) = object : Iterable<R> {
    override fun iterator() = object : Iterator<R> {
        val scannedIterator = this@scan.iterator()
        var initialEmitted = false
        var accumulator = initial

        override fun hasNext() = !initialEmitted || scannedIterator.hasNext()

        override fun next(): R {
            if (!initialEmitted) {
                initialEmitted = true
            } else {
                accumulator = operation(accumulator, scannedIterator.next())
            }

            return accumulator
        }
    }
}

tailrec fun gcd(a: Int, b: Int): Int =
    if (b == 0) a
    else gcd(b, a % b)

fun lcm(a: Int, b: Int) = a * b / gcd(a, b)

tailrec fun gcd(a: Long, b: Long): Long =
    if (b == 0L) a
    else gcd(b, a % b)

fun lcm(a: Long, b: Long) = a * b / gcd(a, b)

inline fun <reified T : Enum<T>> Enum<T>.cycleNext(): T = enumValues<T>().run { get((ordinal + 1) % size) }
inline fun <reified T : Enum<T>> Enum<T>.cyclePrev(): T = enumValues<T>().run { get((ordinal + size - 1) % size) }
