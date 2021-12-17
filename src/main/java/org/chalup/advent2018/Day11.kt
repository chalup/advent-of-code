package org.chalup.advent2018

import org.chalup.utils.Point
import org.chalup.utils.Rect
import org.chalup.utils.points

object Day11 {
    @Suppress("ReplaceSingleLineLet")
    fun powerLevel(position: Point, gridSerialNo: Int): Int {
        val (x, y) = position
        val rackId = (x + 10)

        return rackId
            .let { it * y }
            .let { it + gridSerialNo }
            .let { it * rackId }
            .let { (it % 1000) / 100 }
            .let { it - 5 }
    }

    data class Coordinates(val position: Point,
                           val areaSize: Int)

    fun findBestAreas(gridSize: Int, gridSerialNo: Int): List<Pair<Coordinates, Int>> {
        var previousAreaSizeData = emptyMap<Point, Int>().withDefault { 0 }
        val bestResultPerArea = mutableListOf<Pair<Coordinates, Int>>()
        (1..gridSize)
            .forEach { areaSize ->
                println("Checking area size: $areaSize")

                val currentAreaSizeData = Rect(1, 1, gridSize - areaSize + 1, gridSize - areaSize + 1)
                    .points()
                    .map { startingPoint ->
                        val additionalPoints = (
                                (startingPoint.x until startingPoint.x + areaSize).map { x -> Point(x, startingPoint.y + areaSize - 1) } +
                                        (startingPoint.y until startingPoint.y + areaSize).map { y -> Point(startingPoint.x + areaSize - 1, y) }
                                ).toSet()

                        val areaPower = previousAreaSizeData.getValue(startingPoint) + additionalPoints.sumOf { powerLevel(it, gridSerialNo) }

                        startingPoint to areaPower
                    }

                currentAreaSizeData
                    .maxByOrNull { (_, power) -> power }
                    ?.also { (point, power) ->
                        bestResultPerArea.add(Coordinates(point, areaSize) to power)
                    }

                previousAreaSizeData = currentAreaSizeData.toMap()
            }

        return bestResultPerArea
    }
}