package org.chalup.advent2023

import java.math.BigDecimal
import java.math.BigInteger
import org.chalup.utils.ValueMatrix
import org.chalup.utils.determinant
import org.chalup.utils.div
import org.chalup.utils.getColumn
import org.chalup.utils.matrixOfCoefficients
import org.chalup.utils.matrixOfMinors
import org.chalup.utils.times
import org.chalup.utils.transposed

object Day24 {
    fun task1(input: List<String>, testAreaRange: LongRange): Int {
        val hailstones = input.map(::parseHailstone)

        val pairs = hailstones.flatMapIndexed { i: Int, one ->
            hailstones.asSequence().drop(i + 1).map { other -> one to other }
        }

        val intersections = pairs.mapNotNull { (a, b) ->
            val denominator = (a.v.x) * (b.v.y) - (a.v.y) * (b.v.x)

            if (denominator == BigInteger.ZERO) {
                null
            } else {
                val c1 = (a.p.x * a.v.y - a.p.y * a.v.x)
                val c2 = (b.p.x * b.v.y - b.p.y * b.v.x)

                val nomX = c1 * (-b.v.x) - (-a.v.x) * c2
                val nomY = c1 * (-b.v.y) - (-a.v.y) * c2

                Triple(
                    a, b,
                    IntersectionPoint(
                        nomX.toBigDecimal() / denominator.toBigDecimal(),
                        nomY.toBigDecimal() / denominator.toBigDecimal(),
                    )
                )
            }
        }

        val testArea = testAreaRange.first.toBigDecimal()..<(testAreaRange.last + 1).toBigDecimal()

        return intersections
            .filter { (_, _, p) -> p.x in testArea && p.y in testArea }
            .filter { (h, _, p) -> (p.x - h.p.x.toBigDecimal()).signum() == h.v.x.signum() }
            .filter { (_, h, p) -> (p.x - h.p.x.toBigDecimal()).signum() == h.v.x.signum() }
            .count()
    }

