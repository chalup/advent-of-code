package org.chalup.advent2015

object Day24 {
    fun task1(input: List<String>): Long {
        val packages = input.map(String::toLong).sortedDescending()
        val arr = calculateGroups(packages, 3)

        return arr
            .asSequence()
            .filter { a -> arr.any { b -> a and b == 0L } }
            .map { mask ->
                packages
                    .mapIndexedNotNull { index, weight -> weight.takeIf { (1L shl index) and mask != 0L } }
                    .let { it.size to it.fold(1, Long::times) }
            }
            .sortedWith(compareBy<Pair<Int, Long>> { (numberOfPackages, _) -> numberOfPackages }.thenBy { (_, quantumEntanglement) -> quantumEntanglement })
            .first()
            .let { (_, quantumEntanglement) -> quantumEntanglement }
    }

    fun task2(input: List<String>): Long {
        val packages = input.map(String::toLong).sortedDescending()

        val arr = calculateGroups(packages, 4).toList()

        return arr
            .asSequence()
            .flatMapIndexed { i, a -> arr.asSequence().drop(i).mapNotNull { b -> if (a and b == 0L) a to b else null } }
            .filter { (a, b) -> arr.any { c -> (a or b) and c == 0L } }
            .map { (mask, _) ->
                packages
                    .mapIndexedNotNull { index, weight -> weight.takeIf { (1L shl index) and mask != 0L } }
                    .let { it.size to it.fold(1, Long::times) }
            }
            .sortedWith(compareBy<Pair<Int, Long>> { (numberOfPackages, _) -> numberOfPackages }.thenBy { (_, quantumEntanglement) -> quantumEntanglement })
            .first()
            .let { (_, quantumEntanglement) -> quantumEntanglement }
    }

    private fun calculateGroups(packages: List<Long>, numberOfGroups: Int): Set<Long> {
        val cache = mutableMapOf<Long, Set<Long>>()

        fun arrangements(groupWeight: Long): Set<Long> = cache.getOrPut(groupWeight) {
            if (groupWeight == 0L) return@getOrPut setOf(0L)

            packages
                .flatMapIndexedTo(mutableSetOf()) { index, weight ->
                    when {
                        weight > groupWeight -> emptySequence()
                        else -> arrangements(groupWeight - weight)
                            .asSequence()
                            .mapNotNull { mask -> (mask or (1L shl index)).takeIf { it != mask } }
                    }
                }
        }

        val groupWeight = packages.sum() / numberOfGroups
        for (i in 1..groupWeight) arrangements(i)

        return arrangements(groupWeight)
    }
}
