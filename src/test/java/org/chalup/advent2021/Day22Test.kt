package org.chalup.advent2021

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import org.chalup.advent2021.Day22.contains
import org.chalup.advent2021.Day22.minus
import org.chalup.utils.Cube
import org.chalup.utils.Vector3
import org.chalup.utils.contains
import org.chalup.utils.intersects
import org.chalup.utils.size

class Day22Test : FreeSpec({
    "Ranges size" - {
        val testCases = listOf(
            1L..2L to 2L,
            1L..1L to 1L,
            LongRange.EMPTY to 0L
        )

        testCases.forEach { (input, expected) ->
            "[$input].size should be $expected" {
                input.size() shouldBe expected
            }
        }
    }

    "Ranges intersections" - {
        val testCases = listOf(
            Triple(5L..10L, 12L..13L, false),
            Triple(6L..10L, 0L..5L, false),
            Triple(5L..10L, 8L..12L, true),
            Triple(5L..10L, 1L..6L, true),
            Triple(5L..10L, 6L..8L, true),
            Triple(5L..10L, 1L..12L, true),
            Triple(5L..10L, 10L..12L, true),
            Triple(5L..10L, 1L..5L, true),
        )

        testCases.forEach { (rangeA, rangeB, expected) ->
            "$rangeA ${if (expected) "intersects" else "does not intersect"} $rangeB" {
                (rangeA intersects rangeB) shouldBe expected
            }
        }
    }

    "Ranges contains" - {
        val testCases = listOf(
            Triple(5L..10L, 12L..13L, false),
            Triple(6L..10L, 0L..5L, false),
            Triple(5L..10L, 8L..12L, false),
            Triple(5L..10L, 1L..6L, false),
            Triple(5L..10L, 6L..8L, true),
            Triple(6L..8L, 5L..10L, false),
            Triple(5L..10L, 1L..12L, false),
            Triple(1L..12L, 5L..10L, true),
        )

        testCases.forEach { (rangeA, rangeB, expected) ->
            "$rangeA ${if (expected) "contains" else "does not contain"} $rangeB" {
                (rangeA contains rangeB) shouldBe expected
            }
        }
    }

    "Cube contains" - {
        val testCases = listOf(
            Triple(
                Cube(1L..2L, 1L..2L, 1L..2L),
                Cube(3L..4L, 3L..4L, 3L..4L),
                false
            ),
            Triple(
                Cube(1L..2L, 1L..2L, 1L..2L),
                Cube(1L..1L, 1L..1L, 1L..1L),
                true
            ),
            Triple(
                Cube(1L..1L, 1L..1L, 1L..1L),
                Cube(1L..2L, 1L..2L, 1L..2L),
                false
            ),
            Triple(
                Cube(1L..2L, 1L..2L, 1L..2L),
                Cube(2L..3L, 2L..3L, 2L..3L),
                false
            ),
        )

        testCases.forEach { (cubeA, cubeB, expected) ->
            "$cubeA ${if (expected) "contains" else "does not contain"} $cubeB" {
                (cubeA contains cubeB) shouldBe expected
            }
        }
    }

    "Cube intersects" - {
        val testCases = listOf(
            Triple(
                Cube(1L..2L, 1L..2L, 1L..2L),
                Cube(3L..4L, 3L..4L, 3L..4L),
                false
            ),
            Triple(
                Cube(1L..2L, 1L..2L, 1L..2L),
                Cube(3L..4L, 1L..2L, 1L..2L),
                false
            ),
            Triple(
                Cube(1L..2L, 1L..2L, 1L..2L),
                Cube(1L..1L, 1L..1L, 1L..1L),
                true
            ),
            Triple(
                Cube(1L..1L, 1L..1L, 1L..1L),
                Cube(1L..2L, 1L..2L, 1L..2L),
                true
            ),
            Triple(
                Cube(1L..2L, 1L..2L, 1L..2L),
                Cube(2L..3L, 2L..3L, 2L..3L),
                true
            ),
        )

        testCases.forEach { (cubeA, cubeB, expected) ->
            "$cubeA ${if (expected) "intersects" else "does not intersect"} $cubeB" {
                (cubeA intersects cubeB) shouldBe expected
            }
        }
    }

    "Cube difference" - {
        val testCases = listOf(
            Triple(
                Cube(1L..2L, 1L..2L, 1L..2L),
                Cube(1L..1L, 1L..1L, 1L..1L),
                setOf(
                    Cube(1L..2L, 1L..2L, 2L..2L),
                    Cube(2L..2L, 1L..2L, 1L..1L),
                    Cube(1L..1L, 2L..2L, 1L..1L),
                )
            ),
            Triple(
                Cube(1L..2L, 1L..2L, 1L..2L),
                Cube(0L..1L, 0L..1L, 0L..1L),
                setOf(
                    Cube(1L..2L, 1L..2L, 2L..2L),
                    Cube(2L..2L, 1L..2L, 1L..1L),
                    Cube(1L..1L, 2L..2L, 1L..1L),
                )
            ),
            Triple(
                Cube(1L..2L, 1L..2L, 1L..2L),
                Cube(2L..2L, 0L..3L, 0L..3L),
                setOf(
                    Cube(1L..1L, 1L..2L, 1L..2L),
                )
            ),
            Triple(
                Cube(0L..2L, 0L..2L, 0L..2L),
                Cube(1L..1L, 2L..3L, 1L..1L),
                setOf(
                    Cube(0L..2L, 0L..1L, 0L..2L),
                    Cube(0L..2L, 2L..2L, 0L..0L),
                    Cube(0L..2L, 2L..2L, 2L..2L),
                    Cube(0L..0L, 2L..2L, 1L..1L),
                    Cube(2L..2L, 2L..2L, 1L..1L),
                )
            ),
            Triple(
                Cube(0L..2L, 0L..2L, 0L..2L),
                Cube(1L..1L, -1L..3L, 1L..1L),
                setOf(
                    Cube(0L..2L, 0L..2L, 0L..0L),
                    Cube(0L..2L, 0L..2L, 2L..2L),
                    Cube(0L..0L, 0L..2L, 1L..1L),
                    Cube(2L..2L, 0L..2L, 1L..1L),
                )
            ),
            Triple(
                Cube(0L..2L, 0L..2L, 0L..2L),
                Cube(-1L..3L, -1L..3L, 1L..1L),
                setOf(
                    Cube(0L..2L, 0L..2L, 0L..0L),
                    Cube(0L..2L, 0L..2L, 2L..2L),
                )
            ),
            Triple(
                Cube(0L..2L, 0L..2L, 0L..2L),
                Cube(1L..3L, -1L..3L, 1L..3L),
                setOf(
                    Cube(0L..2L, 0L..2L, 0L..0L),
                    Cube(0L..0L, 0L..2L, 1L..2L),
                )
            ),
        )

        testCases.forEach { (cubeA, cubeB, equivalentCubeSet) ->
            "$cubeA - $cubeB" {
                // we should have the expected voxels
                (cubeA - cubeB).points() shouldBe equivalentCubeSet.points()

                // the set should be a disjoint set
                (cubeA - cubeB).run {
                    val pairs = this.flatMap { cube ->
                        this.mapNotNull { otherCube ->
                            (cube to otherCube).takeUnless { cube === otherCube }
                        }
                    }

                    pairs.forEach { (a, b) ->
                        a intersects b shouldBe false
                    }
                }
            }
        }
    }
})

private fun Set<Cube>.points() = fold(emptySet<Vector3>()) { points, cube -> points + cube.points() }