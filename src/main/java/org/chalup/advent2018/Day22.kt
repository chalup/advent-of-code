package org.chalup.advent2018

import org.chalup.utils.Point
import org.chalup.utils.Rect
import org.chalup.utils.points

object Day22 {
    enum class TerrainType(val riskLevel: Int) {
        ROCKY(0),
        WET(1),
        NARROW(2)
    }

    private val CAVE_MOUTH = Point(0, 0)

    fun calculateData(depth: Int, target: Point): Map<Point, TerrainType> {
        val area = Rect(topLeft = CAVE_MOUTH,
                        bottomRight = target)

        val geoIndexMap: MutableMap<Point, Int> = mutableMapOf()
        val erosionLevels: MutableMap<Point, Int> = mutableMapOf()

        for (point in area.points().sortedWith(compareBy({ it.y }, { it.x }))) {
            val (x, y) = point

            geoIndexMap[point] = when {
                point == CAVE_MOUTH -> 0
                point == target -> 0
                y == 0 -> x * 16807
                x == 0 -> y * 48271
                else -> erosionLevels.getValue(Point(x, y - 1)) * erosionLevels.getValue(Point(x - 1, y))
            }

            erosionLevels[point] = (geoIndexMap.getValue(point) + depth) % 20183
        }


        return erosionLevels.mapValues { (_, erosionLevel) ->
            when (erosionLevel % 3) {
                0 -> TerrainType.ROCKY
                1 -> TerrainType.WET
                2 -> TerrainType.NARROW
                else -> throw IllegalStateException("Impossiburu!")
            }
        }
    }

    fun estimateTotalRisk(depth: Int, target: Point) = calculateData(depth, target).values.sumBy { it.riskLevel }
}