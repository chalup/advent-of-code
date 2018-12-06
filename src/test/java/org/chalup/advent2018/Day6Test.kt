package org.chalup.advent2018

import com.google.common.truth.Truth
import org.junit.jupiter.api.Test

class Day6Test {
    @Test
    fun `calculate largest finite area`() {
        Truth.assertThat(Day6.findLargestFiniteArea(points)).isEqualTo(17)
    }

    @Test
    fun `calculate safe region area`() {
        Truth.assertThat(Day6.findSafeRegionArea(points, 32)).isEqualTo(16)
    }

    companion object {
        val points = listOf("1, 1",
                            "1, 6",
                            "8, 3",
                            "3, 4",
                            "5, 5",
                            "8, 9")
    }
}