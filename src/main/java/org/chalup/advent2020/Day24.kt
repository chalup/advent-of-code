package org.chalup.advent2020

object Day24 {
    fun task1(input: List<String>): Int = getInitialBlackTilesSet(input).size
    fun task2(input: List<String>): Int {
        val mosaicSequence = generateSequence(getInitialBlackTilesSet(input)) { blackTiles ->
            val whiteTilesAroundBlackTiles = blackTiles
                .flatMap { it.surrounding() }
                .minus(blackTiles)

            val flippedWhiteTiles = whiteTilesAroundBlackTiles
                .filter { whiteTile -> whiteTile.surrounding().count { it in blackTiles } == 2 }

            val remainingBlackTiles = blackTiles
                .filter { blackTile -> blackTile.surrounding().count { it in blackTiles } in 1..2 }

            (flippedWhiteTiles + remainingBlackTiles).toSet()
        }

        return mosaicSequence
            .drop(100)
            .first()
            .count()
    }

    private fun HexTile.surrounding() = sequence {
        yield(HexTile(ew + 2, ns))
        yield(HexTile(ew - 2, ns))
        yield(HexTile(ew - 1, ns + 1))
        yield(HexTile(ew + 1, ns + 1))
        yield(HexTile(ew - 1, ns - 1))
        yield(HexTile(ew + 1, ns - 1))
    }

    private fun getInitialBlackTilesSet(input: List<String>) = input
        .map { line ->
            var ew = 0
            var ns = 0

            val i = line.iterator()
            while (i.hasNext()) {
                when (i.next()) {
                    's' -> {
                        ns--
                        when (i.next()) {
                            'e' -> ew++
                            'w' -> ew--
                        }
                    }

                    'n' -> {
                        ns++
                        when (i.next()) {
                            'e' -> ew++
                            'w' -> ew--
                        }
                    }

                    'e' -> ew += 2
                    'w' -> ew -= 2
                }
            }

            HexTile(ew, ns)
        }
        .groupingBy { it }
        .eachCount()
        .filterValues { it % 2 == 1 }
        .keys

    private data class HexTile(val ew: Int, val ns: Int)
}
