package org.chalup.advent2021

import org.chalup.utils.Point

object Day25 {
    fun task1(input: List<String>): Int = input
        .let(this::parse)
        .let { generateSequence(it, this::migrateSeaCucumbers) }
        .zipWithNext()
        .takeWhile { (prev, next) -> prev != next }
        .let { it.count() + 1 }

    private fun migrateSeaCucumbers(seafloor: Seafloor): Seafloor {
        val migratedEastwardCucumbers = seafloor.eastwardCucumbers.mapTo(mutableSetOf()) { location ->
            location
                .copy(x = (location.x + 1) % seafloor.width)
                .takeUnless { it in seafloor.eastwardCucumbers }
                ?.takeUnless { it in seafloor.southwardCucumbers }
                ?: location
        }

        val migratedSouthwardCucumbers = seafloor.southwardCucumbers.mapTo(mutableSetOf()) { location ->
            location
                .copy(y = (location.y + 1) % seafloor.height)
                .takeUnless { it in migratedEastwardCucumbers }
                ?.takeUnless { it in seafloor.southwardCucumbers }
                ?: location
        }

        return seafloor.copy(
            eastwardCucumbers = migratedEastwardCucumbers,
            southwardCucumbers = migratedSouthwardCucumbers,
        )
    }

    private fun parse(input: List<String>): Seafloor {
        val eastwardCucumbers = mutableSetOf<Point>()
        val southwardCucumbers = mutableSetOf<Point>()

        input.forEachIndexed { y, line ->
            line.forEachIndexed { x, char ->
                when (char) {
                    '>' -> eastwardCucumbers.add(Point(x, y))
                    'v' -> southwardCucumbers.add(Point(x, y))
                }
            }
        }

        return Seafloor(
            width = input.first().length,
            height = input.size,
            eastwardCucumbers,
            southwardCucumbers
        )
    }

    private data class Seafloor(
        val width: Int,
        val height: Int,
        val eastwardCucumbers: Set<Point>,
        val southwardCucumbers: Set<Point>,
    )
}
