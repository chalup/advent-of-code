package org.chalup.advent2019

import org.chalup.advent2019.Day15.Tile.EMPTY
import org.chalup.advent2019.Day15.Tile.OXYGEN
import org.chalup.advent2019.Day15.Tile.WALL
import org.chalup.advent2019.IntcodeInterpreter.Memory
import org.chalup.advent2019.IntcodeInterpreter.ProgramResult.GeneratedOutput
import org.chalup.utils.Direction
import org.chalup.utils.Direction.D
import org.chalup.utils.Direction.L
import org.chalup.utils.Direction.R
import org.chalup.utils.Direction.U
import org.chalup.utils.Point
import org.chalup.utils.plus

object Day15 {
    fun exploreMap(droidProgram: String): MapInfo {
        val droidDumps = mutableMapOf(Point(0, 0) to Memory(IntcodeInterpreter.parseProgram(droidProgram)))

        val frontier = mutableListOf(Point(0, 0))
        val systemMap = mutableMapOf(Point(0, 0) to EMPTY)
        val pathToOrigin = mutableMapOf(Point(0, 0) to emptyList<Point>())

        while (frontier.isNotEmpty()) {
            val position = frontier.removeAt(0)
            val pathToPosition = pathToOrigin.getValue(position)

            for (direction in Direction.values()) {
                val newPosition = position + direction.vector

                if (newPosition in systemMap) continue

                val intcode = IntcodeInterpreter(droidDumps.getValue(position))
                intcode.sendInput(direction.asRepairDroidInput())

                val discoveredTile = Tile.values()[(intcode.run() as GeneratedOutput).output.toInt()]

                systemMap[newPosition] = discoveredTile

                if (discoveredTile != WALL) {
                    pathToOrigin[newPosition] = pathToPosition + newPosition
                    frontier += newPosition
                    droidDumps[newPosition] = intcode.dump()
                }
            }
        }

        return MapInfo(systemMap, pathToOrigin)
    }

    fun task1(droidProgram: String): Int {
        val mapInfo = exploreMap(droidProgram)

        val oxygenSystemPosition = mapInfo.systemMap.asIterable().single { (_, tile) -> tile == OXYGEN }.key
        val oxygenSystemPath = mapInfo.pathToOrigin.getValue(oxygenSystemPosition)

        return oxygenSystemPath.size
    }

    fun task2(droidProgram: String): Int {
        val map = exploreMap(droidProgram).systemMap

        val (oxygenGenerator, emptyTiles) = map.asIterable().groupBy(keySelector = { (_, tile) -> tile },
            valueTransform = { (point, _) -> point }).let {
            val oxygenGenerator = it.getValue(OXYGEN).single()
            val emptyTiles = it.getValue(EMPTY)

            oxygenGenerator to emptyTiles
        }

        val frontier = mutableListOf(oxygenGenerator)
        val tilesLeft = emptyTiles.toMutableSet()
        val distanceToGenerator = mutableMapOf(oxygenGenerator to 0)

        while (frontier.isNotEmpty() && tilesLeft.isNotEmpty()) {
            val position = frontier.removeAt(0)

            for (direction in Direction.values()) {
                val newPosition = position + direction.vector

                if (!tilesLeft.remove(newPosition)) continue

                distanceToGenerator[newPosition] = distanceToGenerator.getValue(position) + 1
                frontier += newPosition
            }
        }

        return distanceToGenerator.values.maxOrNull()!!
    }

    data class MapInfo(val systemMap: Map<Point, Tile>,
                       val pathToOrigin: Map<Point, List<Point>>)

    enum class Tile { WALL, EMPTY, OXYGEN }
}

private fun Direction.asRepairDroidInput(): Long = when (this) {
    U -> 1
    R -> 4
    D -> 2
    L -> 3
}