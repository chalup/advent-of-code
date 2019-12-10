package org.chalup.advent2019

import com.google.common.truth.Truth
import org.chalup.utils.Point
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

internal class Day10Test {
    @ParameterizedTest
    @MethodSource("maps")
    fun `should calculate maximum number of detected asteroids`(expectedResult: Int, map: String) {
        Truth.assertThat(Day10.maximumNumberOfDetectedAsteroids(map.lines())).isEqualTo(expectedResult)
    }

    @Test
    fun `should determine correct vaporization order`() {
        val (_, asteroids) = Day10.readMap(bigMap.lines())
        val laserLocation = Point(11, 13)

        val vaporizationOrder = Day10.vaporizationOrder(laserLocation = laserLocation,
                                                        asteroids = asteroids)

        for ((nth, point) in listOf(1 to Point(11, 12),
                                    2 to Point(12, 1),
                                    3 to Point(12, 2),
                                    10 to Point(12, 8),
                                    20 to Point(16, 0),
                                    50 to Point(16, 9),
                                    100 to Point(10, 16),
                                    199 to Point(9, 6),
                                    200 to Point(8, 2),
                                    201 to Point(10, 9),
                                    299 to Point(11, 1))) {
            Truth.assertThat(vaporizationOrder[nth - 1]).isEqualTo(point)
        }
    }

    companion object {
        val bigMap = """
                .#..##.###...#######
                ##.############..##.
                .#.######.########.#
                .###.#######.####.#.
                #####.##.#.##.###.##
                ..#####..#.#########
                ####################
                #.####....###.#.#.##
                ##.#################
                #####.##.###..####..
                ..######..##.#######
                ####.##.####...##..#
                .#####..#.######.###
                ##...#.##########...
                #.##########.#######
                .####.#.###.###.#.##
                ....##.##.###..#####
                .#.#.###########.###
                #.#.#.#####.####.###
                ###.##.####.##.#..##
        """.trimIndent()

        @JvmStatic
        fun maps() = listOf<Arguments>(
            Arguments.arguments(8, """
                .#..#
                .....
                #####
                ....#
                ...##
            """.trimIndent()),
            Arguments.arguments(33, """
                ......#.#.
                #..#.#....
                ..#######.
                .#.#.###..
                .#..#.....
                ..#....#.#
                #..#....#.
                .##.#..###
                ##...#..#.
                .#....####
            """.trimIndent()),
            Arguments.arguments(41, """
                .#..#..###
                ####.###.#
                ....###.#.
                ..###.##.#
                ##.##.#.#.
                ....###..#
                ..#.#..#.#
                #..#.#.###
                .##...##.#
                .....#.#..
            """.trimIndent()),
            Arguments.arguments(210, bigMap)
        )
    }
}