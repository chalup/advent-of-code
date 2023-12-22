package org.chalup.utils

infix operator fun LongRange.contains(other: LongRange): Boolean =
    other.first >= this.first && other.last <= this.last

infix fun LongRange.overlaps(other: LongRange): Boolean =
    other.first in this || other.last in this

infix fun LongRange.intersects(other: LongRange): Boolean =
    this overlaps other || other overlaps this

infix fun LongRange.intersection(other: LongRange): LongRange? =
    (maxOf(this.first, other.first)..minOf(this.last, other.last)).takeIf { this overlaps other || other overlaps this }

infix fun LongRange.except(other: LongRange): List<LongRange> {
    val intersection = (this intersection other) ?: return listOf(this)
    if (intersection == this) return emptyList()

    val result = mutableListOf<LongRange>()

    if (intersection.first > this.first) {
        result.add(this.first until intersection.first)
    }

    if (intersection.last < this.last) {
        result.add(intersection.last + 1..this.last)
    }

    return result
}

fun LongRange.extendBy(n: Long) = (first - n)..(last + n)
fun LongRange.size(): Long = (last - first + 1).takeUnless { isEmpty() } ?: 0L
