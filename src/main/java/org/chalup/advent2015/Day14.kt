package org.chalup.advent2015

object Day14 {
    fun task1(input: List<String>): Int {
        return parseReindeers(input).maxOf {
            it.distanceSequence()
                .takeWhile { (t, _) -> t <= 2503 }
                .last()
                .let { (_, d) -> d }
        }
    }

    fun task2(input: List<String>): Int {
        return parseReindeers(input)
            .map { it.distanceSequence().iterator() }
            .let { deers ->
                val scores = MutableList(deers.size) { 0 }

                repeat(2503) {
                    val distances = deers.map { it.next().second }
                    val maxDistance = distances.max()

                    distances.forEachIndexed { index, d ->
                        if (d == maxDistance) {
                            scores[index] = scores[index] + 1
                        }
                    }
                }

                scores.max()
            }
    }

    private fun parseReindeers(input: List<String>) = input.map { line ->
            val (speed, flightDuration, restDuration) = line.split(" ").mapNotNull { it.toIntOrNull() }
            ReindeerInfo(speed, flightDuration, restDuration)
        }

    private fun ReindeerInfo.distanceSequence() = sequence {
        var t = 0
        var d = 0
        while (true) {
            repeat(flightDuration) {
                t++
                d += speed

                yield(t to d)
            }

            repeat(restDuration) {
                t++
                yield(t to d)
            }
        }
    }

    private data class ReindeerInfo(
        val speed: Int,
        val flightDuration: Int,
        val restDuration: Int
    )
}