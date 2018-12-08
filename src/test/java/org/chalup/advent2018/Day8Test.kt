package org.chalup.advent2018

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

class Day8Test {
    @Test
    fun `should calculate a proper checksum`() {
        assertThat(Day8.calculateRootNodeChecksum(license)).isEqualTo(138)
    }

    @Test
    fun `should calculate a proper value`() {
        assertThat(Day8.calculateRootNodeValue(license)).isEqualTo(66)
    }

    companion object {
        const val license = "2 3 0 3 10 11 12 1 1 0 1 99 2 1 1 2"
    }
}