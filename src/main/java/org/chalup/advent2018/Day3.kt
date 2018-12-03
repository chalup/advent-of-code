package org.chalup.advent2018

object Day3 {
    data class Claim(val x: Int, val y: Int, val width: Int, val height: Int)
    data class Coords(val x: Int, val y: Int)

    private val claimRegex = """^#\d+ @ (\d+),(\d+): (\d+)x(\d+)$""".toRegex()

    private fun parseClaim(claim: String): Claim = claimRegex
        .matchEntire(claim)!!.groupValues.drop(1)
        .map { it.toInt() }
        .let { (x, y, w, h) -> Claim(x, y, w, h) }

    private fun Claim.allClaimedCoords(): Set<Coords> = mutableSetOf<Coords>().apply {
        for (px in x until x + width) {
            for (py in y until y + height) {
                add(Coords(px, py))
            }
        }
    }

    fun overlap(claims: List<String>): Int =
        claims
            .map { parseClaim(it) }
            .flatMap { it.allClaimedCoords() }
            .groupingBy { it }
            .eachCount()
            .count { (_, claims) -> claims > 1 }
}