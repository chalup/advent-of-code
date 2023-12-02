package org.chalup.advent2020

object Day17 {
    fun task1(input: List<String>) = input
        .let(::parse2dSlice)
        .map(Point2d::to3d)
        .toSet()
        .let { initialSetup ->
            (1..6).fold(initialSetup) { activeCubes, _ -> simulate(activeCubes) }
        }
        .size

    fun task2(input: List<String>) = input
        .let(::parse2dSlice)
        .map(Point2d::to3d)
        .map(Point3d::to4d)
        .toSet()
        .let { initialSetup ->
            (1..6).fold(initialSetup) { activeCubes, _ -> simulate(activeCubes) }
        }
        .size


    private inline fun <reified T : Point> simulate(activeCubes: Set<T>): Set<T> {
        val inactiveNeighbours: Set<Point> = activeCubes
            .flatMapTo(mutableSetOf()) { it.neighbours() }
            .minus(activeCubes)

        return buildSet {
            // cubes that stayed active
            activeCubes.filterTo(this) { cube ->
                val activeNeighbours = cube.neighbours()
                    .filter(activeCubes::contains)
                    .take(4)
                    .count()

                activeNeighbours == 2 || activeNeighbours == 3
            }

            // cubes that got activated
            inactiveNeighbours
                .filterIsInstance<T>()
                .filterTo(this) { cube ->
                    val activeNeighbours = cube.neighbours()
                        .filter(activeCubes::contains)
                        .take(4)
                        .count()

                    activeNeighbours == 3
                }
        }
    }
}

private fun parse2dSlice(lines: List<String>) = buildSet<Point2d> {
    lines.forEachIndexed { y, line ->
        line.forEachIndexed { x, char ->
            if (char == '#') {
                add(Point2d(x, y))
            }
        }
    }
}

private interface Point {
    fun neighbours(): Sequence<Point>
}

private data class Point2d(val x: Int, val y: Int) {
    fun to3d() = Point3d(x, y, z = 0)
}

private data class Point3d(val x: Int, val y: Int, val z: Int) : Point {
    fun to4d() = Point4d(x, y, z, w = 0)

    override fun neighbours(): Sequence<Point3d> = sequence {
        for (dx in -1..1) {
            for (dy in -1..1) {
                for (dz in -1..1) {
                    if (dx != 0 || dy != 0 || dz != 0) {
                        yield(Point3d(x + dx, y + dy, z + dz))
                    }
                }
            }
        }
    }
}

private data class Point4d(val x: Int, val y: Int, val z: Int, val w: Int) : Point {
    override fun neighbours(): Sequence<Point4d> = sequence {
        for (dx in -1..1) {
            for (dy in -1..1) {
                for (dz in -1..1) {
                    for (dw in -1..1) {
                        if (dx != 0 || dy != 0 || dz != 0 || dw != 0) {
                            yield(Point4d(x + dx, y + dy, z + dz, w + dw))
                        }
                    }
                }
            }
        }
    }
}