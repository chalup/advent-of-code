package org.chalup.advent2025

import org.chalup.utils.intersects
import org.chalup.utils.size
import org.chalup.utils.textBlocks

object Day5 {
    fun task1(input: List<String>): Int {
        val (rangesBlock, idsBlock) = textBlocks(input)

        val ranges = rangesBlock.map {
            it.split("-").map(String::toLong).let { (start, end) -> start..end }
        }

        return idsBlock
            .map(String::toLong)
            .count { id -> ranges.any { id in it } }
    }

    fun task2(input: List<String>): Long {
        val (rangesBlock, _) = textBlocks(input)

        val ranges = rangesBlock.map {
            it.split("-").map(String::toLong).let { (start, end) -> start..end }
        }

        infix fun LongRange.mergeWith(other: LongRange): List<LongRange> =
            if (this intersects other) {
                listOf(minOf(first, other.first)..maxOf(last, other.last))
            } else {
                listOf(this, other)
            }

        return ranges
            .sortedBy { it.first }
            .fold(emptyList<LongRange>()) { mergedRanges, range ->
                mergedRanges
                    .lastOrNull()
                    ?.let { mergedRanges.dropLast(1) + (it mergeWith range) }
                    ?: listOf(range)
            }
            .sumOf { it.size() }
    }
}