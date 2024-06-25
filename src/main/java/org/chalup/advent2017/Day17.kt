package org.chalup.advent2017

import java.util.LinkedList

object Day17 {
    fun task1(input: List<String>): Int = spinlock(n = 2017, delta = input.single().toInt(), lookupIndex = 2017)
    fun task2(input: List<String>): Int = spinlock(n = 50_000_000, delta = input.single().toInt(), lookupIndex = 0)

    private fun spinlock(n: Int, delta: Int, lookupIndex: Int): Int {
        val list = LinkedList<Int>().apply { add(0) }

        val i = list.cyclicListIterator()

        repeat(n) {
            if (it % 100_000 == 0) println(it)

            repeat(delta % list.size) { i.next() }
            i.add(it + 1)
        }

        return list[(list.indexOf(lookupIndex) + 1) % list.size]
    }
}

private fun <E> MutableList<E>.cyclicListIterator(): MutableListIterator<E> {
    return object : MutableListIterator<E> {
        var delegate = this@cyclicListIterator.listIterator()

        override fun add(element: E) = delegate.add(element)
        override fun hasNext(): Boolean = this@cyclicListIterator.isNotEmpty()

        override fun next(): E {
            if (!delegate.hasNext()) delegate = this@cyclicListIterator.listIterator()
            return delegate.next()
        }

        override fun hasPrevious(): Boolean = TODO()
        override fun nextIndex(): Int = TODO()
        override fun previous(): E = TODO()
        override fun previousIndex(): Int = TODO()
        override fun remove() = TODO()
        override fun set(element: E) = TODO()
    }
}
