package org.chalup.advent2025

import org.chalup.utils.Point

object Day9 {
    fun task1(input: List<String>): Long {
        return input
            .map { it.split(",").map(String::toInt).let { (x, y) -> Point(x, y) } }
            .let { points ->
                points.flatMapIndexed { i, p1 ->
                    points
                        .asSequence()
                        .drop(i + 1)
                        .map { p2 -> p1 to p2 }
                }
            }
            .maxOf { (p1, p2) ->
                val w = (maxOf(p1.x, p2.x) - minOf(p1.x, p2.x) + 1).toLong()
                val h = (maxOf(p1.y, p2.y) - minOf(p1.y, p2.y) + 1).toLong()

                w * h
            }
    }
}