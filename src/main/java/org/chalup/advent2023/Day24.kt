package org.chalup.advent2023

import java.math.BigDecimal

object Day24 {
    fun task1(input: List<String>, testAreaRange: LongRange): Int {
        val hailstones = input.map { line ->
            fun parseVector3(text: String) =
                text.split(", ").map { it.trim().toBigDecimal() }.let { (x, y, z) -> Vector3(x, y, z) }

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

            if (denominator == BigDecimal.ZERO) {
                null
            } else {
                val c1 = (a.p.x * a.v.y - a.p.y * a.v.x)
                val c2 = (b.p.x * b.v.y - b.p.y * b.v.x)

                val nomX = c1 * (-b.v.x) - (-a.v.x) * c2
                val nomY = c1 * (-b.v.y) - (-a.v.y) * c2

                Triple(
                    a, b,
                    Vector3(
                        nomX / denominator,
                        nomY / denominator,
                        z = BigDecimal.ZERO
                    )
                )
            }
        }

        val testArea = testAreaRange.first.toBigDecimal()..<(testAreaRange.last + 1).toBigDecimal()

        return intersections
            .filter { (_, _, p) -> p.x in testArea && p.y in testArea }
            .filter { (h, _, p) -> (p.x - h.p.x).signum() == h.v.x.signum() }
            .filter { (_, h, p) -> (p.x - h.p.x).signum() == h.v.x.signum() }
            .count()
    }

    fun task2(input: List<String>): Long = TODO()
}

private data class Vector3(
    val x: BigDecimal,
    val y: BigDecimal,
    val z: BigDecimal
)

private data class Hailstone(
    val p: Vector3,
    val v: Vector3,
)