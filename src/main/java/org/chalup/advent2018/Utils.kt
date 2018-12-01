package org.chalup.advent2018

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