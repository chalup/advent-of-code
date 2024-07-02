package org.chalup.advent2017

import org.chalup.utils.Vector3
import kotlin.math.absoluteValue

object Day20 {
    fun task1(input: List<String>): Int {
        return generateSequence(parseParticles(input)) { it.map(Particle::simulate) }
            .map { particles -> particles.sortedByDescending(Particle::distanceFromOrigin).map { it.index } }
            .windowed(10)
            .takeWhile { it.toSet().size > 1 }
            .map { it.last() }
            .last()
            .last()
    }

    fun task2(input: List<String>): Int {
        val simulation = generateSequence(parseParticles(input)) { particles ->
            particles
                .map(Particle::simulate)
                .groupBy { it.position }
                .flatMap { (_, group) -> group.takeIf { it.size == 1 }.orEmpty() }
        }

        return simulation
            .map { particles -> particles.size }
            .windowed(1000)
            .takeWhile { it.toSet().size > 1 }
            .map { it.last() }
            .last()
    }

    private fun parseParticles(input: List<String>) = input.mapIndexed { index, s ->
        """p=<(.*?),(.*?),(.*?)>, v=<(.*?),(.*?),(.*?)>, a=<(.*?),(.*?),(.*?)>""".toRegex()
            .matchEntire(s)!!
            .groupValues.drop(1)
            .map { it.toLong() }
            .chunked(3) { (x, y, z) -> Vector3(x, y, z) }
            .let { (p, v, a) ->
                Particle(index, p, v, a)
            }
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

        fun distanceFromOrigin() = with(position) { x.absoluteValue + y.absoluteValue + z.absoluteValue }
    }
}