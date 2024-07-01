package org.chalup.utils

class Counter<T>(private val mutableMap: MutableMap<T, Int> = mutableMapOf()) : Map<T, Int> by mutableMap {
    constructor(i: Iterable<T>) : this(i.groupingBy { it }.eachCountTo(mutableMapOf()))

    operator fun minus(item: T): Counter<T> {
        return when (val currentCount = mutableMap[item]) {
            null -> throw IllegalArgumentException("No $item in counter: $mutableMap")
            1 -> Counter(mutableMap.toMutableMap().apply { remove(item) })
            else -> Counter(mutableMap.toMutableMap().apply { this[item] = currentCount - 1 })
        }
    }
}
