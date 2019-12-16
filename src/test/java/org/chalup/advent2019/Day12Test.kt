package org.chalup.advent2019

import com.google.common.truth.Truth
import org.chalup.advent2019.Day12.Moon
import org.chalup.advent2019.Day12.MoonLocation
import org.chalup.advent2019.Day12.MoonVelocity
import org.chalup.utils.match
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class Day12Test {
    @ParameterizedTest
    @MethodSource("simulationData")
    fun `should simulate moons`(input: String, steps: List<String>) {
        var moons = Day12.parseInput(input.lines())

        steps.forEachIndexed { i, step ->
            Truth.assertWithMessage("Simulation error at step #$i")
                .that(moons)
                .containsExactlyElementsIn(
                    step.lines().map { parseSimulationStep(it) }
                )
                .inOrder()

            moons = Day12.simulate(moons)
        }
    }

    @ParameterizedTest
    @MethodSource("cyclesData")
    fun `should find cycle`(input: String, expectedCycleLength: Long) {
        Truth.assertThat(Day12.task2(input.lines())).isEqualTo(expectedCycleLength)
    }

    private fun parseSimulationStep(input: String): Moon = match(input) {
        fun String.parseLong() = trim().toLong()

        pattern("""pos=\<x=(.*?), y=(.*?), z=(.*?)\>, vel=\<x=(.*?), y=(.*?), z=(.*?)\>""") { (x, y, z, vx, vy, vz) ->
            Moon(location = MoonLocation(x.parseLong(), y.parseLong(), z.parseLong()),
                 velocity = MoonVelocity(vx.parseLong(), vy.parseLong(), vz.parseLong()))
        }
    }

    companion object {
        @JvmStatic
        fun simulationData() = listOf<Arguments>(
            Arguments.arguments("""
                                    <x=-1, y=0, z=2>
                                    <x=2, y=-10, z=-7>
                                    <x=4, y=-8, z=8>
                                    <x=3, y=5, z=-1>
                                """.trimIndent(),
                                listOf(
                                    """
                                    pos=<x=-1, y=  0, z= 2>, vel=<x= 0, y= 0, z= 0>
                                    pos=<x= 2, y=-10, z=-7>, vel=<x= 0, y= 0, z= 0>
                                    pos=<x= 4, y= -8, z= 8>, vel=<x= 0, y= 0, z= 0>
                                    pos=<x= 3, y=  5, z=-1>, vel=<x= 0, y= 0, z= 0>
                                """.trimIndent(),
                                    """
                                    pos=<x= 2, y=-1, z= 1>, vel=<x= 3, y=-1, z=-1>
                                    pos=<x= 3, y=-7, z=-4>, vel=<x= 1, y= 3, z= 3>
                                    pos=<x= 1, y=-7, z= 5>, vel=<x=-3, y= 1, z=-3>
                                    pos=<x= 2, y= 2, z= 0>, vel=<x=-1, y=-3, z= 1>
                                """.trimIndent(),
                                    """
                                    pos=<x= 5, y=-3, z=-1>, vel=<x= 3, y=-2, z=-2>
                                    pos=<x= 1, y=-2, z= 2>, vel=<x=-2, y= 5, z= 6>
                                    pos=<x= 1, y=-4, z=-1>, vel=<x= 0, y= 3, z=-6>
                                    pos=<x= 1, y=-4, z= 2>, vel=<x=-1, y=-6, z= 2>
                                """.trimIndent(),
                                    """
                                    pos=<x= 5, y=-6, z=-1>, vel=<x= 0, y=-3, z= 0>
                                    pos=<x= 0, y= 0, z= 6>, vel=<x=-1, y= 2, z= 4>
                                    pos=<x= 2, y= 1, z=-5>, vel=<x= 1, y= 5, z=-4>
                                    pos=<x= 1, y=-8, z= 2>, vel=<x= 0, y=-4, z= 0>
                                """.trimIndent(),
                                    """
                                    pos=<x= 2, y=-8, z= 0>, vel=<x=-3, y=-2, z= 1>
                                    pos=<x= 2, y= 1, z= 7>, vel=<x= 2, y= 1, z= 1>
                                    pos=<x= 2, y= 3, z=-6>, vel=<x= 0, y= 2, z=-1>
                                    pos=<x= 2, y=-9, z= 1>, vel=<x= 1, y=-1, z=-1>
                                """.trimIndent(),
                                    """
                                    pos=<x=-1, y=-9, z= 2>, vel=<x=-3, y=-1, z= 2>
                                    pos=<x= 4, y= 1, z= 5>, vel=<x= 2, y= 0, z=-2>
                                    pos=<x= 2, y= 2, z=-4>, vel=<x= 0, y=-1, z= 2>
                                    pos=<x= 3, y=-7, z=-1>, vel=<x= 1, y= 2, z=-2>
                                """.trimIndent(),
                                    """
                                    pos=<x=-1, y=-7, z= 3>, vel=<x= 0, y= 2, z= 1>
                                    pos=<x= 3, y= 0, z= 0>, vel=<x=-1, y=-1, z=-5>
                                    pos=<x= 3, y=-2, z= 1>, vel=<x= 1, y=-4, z= 5>
                                    pos=<x= 3, y=-4, z=-2>, vel=<x= 0, y= 3, z=-1>
                                """.trimIndent(),
                                    """
                                    pos=<x= 2, y=-2, z= 1>, vel=<x= 3, y= 5, z=-2>
                                    pos=<x= 1, y=-4, z=-4>, vel=<x=-2, y=-4, z=-4>
                                    pos=<x= 3, y=-7, z= 5>, vel=<x= 0, y=-5, z= 4>
                                    pos=<x= 2, y= 0, z= 0>, vel=<x=-1, y= 4, z= 2>
                                """.trimIndent(),
                                    """
                                    pos=<x= 5, y= 2, z=-2>, vel=<x= 3, y= 4, z=-3>
                                    pos=<x= 2, y=-7, z=-5>, vel=<x= 1, y=-3, z=-1>
                                    pos=<x= 0, y=-9, z= 6>, vel=<x=-3, y=-2, z= 1>
                                    pos=<x= 1, y= 1, z= 3>, vel=<x=-1, y= 1, z= 3>
                                """.trimIndent(),
                                    """
                                    pos=<x= 5, y= 3, z=-4>, vel=<x= 0, y= 1, z=-2>
                                    pos=<x= 2, y=-9, z=-3>, vel=<x= 0, y=-2, z= 2>
                                    pos=<x= 0, y=-8, z= 4>, vel=<x= 0, y= 1, z=-2>
                                    pos=<x= 1, y= 1, z= 5>, vel=<x= 0, y= 0, z= 2>
                                """.trimIndent(),
                                    """
                                    pos=<x= 2, y= 1, z=-3>, vel=<x=-3, y=-2, z= 1>
                                    pos=<x= 1, y=-8, z= 0>, vel=<x=-1, y= 1, z= 3>
                                    pos=<x= 3, y=-6, z= 1>, vel=<x= 3, y= 2, z=-3>
                                    pos=<x= 2, y= 0, z= 4>, vel=<x= 1, y=-1, z=-1>
                                """.trimIndent()
                                )
            )
        )

        @JvmStatic
        fun cyclesData() = listOf<Arguments>(
            Arguments.arguments("""
                                    <x=-1, y=0, z=2>
                                    <x=2, y=-10, z=-7>
                                    <x=4, y=-8, z=8>
                                    <x=3, y=5, z=-1>
                                """.trimIndent(), 2772),
            Arguments.arguments("""
                                    <x=-8, y=-10, z=0>
                                    <x=5, y=5, z=10>
                                    <x=2, y=-7, z=3>
                                    <x=9, y=-8, z=-3>
                                """.trimIndent(), 4686774924)
        )
    }
}