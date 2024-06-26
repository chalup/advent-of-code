package org.chalup.advent2017

object Day21 {
    fun task1(input: List<String>): Int = countPixels(input, iterations = 5)
    fun task2(input: List<String>): Int = countPixels(input, iterations = 18)

    private fun countPixels(input: List<String>, iterations: Int): Int {
        val simpleTransforms = input
            .flatMap { line ->
                val (from, to) = line.split(" => ")

                val toTiles: List<String> = when (to.length) {
                    11 -> listOf(to)
                    else -> {
                        to.split("/").chunked(2).flatMap { rows ->
                            listOf(0..1, 2..3).map { colRange ->
                                rows.joinToString(separator = "/") { it.substring(colRange) }
                            }
                        }
                    }
                }

                rotationsAndFlips(from).map { it to toTiles }
            }
            .toSet()
            .let { transforms -> transforms.toMap().also { check(it.size == transforms.size) } }

        val combinedTransforms = simpleTransforms
            .filterKeys { it.length == 11 }
            .mapValues { (_, tiles) ->
                tiles
                    .map { simpleTransforms.getValue(it).single().split("/") }
                    .chunked(2)
                    .flatMap { (leftTile, rightTile) ->
                        leftTile.zip(rightTile) { l, r -> l + r }
                    }
                    .chunked(2) { rows ->
                        listOf(0..1, 2..3, 4..5).map { colRange ->
                            rows.joinToString(separator = "/") { it.substring(colRange) }
                        }
                    }
                    .flatten()
                    .toList()
            }

        val fractalSequence = generateSequence(mapOf(".#./..#/###" to 1) to iterations) { (tiles, stepsToGo) ->
            if (stepsToGo == 0) {
                null
            } else {
                val (transforms, stepsPerformed) = if (tiles.keys.all { it.length == 11 } && stepsToGo >= 2) {
                    combinedTransforms to 2
                } else {
                    simpleTransforms to 1
                }

                val nextTiles = tiles
                    .flatMap { (tile, count) ->
                        transforms.getValue(tile).map { it to count }
                    }
                    .groupBy { (tile, _) -> tile }
                    .mapValues { (_, counts) -> counts.sumOf { (_, count) -> count } }

                nextTiles to (stepsToGo - stepsPerformed)
            }
        }

        return fractalSequence
            .last()
            .let { (tiles, _) -> tiles.entries }
            .sumOf { (tile, count) -> count * tile.count { it == '#' } }
    }

    private fun rotationsAndFlips(tileSpec: String): List<String> = buildList {
        fun rotationSequence(tile: List<String>) = generateSequence(tile) { t ->
            t.first().indices.reversed().map { column ->
                t.map { it[column] }.joinToString(separator = "")
            }
        }

        fun flip(tile: List<String>) = tile.map { it.reversed() }

        val tile = tileSpec.split("/")

        rotationSequence(tile).take(4).mapTo(this) { it.joinToString(separator = "/") }
        rotationSequence(flip(tile)).take(4).mapTo(this) { it.joinToString(separator = "/") }
    }
}