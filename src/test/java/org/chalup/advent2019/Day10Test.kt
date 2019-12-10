package org.chalup.advent2019

import com.google.common.truth.Truth
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

internal class Day10Test {
    @ParameterizedTest
    @MethodSource("maps")
    fun `should calculate maximum number of detected asteroids`(expectedResult: Int, map: String) {
        Truth.assertThat(Day10.maximumNumberOfDetectedAsteroids(map.lines())).isEqualTo(expectedResult)
    }

    companion object {
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
            Arguments.arguments(210, """
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
            """.trimIndent())
        )
    }
}