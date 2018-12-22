package org.chalup.advent2018

import org.chalup.advent2018.Day22.Cave.Companion.CAVE_MOUTH
import org.chalup.utils.Point
import org.chalup.utils.Rect
import org.chalup.utils.points

object Day22 {
    enum class TerrainType(val riskLevel: Int) {
        ROCKY(0),
        WET(1),
        NARROW(2)
    }

    class Cave(private val depth: Int,
               private val target: Point) {

        private val geoIndexMap: MutableMap<Point, Int> = mutableMapOf()
        private val erosionLevels: MutableMap<Point, Int> = mutableMapOf()

        private fun geoIndex(location: Point): Int = geoIndexMap.getOrPut(location) {
            when {
                location == CAVE_MOUTH -> 0
                location == target -> 0
                location.y == 0 -> location.x * 16807
                location.x == 0 -> location.y * 48271
                else -> erosionLevel(Point(location.x, location.y - 1)) *
                        erosionLevel(Point(location.x - 1, location.y))
            }
        }

        private fun erosionLevel(location: Point): Int = erosionLevels.getOrPut(location) {
            (geoIndex(location) + depth) % 20183
        }

        operator fun get(location: Point) = TerrainType.values()[erosionLevel(location) % 3]

        companion object {
            val CAVE_MOUTH = Point(0, 0)
        }
    }

    fun estimateTotalRisk(depth: Int, target: Point) = Cave(depth, target).let { cave ->
        Rect(topLeft = CAVE_MOUTH, bottomRight = target)
            .points()
            .sumBy { cave[it].riskLevel }
    }
}