package org.chalup.advent2021

import org.chalup.utils.Point
import org.chalup.utils.bounds
import org.chalup.utils.contains

object Day20 {
    fun task1(input: List<String>): Int = input
        .let(this::parse)
        .let { (algorithm, image) -> enhance(image, algorithm) }
        .drop(2)
        .first()
        .count()

    fun task2(input: List<String>): Int = input
        .let(this::parse)
        .let { (algorithm, image) -> enhance(image, algorithm) }
        .drop(50)
        .first()
        .count()

    private fun enhance(initialImage: Set<Point>, algorithm: List<Boolean>) = generateSequence(initialImage to false) { (image, surroundingTiles) ->
        val bounds = image.bounds()

        val newImage = buildSet {
            for (y in (bounds.topLeft.y - 1)..(bounds.bottomRight.y + 1))
                for (x in (bounds.topLeft.x - 1)..(bounds.bottomRight.x + 1)) {
                    var pixel = 0

                    for (dy in -1..1)
                        for (dx in -1..1) {
                            pixel = pixel shl 1

                            val testedPoint = Point(x + dx, y + dy)

                            if ((testedPoint in bounds && testedPoint in image) || (testedPoint !in bounds && surroundingTiles)) {
                                pixel += 1
                            }
                        }

                    if (algorithm[pixel]) add(Point(x, y))
                }
        }

        newImage to !surroundingTiles
    }.map { (image, _) -> image }

    private fun parse(input: List<String>) = Input(
        enhancementAlgorithm = input.first().map { it == '#' },
        initialImage = buildSet {
            input.drop(2).forEachIndexed { y, line ->
                line.forEachIndexed { x, c ->
                    if (c == '#') add(Point(x, y))
                }
            }
        }
    )

    private data class Input(
        val enhancementAlgorithm: List<Boolean>,
        val initialImage: Set<Point>,
    )
}

