package org.chalup.advent2022

import org.chalup.utils.Point
import org.chalup.utils.intersection
import org.chalup.utils.manhattanDistance
import org.chalup.utils.matchRegex
import org.chalup.utils.overlaps

object Day15 {
    fun task1(input: List<String>) = input
        .let(::parse)
        .let { devices ->
            val devicesInRow: Set<Int> = devices
                .asSequence()
                .flatMap { (sensor, beacon) -> sequenceOf(sensor, beacon) }
                .mapNotNullTo(mutableSetOf()) { (x, y) -> x.takeIf { y == 2_000_000 } }

            spansForLine(devices, 2_000_000)
                .sumOf { span ->
                span.last - span.first + 1 - devicesInRow.count { it in span }
            }
        }

    fun task2(input: List<String>) = input
        .let(::parse)
        .let { devices ->
            val targetRange = 0..4_000_000

            for (y in targetRange) {
                val spans = spansForLine(devices, y)
                    .mapNotNull { it intersection targetRange }
                    .filterNot { it == targetRange }

                if (spans.isNotEmpty()) {
                    return@let spans
                        .fold(targetRange.toSet()) { possibleLocations, span -> possibleLocations - span.toSet() }
                        .single()
                        .let { x -> Point(x, y) }
                }
            }

            throw IllegalStateException("Could not find the spot :(")
        }
        .let { (x, y) -> x.toLong() * 4_000_000L + y.toLong() }

    private fun spansForLine(devices: List<Pair<Point, Point>>, lineY: Int): List<IntRange> {
        return devices
            .mapNotNull { (sensor, beacon) ->
                manhattanDistance(sensor, beacon)
                    .let { radius ->
                        (radius - manhattanDistance(sensor, sensor.copy(y = lineY))).takeIf { it >= 0 }
                    }
                    ?.let { span ->
                        (sensor.x - span)..(sensor.x + span)
                    }
            }
            .sortedBy { it.first }
            .let { spans ->
                val mergedSpans = mutableListOf<IntRange>()

                for (span in spans) {
                    val possibleMerge = mergedSpans.removeLastOrNull()

                    if (possibleMerge != null) {
                        if (possibleMerge overlaps span) {
                            mergedSpans += minOf(possibleMerge.first, span.first)..maxOf(possibleMerge.last, span.last)
                        } else {
                            mergedSpans += possibleMerge
                            mergedSpans += span
                        }
                    } else {
                        mergedSpans += span
                    }
                }

                mergedSpans
            }
    }

    private fun parse(input: List<String>): List<Pair<Point, Point>> = input
        .map { line ->
            val digit = """([-\d]+)"""
            line.matchRegex("""Sensor at x=$digit, y=$digit: closest beacon is at x=$digit, y=$digit""") {
                it.toList()
                    .map(String::toInt)
                    .let { (sensorX, sensorY, beaconX, beaconY) ->
                        Point(sensorX, sensorY) to Point(beaconX, beaconY)
                    }
            }
        }
}