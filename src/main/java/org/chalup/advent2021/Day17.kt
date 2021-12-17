package org.chalup.advent2021

import org.chalup.utils.Point
import org.chalup.utils.Rect
import org.chalup.utils.Vector
import org.chalup.utils.match
import org.chalup.utils.plus

object Day17 {
    fun task1(input: List<String>): Int = input
        .single()
        .let(this::parseTargetArea)
        .let { targetArea ->
            // I assume the target area is below the (0, 0)

            check(targetArea.topLeft.y < 0)

            // The way the gravity works in this assignment, the probe will
            // eventually end up at y=0 and its velocity will be equal to
            // the launch velocity plus 1.
            // If we want to maximize the vertical launch velocity, we should
            // ensure the probe will hit exactly the bottom of the target area
            // in a single step.

            val launchVelocity = (-targetArea.bottom) - 1

            val highestPoint = (launchVelocity + 1) * launchVelocity / 2

            highestPoint
        }

    fun task2(input: List<String>): Int = input
        .single()
        .let(this::parseTargetArea)
        .let { targetArea ->
            // I assume the target area is below the surface level and on the right

            check(targetArea.topLeft.y < 0)
            check(targetArea.topLeft.x > 0)

            val maxVerticalVelocity = (-targetArea.bottom) - 1
            val minVerticalVelocity = targetArea.bottom

            val maxHorizontalVelocity = targetArea.bottomRight.x
            val minHorizontalVelocity = (1..targetArea.topLeft.x)
                .first { v -> ((v + 1) * v / 2) >= targetArea.topLeft.x }

            val viableLaunchVectors = sequence {
                for (verticalV in minVerticalVelocity..maxVerticalVelocity)
                    for (horizontalV in minHorizontalVelocity..maxHorizontalVelocity) {
                        val launchVector = Vector(horizontalV, verticalV)

                        val willHitTargetArea = simulateProbe(launchVector)
                            .mapNotNull { point ->
                                when {
                                    point in targetArea -> true
                                    point.x > targetArea.bottomRight.x -> false
                                    point.y < targetArea.bottomRight.y -> false
                                    else -> null
                                }
                            }
                            .first()

                        if (willHitTargetArea) yield(launchVector)
                    }

            }

            viableLaunchVectors.count()
        }

    private fun simulateProbe(launchVector: Vector) = sequence {
        var vector = launchVector
        var position = Point(0, 0)

        while (true) {
            yield(position)
            position += vector
            vector = vector.copy(
                dx = (vector.dx - 1).coerceAtLeast(0),
                dy = vector.dy - 1
            )
        }
    }

    private operator fun Rect.contains(point: Point): Boolean = when {
        point.x < topLeft.x -> false
        point.x > bottomRight.x -> false
        point.y > topLeft.y -> false
        point.y < bottomRight.y -> false
        else -> true
    }

    private fun parseTargetArea(input: String) = match<Rect>(input) {
        pattern("""target area: x=([-\d]+)..([-\d]+), y=([-\d]+)..([-\d]+)""") { (x1, x2, y1, y2) ->
            Rect(
                minOf(x1.toInt(), x2.toInt()),
                maxOf(y1.toInt(), y2.toInt()),
                maxOf(x1.toInt(), x2.toInt()),
                minOf(y1.toInt(), y2.toInt()),
            )
        }
    }
}
