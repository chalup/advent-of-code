package org.chalup.utils

operator fun <T, U> Iterable<T>.times(other: Iterable<U>) = flatMap { t -> other.map { u -> t to u } }

fun Iterable<Int>.minMax(): Pair<Int, Int> {
    var min = Int.MAX_VALUE
    var max = Int.MIN_VALUE

    val iterator = iterator()

    if (!iterator.hasNext()) throw IllegalArgumentException("Cannot get the min/max of empty iterable!")

    for (i in iterator) {
        min = minOf(i, min)
        max = maxOf(i, max)
    }

    return min to max
}

fun Iterable<Long>.minMaxLong(): Pair<Long, Long> {
    var min = Long.MAX_VALUE
    var max = Long.MIN_VALUE

    val iterator = iterator()

    if (!iterator.hasNext()) throw IllegalArgumentException("Cannot get the min/max of empty iterable!")

    for (i in iterator) {
        min = minOf(i, min)
        max = maxOf(i, max)
    }

    return min to max
}