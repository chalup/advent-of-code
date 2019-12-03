package org.chalup.advent2019

import com.google.common.truth.Truth.assertThat
import org.chalup.advent2019.Day3.distanceToNearestIntersection
import org.chalup.advent2019.Day3.shortestPath
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class Day3Test {
    @ParameterizedTest
    @MethodSource("intersectingWires")
    fun `should find the nearest intersection`(wires: List<String>,
                                               distanceToNearestIntersection: Int,
                                               shortestPath: Int) {
        assertThat(distanceToNearestIntersection(wires)).isEqualTo(distanceToNearestIntersection)
    }

    @ParameterizedTest
    @MethodSource("intersectingWires")
    fun `should find the shortest path`(wires: List<String>,
                                        distanceToNearestIntersection: Int,
                                        shortestPath: Int) {
        assertThat(shortestPath(wires)).isEqualTo(shortestPath)
    }

    companion object {
        @JvmStatic
        fun intersectingWires() = listOf<Arguments>(
            Arguments.arguments(listOf("R8,U5,L5,D3", "U7,R6,D4,L4"), 6, 30),
            Arguments.arguments(listOf("R75,D30,R83,U83,L12,D49,R71,U7,L72", "U62,R66,U55,R34,D71,R55,D58,R83"), 159, 610),
            Arguments.arguments(listOf("R98,U47,R26,D63,R33,U87,L62,D20,R33,U53,R51", "U98,R91,D20,R16,D67,R40,U7,R15,U6,R7"), 135, 410)
        )
    }
}