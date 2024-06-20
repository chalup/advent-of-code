package org.chalup.advent2020

import org.chalup.utils.textBlocks

object Day20 {
    fun task1(input: List<String>): Long {
        val tiles = textBlocks(input)
            .filter { it.isNotEmpty() }
            .map {
                val tileId = it.first().substringAfter("Tile ").substringBefore(":").toLong()

                Tile(tileId, it.drop(1))
            }

        fun Tile.edges() = buildList {
            add(map.first())
            add(map.joinToString(separator = "") { it.last().toString() })
            add(map.last().reversed())
            add(map.joinToString(separator = "") { it.first().toString() }.reversed())
        }.flatMap {
            buildList {
                add(it)
                add(it.reversed())
            }
        }

        val edges = tiles
            .flatMap { tile -> tile.edges() }
            .groupingBy { it }
            .eachCount()

        val cornerTiles = tiles
            .associateWith { tile -> tile.edges().map { edges[it] ?: 0 }.groupingBy { it }.eachCount() }
            .filterValues { it[1] == 4 }
            .keys

        return cornerTiles
            .map { it.id }
            .fold(1L, Long::times)
    }

    private data class Tile(
        val id: Long,
        val map: List<String>
    )
}
