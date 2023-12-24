package org.chalup.advent2023

import org.chalup.utils.Vector3
import kotlin.math.sign

object Day24 {
    fun task1(input: List<String>, testAreaRange: LongRange): Int {
        val hailstones = input.map { line ->
            fun parseVector3(text: String) = text.split(", ").map { it.trim().toLong() }.let { (x, y, z) -> Vector3(x, y, z) }

            Hailstone(
                p = line.substringBefore(" @ ").let(::parseVector3),
                v = line.substringAfter(" @ ").let(::parseVector3),
            )
        }

        val pairs = hailstones.flatMapIndexed { i: Int, one ->
            hailstones.asSequence().drop(i + 1).map { other -> one to other }
        }

        val intersections = pairs.mapNotNull { (a, b) ->
            val denominator = (a.v.x) * (b.v.y) - (a.v.y) * (b.v.x)

            if (denominator == 0L) {
                // TODO: this might be a source of errors. Imagine hailstones on coinciding lines
                //       moving at different speeds. They will collide eventually, and they may
                //       just collide within the test area bounds.
                null
            } else {
                val nomX = (a.p.x * (a.p.y + a.v.y) - a.p.y * (a.p.x + a.v.x)) * (-b.v.x) - (-a.v.x) * (b.p.x * (b.p.y + b.v.y) - b.p.y * (b.p.x + b.v.x))
                val nomY = (a.p.x * (a.p.y + a.v.y) - a.p.y * (a.p.x + a.v.x)) * (-b.v.y) - (-a.v.y) * (b.p.x * (b.p.y + b.v.y) - b.p.y * (b.p.x + b.v.x))

                Triple(
                    a, b,
                    PointF(
                        nomX.toDouble() / denominator.toDouble(),
                        nomY.toDouble() / denominator.toDouble(),
                    )
                )
            }
        }

        // I'm getting "too low" hint for 11907. It doesn't make any sense – if anything, I think I'm not
        // filtering nearly enough collisions. For example, I don't verify the time of collision.
        // Maybe it's some double math shenanigans?
        // Also, I'm finding it interesting that we have 11k collisions with just 300 hailstones...

        val testArea = testAreaRange.first.toDouble()..<(testAreaRange.last + 1).toDouble()

        return intersections
            .filter { (_, _, p) -> p.x in testArea && p.y in testArea }
            .filter { (h, _, p) -> sign(p.x - h.p.x) == sign(h.v.x.toDouble()) }
            .filter { (_, h, p) -> sign(p.x - h.p.x) == sign(h.v.x.toDouble()) }
            .count()
    }

    fun task2(input: List<String>): Long = TODO()
}

data class PointF(
    val x: Double,
    val y: Double,
)

data class Hailstone(
    val p: Vector3,
    val v: Vector3,
)