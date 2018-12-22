package org.chalup.advent2018

object Day20 {
    data class RoutesGroup(val minLength: Int,
                           val maxLength: Int)

    fun part1(input: String): Int = processGroup(input.asIterable().iterator()).maxLength

    fun processGroup(input: Iterator<Char>): RoutesGroup {
        var routesChain = emptyList<RoutesGroup>()
        var alternativeRoutes = emptySet<RoutesGroup>()
        var currentRouteLength: Int? = null

        fun appendCurrentPath() {
            currentRouteLength?.run { routesChain += RoutesGroup(this, this) }
            currentRouteLength = null
        }

        fun List<RoutesGroup>.combine(): RoutesGroup {
            val combinedLength = dropLast(1).sumBy { it.minLength } + last().maxLength
            return RoutesGroup(combinedLength, combinedLength)
        }

        fun Set<RoutesGroup>.combine() = RoutesGroup(
            map { it.minLength }.min()!!,
            map { it.maxLength }.max()!!
        )

        input.forEach {
            when (it) {
                '^' -> Unit
                '(' -> {
                    appendCurrentPath()
                    routesChain += processGroup(input)
                }
                '$', ')' -> {
                    appendCurrentPath()
                    alternativeRoutes += routesChain.combine()
                    return alternativeRoutes.combine()
                }
                '|' -> {
                    appendCurrentPath()
                    currentRouteLength = 0
                    alternativeRoutes += routesChain.combine()
                    routesChain = emptyList()
                }
                'N', 'E', 'S', 'W' -> currentRouteLength = (currentRouteLength ?: 0) + 1
            }
        }

        throw IllegalStateException("Hmm, I should have encountered '$' or ')' token earlier!")
    }
}
