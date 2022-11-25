package org.chalup.advent2017

import org.chalup.utils.Point
import kotlin.math.absoluteValue

object Day3 {
    fun task1(input: List<String>) = input.first().toInt()
        .let(::getCellPosition)
        .let { it.x.absoluteValue + it.y.absoluteValue }

    fun task2(input: List<String>) = input.first().toInt()
        .let { n ->
            val cache = mutableMapOf<Point, Int>(
                Point(0, 0) to 1
            )

            var cell = 2
            while (true) {
                val p = getCellPosition(cell++)

                val value = run {
                    var sum = 0

                    for (dx in -1..1) {
                        for (dy in -1..1) {
                            sum += cache[Point(p.x + dx, p.y + dy)] ?: 0
                        }
                    }

                    sum
                }

                cache[p] = value

                if (value > n) return@let value
            }
        }

    private fun getCellPosition(n: Int): Point {
        fun Int.ringSize(): Int = (this * 2 + 1).let { it * it }

        val ring = generateSequence(0) { it + 1 }
            .takeWhile { ring -> ring.ringSize() < n }
            .count()

        if (ring == 0) return Point(0, 0)

        val indexInRing = n - (ring - 1).ringSize() - 1
        val ringCount = ring.ringSize() - (ring - 1).ringSize()
        val runSize = ringCount / 4

        return when (indexInRing / runSize) {
            0 -> Point(x = ring, y = ring - 1 - (indexInRing % runSize))
            1 -> Point(y = -ring, x = ring - 1 - (indexInRing % runSize))
            2 -> Point(x = -ring, y = -ring + 1 + (indexInRing % runSize))
            3 -> Point(y = ring, x = -ring + 1 + (indexInRing % runSize))
            else -> throw IllegalStateException("n=$n, ring=$ring, indexInRing=$indexInRing, ringCount=$ringCount")
        }
    }
}