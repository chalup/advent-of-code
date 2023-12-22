package org.chalup.advent2023

import org.chalup.utils.except
import org.chalup.utils.intersection
import org.chalup.utils.transposeBy

object Day5 {
    fun task1(input: List<String>): Long = input
        .let(::parseAlmanac)
        .let { almanac ->
            almanac
                .seedEntries
                .map { it..it }
                .let { ranges -> findSeedsLocations(almanac, ranges) }
                .map { it.first }
        }
        .min()

    fun task2(input: List<String>): Long = input
        .let(::parseAlmanac)
        .let { almanac ->
            almanac
                .seedEntries
                .chunked(2)
                .map { (start, count) ->
                    start until start + count
                }
                .let { ranges -> findSeedsLocations(almanac, ranges) }
                .map { it.first }
        }
        .min()

    private fun findSeedsLocations(almanac: Almanac, ranges: List<LongRange>): List<LongRange> {
        val mappingSequence = generateSequence("seed" to ranges) { (category, categoryRanges) ->
            almanac
                .mappings
                .find { it.sourceCategory == category }
                ?.let { mapping ->
                    val mappedRanges = categoryRanges
                        .flatMap { range ->
                            mapping
                                .ranges
                                .fold(emptyList<LongRange>() to listOf(range)) { (mappedRanges, unmappedRanges), mappingRange ->
                                    val newMappings = unmappedRanges
                                        .mapNotNull { it intersection mappingRange.sourceRange }
                                        .map { it transposeBy mappingRange.skew }

                                    val newUnmappedRanges = unmappedRanges
                                        .flatMap { it except mappingRange.sourceRange }

                                    (mappedRanges + newMappings) to newUnmappedRanges
                                }
                                .let { (mapped, unmapped) -> mapped + unmapped }
                        }

                    mapping.destinationCategory to mappedRanges
                }
        }

        return mappingSequence
            .last()
            .let { (category, ranges) -> ranges.also { check(category == "location") } }
    }
}

private fun parseAlmanac(input: List<String>): Almanac {
    val iterator = input.iterator()
    val seeds = iterator.next()
        .substringAfter("seeds:")
        .trim()
        .split(' ')
        .map { it.toLong() }

    check(iterator.next().isEmpty())

    val mappings = buildList {
        while (iterator.hasNext()) {
            add(parseMapping(iterator))
        }
    }

    return Almanac(seeds, mappings)
}

private fun parseMapping(iterator: Iterator<String>): Mapping {
    val (src, dst) = iterator.next().let { line ->
        val src = line.substringBefore("-to-")
        val dst = line.substringAfter("-to-").substringBefore(" ")

        src to dst
    }

    val ranges = buildList {
        while (iterator.hasNext()) {
            val line = iterator.next()
            if (line.isEmpty()) break

            val (dstStart, srcStart, size) = line.split(' ').map { it.toLong() }
            add(MappingRange(dstStart, srcStart, size))
        }
    }

    return Mapping(src, dst, ranges)
}

private data class Almanac(
    val seedEntries: List<Long>,
    val mappings: List<Mapping>
)

private data class Mapping(
    val sourceCategory: String,
    val destinationCategory: String,
    val ranges: List<MappingRange>,
)

private data class MappingRange(
    val destinationStartIndex: Long,
    val sourceStartIndex: Long,
    val rangeSize: Long,
) {
    val sourceRange = sourceStartIndex until sourceStartIndex + rangeSize

    val skew = destinationStartIndex - sourceStartIndex
}
