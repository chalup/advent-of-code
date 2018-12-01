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