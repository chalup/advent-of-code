package org.chalup.advent2017

import org.chalup.utils.Vector3

object Day20 {
    // 705 too high
    fun task1(input: List<String>): Int {
        val initialParticles = input.mapIndexed { index, s ->
            """p=<(.*?),(.*?),(.*?)>, v=<(.*?),(.*?),(.*?)>, a=<(.*?),(.*?),(.*?)>""".toRegex()
                .matchEntire(s)!!
                .groupValues.drop(1)
                .map { it.toLong() }
                .chunked(3) { (x, y, z) -> Vector3(x, y, z) }
                .let { (p, v, a) ->
                    Particle(index, p, v, a)
                }
        }


        return generateSequence(initialParticles) { it.map(Particle::simulate) }
            .map { particles -> particles.sortedByDescending(Particle::distanceFromOrigin).map { it.index } }
            // This doesn't look foolproof and probably is the reason why this crap doesn't work. Imagine
            // two particles, one chasing the other along some axis with +/- 1 position change per tick.
            .windowed(100)
            .takeWhile { it.toSet().size > 1 }
            .map { it.last() }
            .last()
            .first()
    }

    data class Particle(
        val index: Int,
        val position: Vector3,
        val velocity: Vector3,
        val acceleration: Vector3,
    ) {
        fun simulate() = (velocity + acceleration).let { newVelocity ->
            copy(
                position = position + newVelocity,
                velocity = newVelocity
            )
        }

        fun distanceFromOrigin() = with(position) { x + y + z }
    }
}