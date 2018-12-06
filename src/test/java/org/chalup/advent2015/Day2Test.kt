package org.chalup.advent2015

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

class Day2Test {
    @Test
    fun `should calculate amount of paper needed for all packages`() {
        assertThat(Day2.paperAmountForAllPackages(listOf("2x3x4", "1x1x10"))).isEqualTo(58 + 43)
    }

    @Test
    fun `should calculate amount of ribbon needed for all packages`() {
        assertThat(Day2.ribbonAmountForAllPackages(listOf("2x3x4", "1x1x10"))).isEqualTo(34 + 14)
    }
}