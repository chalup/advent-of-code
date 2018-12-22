package org.chalup.advent2018

import com.google.common.truth.Truth.assertThat
import org.chalup.utils.Point
import org.junit.jupiter.api.Test

class Day22Test {
    @Test
    fun `should calculate total risk`() {
        assertThat(Day22.estimateTotalRisk(depth = 510,
                                           target = Point(10,10))).isEqualTo(114)
    }
}