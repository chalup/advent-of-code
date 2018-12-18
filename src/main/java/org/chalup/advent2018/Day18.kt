package org.chalup.advent2018

import org.chalup.advent2018.Day18.Tile.LUMBERYARD
import org.chalup.advent2018.Day18.Tile.OPEN
import org.chalup.advent2018.Day18.Tile.WOOD
import org.chalup.utils.Point
import org.chalup.utils.bounds

object Day18 {
    enum class Tile(val symbol: Char) {
        WOOD('|'),
        LUMBERYARD('#'),
        OPEN('.')
    }

    private data class Stats(val openTiles: Int,
                             val woodedTiles: Int,
                             val lumberyards: Int)

    data class World(val map: Map<Point, Tile>) {
        private val bounds = map.keys.bounds()

        private fun stats(): Stats =
            map
                .values
                .groupingBy { it }
                .eachCount()
                .let { counts ->
                    Stats(counts.getOrDefault(OPEN, 0),
                          counts.getOrDefault(WOOD, 0),
                          counts.getOrDefault(LUMBERYARD, 0))
                }

        @Suppress("unused")
        fun print() {
            val (tl, br) = bounds
            for (y in tl.y..br.y) {
                for (x in tl.x..br.x) {
                    val tile = map[Point(x, y)] ?: throw IllegalStateException("Can't find tile at [$x, $y]!")
                    print(tile.symbol)
                }
                println()
            }
            println("-".repeat(40))
        }

        val resourceValue: Int by lazy {
            val (_, woods, lumberyards) = stats()
            woods * lumberyards
        }
    }

    fun simulate(initialWorld: World) = generateSequence(initialWorld) { world ->
        world
            .map
            .map { (point, tile) ->
                val adjacentTiles = point
                    .adjacentPoints()
                    .mapNotNull { world.map[it] }

                val newTile = when (tile) {
                    OPEN -> if (adjacentTiles.count { it == WOOD } >= 3) WOOD else OPEN
                    WOOD -> if (adjacentTiles.count { it == LUMBERYARD } >= 3) LUMBERYARD else WOOD
                    LUMBERYARD -> if (
                        adjacentTiles.count { it == LUMBERYARD } >= 1 &&
                        adjacentTiles.count { it == WOOD } >= 1) LUMBERYARD else OPEN
                }

                point to newTile
            }
            .toMap()
            .let { World(it) }
    }

    fun readWorld(input: List<String>): World =
        input
            .mapIndexed { y, row ->
                row.mapIndexed { x, symbol ->
                    Tile
                        .values()
                        .first { it.symbol == symbol }
                        .let { tile ->
                            Point(x, y) to tile
                        }
                }
            }
            .flatten()
            .toMap()
            .let { World(it) }

    fun part1(input: List<String>, targetGeneration: Int = 10) =
        simulate(readWorld(input))
            .drop(targetGeneration)
            .first()
            .resourceValue

    fun part2(input: List<String>, targetGeneration: Int = 1_000_000_000) =
        simulate(readWorld(input))
            .let { simulation ->
                val worldsCache = mutableMapOf<World, Int>()

                simulation.forEachIndexed { index, world ->
                    val cachedIndex = worldsCache[world]

                    if (cachedIndex != null) {
                        val cycleLength = index - cachedIndex
                        val targetIndexInCycle = (targetGeneration - index) % cycleLength
                        val targetIndexInCache = cachedIndex + targetIndexInCycle

                        return@let worldsCache
                            .entries
                            .first { (_, index) -> index == targetIndexInCache }
                            .key
                    } else {
                        worldsCache[world] = index
                    }
                }

                throw IllegalStateException("Simulation ended prematurely")
            }
            .resourceValue
}

private fun Point.adjacentPoints(): List<Point> =
    (-1..1)
        .map { dy ->
            (-1..1).map { dx ->
                Point(x + dx, y + dy)
            }
        }
        .flatten()
        .let { it - this }
