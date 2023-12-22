package org.chalup.advent2023

import org.chalup.utils.Cube
import org.chalup.utils.intersects
import org.chalup.utils.transposeBy
import java.util.PriorityQueue

object Day22 {
    fun task1(input: List<String>) = solve(input).first
    fun task2(input: List<String>) = solve(input).second

    private fun solve(input: List<String>): Pair<Int, Int> {
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
        val supportsByCube = mutableMapOf<Cube, Set<Cube>>()
        val supportedCubesByCube = mutableMapOf<Cube, MutableSet<Cube>>()

        for (c in cubes) {
            val dropArea = c.copy(zs = 0 until c.zs.first)

            val potentialSupports = settledCubes.filter { it intersects dropArea }
            val supportLevel = potentialSupports.maxOfOrNull { it.zs.last } ?: 0L
            val droppedCube = c.transposeBy(dz = (supportLevel + 1) - c.zs.first)

            settledCubes.add(droppedCube)

            potentialSupports
                .filter { it.zs.last == supportLevel }
                .also { supportsByCube[droppedCube] = it.toSet() }
                .forEach { supportedCubesByCube.getOrPut(it) { mutableSetOf() }.add(droppedCube) }
        }

        val criticalSupports = supportsByCube
            .mapNotNullTo(mutableSetOf()) { (_, supports) -> supports.singleOrNull() }

        val cubesThatCanBeSafelyDisintegrated = settledCubes.count { it !in criticalSupports }

        val fallingCubes = criticalSupports.sumOf { firstRemovedCube ->
            val removedCubes = mutableSetOf<Cube>()
            val queue = PriorityQueue<Cube>(compareBy { it.zs.first }).apply { add(firstRemovedCube) }
            while (queue.isNotEmpty()) {
                val cube = queue.poll()
                removedCubes.add(cube)

                for (supportedCube in supportedCubesByCube[cube].orEmpty()) {
                    if (supportsByCube.getValue(supportedCube).all { it in removedCubes }) {
                        queue.add(supportedCube)
                    }
                }
            }

            removedCubes.size - 1
        }

        return cubesThatCanBeSafelyDisintegrated to fallingCubes
    }

    fun Cube.transposeBy(dx: Long = 0, dy: Long = 0, dz: Long = 0) = Cube(
        xs transposeBy dx,
        ys transposeBy dy,
        zs transposeBy dz,
    )
}