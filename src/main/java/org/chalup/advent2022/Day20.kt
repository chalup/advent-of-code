package org.chalup.advent2022

object Day20 {
    fun task1(input: List<String>) = input
        .map { EncryptedCoordinate(it.toLong()) }
        .let { mix(it, times = 1) }
        .let(::getGroveCoordinate)

    fun task2(input: List<String>) = input
        .map { EncryptedCoordinate(it.toLong() * 811589153) }
        .let { mix(it, times = 10) }
        .let(::getGroveCoordinate)

    private fun mix(initialArrangement: List<EncryptedCoordinate>, times: Int): MutableList<EncryptedCoordinate> {
        val currentArrangement = initialArrangement.toMutableList()

        repeat(times) {
            for (coordinate in initialArrangement) {
                val index = currentArrangement.indexOf(coordinate)
                currentArrangement.removeAt(index)

                val newIndex = fixIndex(index + coordinate.value, currentArrangement.size)
                currentArrangement.add(newIndex, coordinate)
            }
        }

        return currentArrangement
    }

    private fun getGroveCoordinate(coords: List<EncryptedCoordinate>): Long {
        val indexOfZero = coords.indexOfFirst { it.value == 0L }

        return listOf(1000, 2000, 3000)
            .sumOf { coords[(indexOfZero + it) % coords.size].value }
    }

    private fun fixIndex(index: Long, mod: Int) =
        if (index >= 0) index % mod
        else {
            (index + ((-index / mod) + 1) * mod) % mod
        }.toInt()
}

private class EncryptedCoordinate(val value: Long)