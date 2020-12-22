package org.chalup.advent2020

import org.chalup.advent2020.Day11.Tile.*
import org.chalup.utils.*

object Day11 {
    fun task1(input: List<String>) = parseInput(input)
        .let { map ->
            generateSequence(map) { prev ->
                prev
                    .mapValues { (point, tile) ->
                        val occupiedNeighbors = prev.surroundings(point).count { it == OCCUPIED_SEAT }

                        when {
                            tile == EMPTY_SEAT && occupiedNeighbors == 0 -> OCCUPIED_SEAT
                            tile == OCCUPIED_SEAT && occupiedNeighbors >= 4 -> EMPTY_SEAT
                            else -> tile
                        }
                    }
                    .takeIf { it != prev }
            }
        }
        .last()
        .let { it.count { (_, tile) -> tile == OCCUPIED_SEAT } }

    fun task2(input: List<String>) = parseInput(input)
        .let { map ->
            val bounds = map.keys.bounds()
            val deltas = (-1..+1)
                .flatMap { dx ->
                    (-1..+1).map { dy -> Vector(dx, dy) }
                }
                .filterNot { (dx, dy) -> dx == 0 && dy == 0 }

            val observedSeats = map
                .mapValues { (point, _) ->
                    deltas.mapNotNull { vector ->
                        generateSequence(point) { (it + vector).takeIf(bounds::contains) }
                            .drop(1)
                            .firstOrNull(map::containsKey)
                    }
                }

            generateSequence(map) { prev ->
                prev
                    .mapValues { (point, tile) ->
                        val occupiedNeighbors = observedSeats.getValue(point).count { prev[it] == OCCUPIED_SEAT }

                        when {
                            tile == EMPTY_SEAT && occupiedNeighbors == 0 -> OCCUPIED_SEAT
                            tile == OCCUPIED_SEAT && occupiedNeighbors >= 5 -> EMPTY_SEAT
                            else -> tile
                        }
                    }
                    .takeIf { it != prev }
            }
        }
        .last()
        .let { it.count { (_, tile) -> tile == OCCUPIED_SEAT } }

    private fun parseInput(input: List<String>) = input
        .flatMapIndexed { rowIndex, row ->
            row.mapIndexed { colIndex, tileChar ->
                Point(colIndex, rowIndex) to enumOf<Tile> { tile -> tile.symbol == tileChar }!!
            }
        }
        .filterNot { (_, tile) -> tile == FLOOR }
        .toMap()

    enum class Tile(val symbol: Char) {
        EMPTY_SEAT('L'),
        OCCUPIED_SEAT('#'),
        FLOOR('.')
    }

    private fun <T> Map<Point, T>.surroundings(point: Point) = sequence<T> {
        val (x, y) = point

        for (dx in -1..1) {
            for (dy in -1..1) {
                if (dx == 0 && dy == 0) continue

                val tile = get(Point(x + dx, y + dy))
                if (tile != null) yield(tile)
            }
        }
    }

}

private inline fun <reified T : Enum<T>> enumOf(predicate: (T) -> Boolean) = enumValues<T>().find(predicate)