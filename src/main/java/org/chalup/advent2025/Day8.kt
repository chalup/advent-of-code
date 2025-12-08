package org.chalup.advent2025

import org.chalup.utils.Vector3
import java.util.LinkedList

object Day8 {
    fun task1(input: List<String>): Long {
        val points = input.map { it.split(",").map(String::toLong).let { (x, y, z) -> Vector3(x, y, z) } }

        val squaredDistances = buildMap<Pair<Vector3, Vector3>, Long> {
            points.forEachIndexed { i, p1 ->
                points.forEachIndexed { j, p2 ->
                    if (i < j) {
                        put(p1 to p2, distanceSquared(p1, p2))
                    }
                }
            }
        }

        val connections = squaredDistances
            .entries
            .sortedBy { (_, distanceSquared) -> distanceSquared }
            .asSequence()
            .take(1000)
            .map { (points, _) -> points }
            .flatMap { (p1, p2) -> sequenceOf(p1 to p2, p2 to p1) }
            .groupBy(
                keySelector = { (from, _) -> from },
                valueTransform = { (_, to) -> to },
            )

        val clusters = mutableListOf<Set<Vector3>>()
        val pointsToCheck = points.toMutableSet()
        while (pointsToCheck.isNotEmpty()) {
            val cluster = mutableSetOf<Vector3>()

            val queue = LinkedList<Vector3>().apply { add(pointsToCheck.first()) }
            while (queue.isNotEmpty()) {
                val p = queue.removeFirst()
                if (cluster.add(p)) {
                    queue += connections[p].orEmpty()
                }
            }

            clusters += cluster
            pointsToCheck -= cluster
        }

        return clusters
            .map { it.size.toLong() }
            .sortedDescending()
            .take(3)
            .reduce(Long::times)
    }

    private fun distanceSquared(p1: Vector3, p2: Vector3) =
        (p1.x - p2.x) * (p1.x - p2.x) +
        (p1.y - p2.y) * (p1.y - p2.y) +
        (p1.z - p2.z) * (p1.z - p2.z)
}