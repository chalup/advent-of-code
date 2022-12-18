package org.chalup.advent2022

import org.chalup.advent2021.Day22.Cube
import org.chalup.advent2021.Day22.size
import org.chalup.utils.Vector3

object Day18 {
    fun task1(input: List<String>) = input
        .let(::parsePoints)
        .let(::countFaces)

    fun task2(input: List<String>) = input
        .let(::parsePoints)
        .let { points ->
            val castCube = points.boundingCube().let {
                Cube(
                    it.xs.extendBy(1),
                    it.ys.extendBy(1),
                    it.zs.extendBy(1),
                )
            }

            val cast = mutableSetOf<Point3D>()
            val floodFillHeads = mutableListOf(
                castCube.let { Point3D(it.xs.first, it.ys.first, it.zs.first) }
            )

            while (floodFillHeads.isNotEmpty()) {
                val head = floodFillHeads.removeFirst()
                cast += head

                for (v in adjacentVectors) {
                    val newHead = head + v

                    if (newHead !in castCube) continue
                    if (newHead in cast) continue
                    if (newHead in points) continue
                    if (newHead in floodFillHeads) continue

                    floodFillHeads += newHead
                }
            }

            val allCastFaces = countFaces(cast)
            val externalCastFaces = castCube.surfaceArea()

            allCastFaces - externalCastFaces
        }
}

private val adjacentVectors = listOf(
    Vector3(+1, 0, 0),
    Vector3(-1, 0, 0),
    Vector3(0, +1, 0),
    Vector3(0, -1, 0),
    Vector3(0, 0, +1),
    Vector3(0, 0, -1),
)

private fun parsePoints(input: List<String>): Set<Point3D> = input
    .mapTo(mutableSetOf()) { it.split(",").map(String::toLong).let { (x, y, z) -> Point3D(x, y, z) } }

private fun countFaces(points: Collection<Point3D>): Int {
    val gatheredPoints = mutableSetOf<Point3D>()
    var facesCount = 0

    for (p in points) {
        facesCount += 6

        for (v in adjacentVectors) {
            if ((p + v) in gatheredPoints) facesCount -= 2
        }

        gatheredPoints += p
    }

    return facesCount
}

internal infix operator fun Cube.contains(point: Point3D): Boolean = when {
    point.x !in this.xs -> false
    point.y !in this.ys -> false
    point.z !in this.zs -> false
    else -> true
}

private fun Cube.surfaceArea(): Long =
    (xs.size() * ys.size() * 2) + (xs.size() * zs.size() * 2) + (zs.size() * ys.size() * 2)

private fun Cube.volume(): Long = xs.size() * ys.size() * zs.size()

private fun LongRange.extendBy(n: Long) = (first - n)..(last + n)

private fun Collection<Point3D>.boundingCube(): Cube {
    var minX: Long = first().x
    var minY: Long = first().y
    var minZ: Long = first().z
    var maxX: Long = first().x
    var maxY: Long = first().y
    var maxZ: Long = first().z

    for (p in this.asSequence().drop(1)) {
        minX = minOf(minX, p.x)
        minY = minOf(minY, p.y)
        minZ = minOf(minZ, p.z)

        maxX = maxOf(maxX, p.x)
        maxY = maxOf(maxY, p.y)
        maxZ = maxOf(maxZ, p.z)
    }

    return Cube(
        minX..maxX,
        minY..maxY,
        minZ..maxZ
    )
}

private operator fun Point3D.plus(v: Vector3) = Point3D(x + v.x, y + v.y, z + v.z)

data class Point3D(
    val x: Long,
    val y: Long,
    val z: Long
)