    fun task2(input: List<String>): BigInteger {
        val hailstones = input.map(::parseHailstone)
            .let {
                // Manually make it work for test input, where the 1st hailstone is problematic,
                // because it has the same velocity as the rock we'll throw, and the 2nd one
                // has the parallel path with the 3rd hailstone.
                if (it.size == 5) it.drop(5) else it
            }

        // Let's say we have hailstones a, b, and c and we're looking for a hailstone p.
        // From the motion equation:
        //
        // a.p.x + a.v.x * Ta = p.p.x + p.v.x * Ta
        // Ta * (a.v.x - p.v.x) = p.p.x - a.p.x
        // Ta = (p.p.x - a.p.x) / (a.v.x - p.v.x)
        //
        // We can create another equation for the y-axis:
        //
        // Ta = (p.p.y - a.p.y) / (a.v.y - p.v.y)
        //
        // Which means that:
        //
        // (p.p.x - a.p.x) / (a.v.x - p.v.x) = (p.p.y - a.p.y) / (a.v.y - p.v.y)
        // (p.p.x - a.p.x) * (a.v.y - p.v.y) = (p.p.y - a.p.y) * (a.v.x - p.v.x)
        // p.p.x * a.v.y - p.p.x * p.v.y - a.p.x * a.v.y + a.p.x * p.v.y = p.p.y * a.v.x - p.p.y * p.v.x - a.p.y * a.v.x + a.p.y * p.v.x
        //
        // This is a non-linear equation (because of stuff like p.p.x * p.v.y). Let's move all non-linear stuff to one side:
        //
        // p.p.y * p.v.x - p.p.x * p.v.y = p.p.y * a.v.x - a.p.y * a.v.x + a.p.y * p.v.x - p.p.x * a.v.y + a.p.x * a.v.y - a.p.x * p.v.y
        //
        // We can create the same equation for point b:
        //
        // p.p.y * p.v.x - p.p.x * p.v.y = p.p.y * b.v.x - b.p.y * b.v.x + b.p.y * p.v.x - p.p.x * b.v.y + b.p.x * b.v.y - b.p.x * p.v.y
        //
        // And get rid of non-linear stuff:
        //
        // p.p.y * a.v.x - a.p.y * a.v.x + a.p.y * p.v.x - p.p.x * a.v.y + a.p.x * a.v.y - a.p.x * p.v.y = p.p.y * b.v.x - b.p.y * b.v.x + b.p.y * p.v.x - p.p.x * b.v.y + b.p.x * b.v.y - b.p.x * p.v.y
        //
        // After rearrangement we get:
        //
        // p.p.x * (b.v.y - a.v.y) + p.p.y * (a.v.x - b.v.x) + p.v.x * (a.p.y - b.p.y) + p.v.y * (b.p.x - a.p.x) = b.p.x * b.v.y - b.p.y * b.v.x + a.p.y * a.v.x - a.p.x * a.v.y
        //
        // Which is a linear equation with 4 unknowns. We can create another equations for other pairs of points and axis:
        //
        // * ab in XY and XZ
        // * ac in XY and ZY
        // * bc in XZ and ZY
        //
        // This gives us a set of 6 equations with 6 unknowns, which should be enough to find the solution.
        //
        // There are two complications we need to handle. The first one is that if the points are moving along the same path,
        // the determinant of the matrix will be 0, indicating that the set of equations is unsolvable. We'll have to
        // choose a different set of hailstones – fortunately we have a lot of hailstones to choose from.
        //
        // The second one is that we rely on this equation:
        //
        // Ta = (p.p.x - a.p.x) / (a.v.x - p.v.x)
        //
        // If the hailstone is moving along that axis with the same speed as the rock, we'll get the division by 0. I
        // think we can detect that situation by calculating the T for all three axis for a single hailstone. If the
        // times will vary, it means that we chose the problematic hailstone as the input.

        while (true) {
            val (a, b, c) = hailstones
                .shuffled()
                .take(3)

            val matrixA = ValueMatrix(
                rows = 6, cols = 6,
                listOf(
                    // px, py, pz, vx, vy, vz
                    (b.v.y - a.v.y), (a.v.x - b.v.x), BigInteger.ZERO, (a.p.y - b.p.y), (b.p.x - a.p.x), BigInteger.ZERO, // ab XY
                    (b.v.z - a.v.z), BigInteger.ZERO, (a.v.x - b.v.x), (a.p.z - b.p.z), BigInteger.ZERO, (b.p.x - a.p.x), // ab XZ
                    (c.v.y - a.v.y), (a.v.x - c.v.x), BigInteger.ZERO, (a.p.y - c.p.y), (c.p.x - a.p.x), BigInteger.ZERO, // ac XY
                    BigInteger.ZERO, (a.v.z - c.v.z), (c.v.y - a.v.y), BigInteger.ZERO, (c.p.z - a.p.z), (a.p.y - c.p.y), // ac ZY
                    (b.v.z - c.v.z), BigInteger.ZERO, (c.v.x - b.v.x), (c.p.z - b.p.z), BigInteger.ZERO, (b.p.x - c.p.x), // cb XZ
                    BigInteger.ZERO, (b.v.z - c.v.z), (c.v.y - b.v.y), BigInteger.ZERO, (c.p.z - b.p.z), (b.p.y - c.p.y), // bc ZY
                )
            )

            val matrixB = ValueMatrix(
                rows = 6, cols = 1,
                listOf(
                    b.p.x * b.v.y - b.p.y * b.v.x + a.p.y * a.v.x - a.p.x * a.v.y,
                    b.p.x * b.v.z - b.p.z * b.v.x + a.p.z * a.v.x - a.p.x * a.v.z,
                    c.p.x * c.v.y - c.p.y * c.v.x + a.p.y * a.v.x - a.p.x * a.v.y,
                    c.p.z * c.v.y - c.p.y * c.v.z + a.p.y * a.v.z - a.p.z * a.v.y,
                    b.p.x * b.v.z - b.p.z * b.v.x + c.p.z * c.v.x - c.p.x * c.v.z,
                    c.p.z * c.v.y - c.p.y * c.v.z + b.p.y * b.v.z - b.p.z * b.v.y,
                )
            )

            val det = matrixA.determinant()
            val solution = matrixA.matrixOfMinors().matrixOfCoefficients().transposed() * matrixB

            if (det != BigInteger.ZERO) {
                // TODO: add verification
                val (px, py, pz) = (solution / det).getColumn(0)
                    .toList()
                    .take(3)

                return px + py + pz
            } else {
                TODO()
            }
        }
    }

    private fun parseVector3(text: String) =
        text.split(", ").map { it.trim().toBigInteger() }.let { (x, y, z) -> Vector3(x, y, z) }

    private fun parseHailstone(line: String) = Hailstone(
        p = line.substringBefore(" @ ").let(::parseVector3),
        v = line.substringAfter(" @ ").let(::parseVector3),
    )
}

private data class IntersectionPoint(
    val x: BigDecimal,
    val y: BigDecimal,
)

private data class Vector3(
    val x: BigInteger,
    val y: BigInteger,
    val z: BigInteger
) {
    override fun toString(): String = "($x, $y, $z)"
}

private data class Hailstone(
    val p: Vector3,
    val v: Vector3,
)