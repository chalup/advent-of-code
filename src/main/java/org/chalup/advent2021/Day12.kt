package org.chalup.advent2021

object Day12 {
    fun task1(input: List<String>): Int = input
        .let(this::parseRoutes)
        .let { routes -> exploreCaves(routes) }
        .count()

    fun task2(input: List<String>): Int = input
        .let(this::parseRoutes)
        .let { routes -> exploreCaves(routes, allowVisitingSingleSmallCaveOnce = true) }
        .count()

    // 122061 too high

    private fun String.isSmallCave() = this[0].isLowerCase()

    private fun exploreCaves(
        routes: Map<String, List<String>>,
        allowVisitingSingleSmallCaveOnce: Boolean = false
    ): List<String> {
        fun Traversal.explore(cave: String): List<String> {
            val nextStep = copy(
                path = path + cave,
                visitedSmallCaves = if (cave.isSmallCave()) visitedSmallCaves + cave else visitedSmallCaves,
                smallCaveVisitedTwice = smallCaveVisitedTwice
                    ?: cave.takeIf { cave in visitedSmallCaves }
            )

            return if (cave == "end") return listOf(nextStep.path.joinToString(separator = ","))
            else routes[cave]
                .orEmpty()
                .filterNot { it == "start" }
                .let { possibleRoutes ->
                    if (nextStep.smallCaveVisitedTwice == null) possibleRoutes
                    else possibleRoutes.filterNot { it in nextStep.visitedSmallCaves }
                }
                .flatMap { nextStep.explore(it) }
        }

        return Traversal(
            smallCaveVisitedTwice = "start".takeUnless { allowVisitingSingleSmallCaveOnce }
        ).explore("start")
    }

    private fun parseRoutes(input: List<String>): Map<String, List<String>> = input
        .asSequence()
        .flatMap {
            val (from, to) = it.split("-")

            sequence {
                yield(from to to)
                yield(to to from)
            }
        }
        .groupBy { (from, _) -> from }
        .mapValues { (_, entries) -> entries.map { (_, to) -> to } }

    private data class Traversal(
        val path: List<String> = emptyList(),
        val visitedSmallCaves: Set<String> = emptySet(),
        val smallCaveVisitedTwice: String?
    )
}
