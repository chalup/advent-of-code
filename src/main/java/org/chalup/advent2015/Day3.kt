package org.chalup.advent2015

object Day3 {
    private data class Point(val x: Int, val y: Int)

    private fun route(directions: String): List<Point> = directions
        .fold(listOf(Point(0, 0))) { route, direction ->
            val lastLocation = route.last()
            val newLocation = with(lastLocation) {
                when (direction) {
                    '>' -> copy(x = x + 1)
                    '<' -> copy(x = x - 1)
                    '^' -> copy(y = y + 1)
                    'v' -> copy(y = y - 1)
                    else -> throw UnsupportedOperationException("Don't know how to handle direction '$direction'")
                }
            }
            route + newLocation
        }

    fun countHousesOnRoute(directions: String) = route(directions).toSet().size

    fun countHousesVisitedBySantaAndRoboSanta(directions: String) =
        listOf(directions.filterIndexed { index, _ -> index % 2 == 0 },
               directions.filterIndexed { index, _ -> index % 2 == 1 })
            .flatMap { route(it) }
            .toSet()
            .size
}