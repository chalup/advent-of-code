package org.chalup.advent2023

import org.chalup.utils.Cube
import org.chalup.utils.intersects
import org.chalup.utils.transposeBy

object Day22 {
    fun task1(input: List<String>): Int {
        val cubes = input
            .map { line ->
                val (x1, y1, z1) = line.substringBefore('~').split(',').map { it.toLong() }
                val (x2, y2, z2) = line.substringAfter('~').split(',').map { it.toLong() }

                check(x1 <= x2)
                check(y1 <= y2)
                check(z1 <= z2)

                Cube(x1..x2, y1..y2, z1..z2)
            }
            .sortedBy { it.zs.first }

        val settledCubes = mutableSetOf<Cube>()
        val supports = mutableListOf<Pair<Cube, Cube>>()

        for (c in cubes) {
            val dropArea = c.copy(zs = 0 until c.zs.first)

            val potentialSupports = settledCubes.filter { it intersects dropArea }
            val supportLevel = potentialSupports.maxOfOrNull { it.zs.last } ?: 0L
            val droppedCube = c.transposeBy(dz = (supportLevel + 1) - c.zs.first)

            settledCubes.add(droppedCube)

            potentialSupports.mapNotNullTo(supports) { support -> support.takeIf { it.zs.last == supportLevel }?.let { it to droppedCube } }
        }

        val criticalSupports = supports.groupBy { (_, supported) -> supported }
            .values
            .map { it.map { (support, _) -> support } }
            .mapNotNullTo(mutableSetOf()) { it.singleOrNull() }

        return settledCubes.count { it !in criticalSupports }
    }

    fun Cube.transposeBy(dx: Long = 0, dy: Long = 0, dz: Long = 0) = Cube(
        xs transposeBy dx,
        ys transposeBy dy,
        zs transposeBy dz,
    )
}