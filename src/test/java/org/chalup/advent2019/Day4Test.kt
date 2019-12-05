package org.chalup.advent2019

import com.google.common.truth.Truth
import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

class Day4Test {
    @Test
    fun `test non-decreasing digits check`() {
        assertThat(Day4.isNotDecreasing("111111".toCharArray())).isTrue()
        assertThat(Day4.isNotDecreasing("223450".toCharArray())).isFalse()
        assertThat(Day4.isNotDecreasing("123789".toCharArray())).isTrue()
    }

    @Test
    fun `test double check`() {
        assertThat(Day4.containsDouble("111111".toCharArray())).isTrue()
        assertThat(Day4.containsDouble("223450".toCharArray())).isTrue()
        assertThat(Day4.containsDouble("123789".toCharArray())).isFalse()
    }

    @Test
    fun `test separated double check`() {
        assertThat(Day4.containsSeparatedDouble("112233".toCharArray())).isTrue()
        assertThat(Day4.containsSeparatedDouble("123444".toCharArray())).isFalse()
        assertThat(Day4.containsSeparatedDouble("111444".toCharArray())).isFalse()
        assertThat(Day4.containsSeparatedDouble("111122".toCharArray())).isTrue()
        assertThat(Day4.containsSeparatedDouble("221111".toCharArray())).isTrue()
        assertThat(Day4.containsSeparatedDouble("113444".toCharArray())).isTrue()
        assertThat(Day4.containsSeparatedDouble("124444".toCharArray())).isFalse()
        assertThat(Day4.containsSeparatedDouble("123345".toCharArray())).isTrue()
    }
}