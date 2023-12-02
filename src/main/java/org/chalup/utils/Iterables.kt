package org.chalup.utils

operator fun <T, U> Iterable<T>.times(other: Iterable<U>) = flatMap { t -> other.map { u -> t to u } }

inline fun <T, R : Comparable<R>> Iterable<T>.minMaxBy(selector: (T) -> R): Pair<R, R> {
    val iterator = iterator()

    if (!iterator.hasNext()) throw IllegalArgumentException("Cannot get the min/max of empty iterable!")

    var min = selector(iterator.next())
    var max = min

    for (i in iterator) {
        val v = selector(i)
        min = minOf(v, min)
        max = maxOf(v, max)
    }

    return min to max
}

fun Iterable<Int>.minMax(): Pair<Int, Int> = minMaxBy { it }
fun Iterable<Long>.minMaxLong(): Pair<Long, Long> = minMaxBy { it }

fun range(a: Int, b: Int) = if (a < b) a..b else b..a

operator fun IntRange.contains(other: IntRange): Boolean =
    other.first >= this.first && other.last <= this.last

infix fun IntRange.overlaps(other: IntRange): Boolean =
    other.first in this || other.last in this

infix fun IntRange.intersection(other: IntRange): IntRange? =
    (maxOf(this.first, other.first)..minOf(this.last, other.last)).takeIf { this overlaps other }

