package org.chalup.advent2018

object Day2 {
    enum class BoxIdCategory {
        DOUBLE, TRIPLE
    }

    fun categorizeBoxId(id: String) = id.groupingBy { it }.eachCount().values.toSet()
        .let {
            mutableListOf<BoxIdCategory>().apply {
                if (it.contains(2)) add(BoxIdCategory.DOUBLE)
                if (it.contains(3)) add(BoxIdCategory.TRIPLE)
            }
        }

    fun checksum(ids: List<String>) = ids.flatMap { categorizeBoxId(it) }.groupingBy { it }.eachCount().let {
        it.getOrDefault(BoxIdCategory.DOUBLE, 0) * it.getOrDefault(BoxIdCategory.TRIPLE, 0)
    }
}