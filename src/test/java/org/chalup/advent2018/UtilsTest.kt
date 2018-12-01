package org.chalup.advent2018

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class UtilsTest {
    @Test
    fun `should repeat single element indefinitely`() {
        assertThat(listOf(1).cycle().take(10)).containsExactlyElementsIn(List(10) { 1 })
    }

    @Test
    fun `should repeat the collection elements`() {
        val input = listOf(1, 2, 3)

        assertThat(input.cycle().take(input.size * 2)).containsExactlyElementsIn(input + input).inOrder()
    }

    @Test
    fun `cycle through an empty collection should be empty`() {
        assertThat(emptyList<Int>().cycle()).isEmpty()
    }

    @Test
    fun `should fail to take an element from empty cycle`() {
        assertThrows<NoSuchElementException> { emptyList<Int>().cycle().iterator().next() }
    }

    @Test
    fun `should scan through the collection`() {
        assertThat((1..4).asIterable().scan(0) { a, b -> a + b })
            .containsExactlyElementsIn(listOf(0, 1, 3, 6, 10))
            .inOrder()

    }
}