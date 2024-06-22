package org.chalup.utils

fun <T : Any> permutations(set: Set<T>) = sequence {
    fun helper(head: List<T>, elements: Set<T>): Sequence<List<T>> = sequence {
        if (elements.isEmpty()) yield(head)
        else {
            for (e in elements) {
                yieldAll(helper(head + e, elements - e))
            }
        }
    }

    yieldAll(helper(emptyList(), set))
}