package org.chalup

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import org.chalup.utils.except
import org.chalup.utils.intersection

class RangesTest : FreeSpec({
    "Ranges intersections" - {
        val testCases = listOf(
            Triple(5L..10L, 12L..13L, null),
            Triple(6L..10L, 0L..5L, null),
            Triple(5L..10L, 8L..12L, 8L..10L),
            Triple(5L..10L, 1L..6L, 5L..6L),
            Triple(5L..10L, 6L..8L, 6L..8L),
            Triple(5L..10L, 1L..12L, 5L..10L),
            Triple(5L..10L, 10L..12L, 10L..10L),
            Triple(5L..10L, 1L..5L, 5L..5L),
        )

        testCases.forEach { (rangeA, rangeB, expected) ->
            "$rangeA intersection $rangeB == $expected" {
                (rangeA intersection rangeB) shouldBe expected
            }
        }
    }

    "Ranges difference" - {
        val testCases = listOf(
            Triple(5L..10L, 12L..13L, listOf(5L..10L)),
            Triple(6L..10L, 0L..5L, listOf(6L..10L)),
            Triple(5L..10L, 8L..12L, listOf(5L..7L)),
            Triple(5L..10L, 1L..6L, listOf(7L..10L)),
            Triple(5L..10L, 6L..8L, listOf(5L..5L, 9L..10L)),
            Triple(5L..10L, 1L..12L, listOf()),
            Triple(5L..10L, 10L..12L, listOf(5L..9L)),
            Triple(5L..10L, 1L..5L, listOf(6L..10L)),
        )

        testCases.forEach { (rangeA, rangeB, expected) ->
            "$rangeA except $rangeB == $expected" {
                (rangeA except rangeB) shouldContainExactlyInAnyOrder expected
            }
        }
    }
})