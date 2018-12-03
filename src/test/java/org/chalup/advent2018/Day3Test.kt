package org.chalup.advent2018

import com.google.common.truth.Truth
import org.junit.jupiter.api.Test

class Day3Test {
    @Test
    fun `should calculate claims overlap`() {
        Truth.assertThat(Day3.overlap(claims)).isEqualTo(4)
    }

    @Test
    fun `should find non overlapping claim`() {
        Truth.assertThat(Day3.findNonOverlappingClaim(claims)).isEqualTo(3)
    }

    companion object {
        val claims = listOf("#1 @ 1,3: 4x4",
                            "#2 @ 3,1: 4x4",
                            "#3 @ 5,5: 2x2")
    }
}