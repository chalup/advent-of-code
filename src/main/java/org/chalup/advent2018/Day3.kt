package org.chalup.advent2018

object Day3 {
    data class Claim(val id: Int, val x: Int, val y: Int, val width: Int, val height: Int)
    data class ClaimedCoords(val claimId: Int, val x: Int, val y: Int)

    private val claimRegex = """^#(\d+) @ (\d+),(\d+): (\d+)x(\d+)$""".toRegex()

    private fun parseClaim(claim: String): Claim = claimRegex
        .matchEntire(claim)!!.groupValues.drop(1)
        .map { it.toInt() }
        .let { (id, x, y, w, h) -> Claim(id, x, y, w, h) }

    private fun Claim.allClaimedCoords(): Set<ClaimedCoords> = mutableSetOf<ClaimedCoords>().apply {
        for (px in x until x + width) {
            for (py in y until y + height) {
                add(ClaimedCoords(id, px, py))
            }
        }
    }

    fun overlap(claims: List<String>): Int =
        claims
            .map { parseClaim(it) }
            .flatMap { it.allClaimedCoords() }
            .groupingBy { it.x to it.y }
            .eachCount()
            .count { (_, claims) -> claims > 1 }

    fun findNonOverlappingClaim(claims: List<String>): Int =
        claims
            .map { parseClaim(it) }
            .flatMap { it.allClaimedCoords() }
            .groupBy { it.x to it.y }
            .values
            .map { claimsGroup -> claimsGroup.map { it.claimId }.toSet() }
            .let { claimsGroups ->
                val overlappingClaims = claimsGroups
                    .filter { it.size > 1 }
                    .flatten()
                    .toSet()

                claimsGroups
                    .filter { it.intersect(overlappingClaims).isEmpty() }
                    .flatten()
                    .toSet()
                    .single()
            }
}