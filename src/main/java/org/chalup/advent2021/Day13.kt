package org.chalup.advent2021

import org.chalup.utils.Point
import org.chalup.utils.bounds
import org.chalup.utils.match

object Day13 {
    fun task1(input: List<String>): Int = input
        .let(this::parseInstructions)
        .let { instructions -> processFold(instructions.points, instructions.folds.first()) }
        .count()

    fun task2(input: List<String>): String = input
        .let(this::parseInstructions)
        .let { instructions -> instructions.folds.fold(instructions.points, this::processFold) }
        .let(this::dump)

    private fun dump(points: Set<Point>) = buildString {
        appendLine()

        val bounds = points.bounds()

        for (y in (bounds.topLeft.y)..(bounds.bottomRight.y)) {
            for (x in (bounds.topLeft.x)..(bounds.bottomRight.x)) {
                append(if (Point(x, y) in points) '*' else '.')
            }
            appendLine()
        }
    }

    private fun processFold(points: Set<Point>, fold: Fold): Set<Point> =
        points.mapTo(mutableSetOf()) { point ->
            when (fold) {
                is Fold.Horizontal ->
                    if (point.y < fold.alongY) point
                    else point.copy(y = fold.alongY - (point.y - fold.alongY))
                is Fold.Vertical ->
                    if (point.x < fold.alongX) point
                    else point.copy(x = fold.alongX - (point.x - fold.alongX))
            }
        }

    private fun parseInstructions(input: List<String>): Instructions {
        val points = mutableSetOf<Point>()
        val folds = mutableListOf<Fold>()

        val iterator = input.iterator()
        for (line in iterator) {
            if (line.isBlank()) break

            val (x, y) = line.split(",").map(String::toInt)
            points += Point(x, y)
        }

        for (line in iterator) {
            folds += match<Fold>(line) {
                pattern("""fold along x=(\d+)""") { (x) -> Fold.Vertical(x.toInt()) }
                pattern("""fold along y=(\d+)""") { (y) -> Fold.Horizontal(y.toInt()) }
            }
        }

        return Instructions(points, folds)
    }

    private sealed class Fold {
        data class Horizontal(val alongY: Int) : Fold()
        data class Vertical(val alongX: Int) : Fold()
    }

    private data class Instructions(
        val points: Set<Point>,
        val folds: List<Fold>
    )
}
