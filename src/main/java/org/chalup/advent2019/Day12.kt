package org.chalup.advent2019

import org.chalup.advent2018.lcm
import org.chalup.advent2019.Day12.Axis.X
import org.chalup.advent2019.Day12.Axis.Y
import org.chalup.advent2019.Day12.Axis.Z
import org.chalup.advent2019.Day12.MoonLocation
import org.chalup.advent2019.Day12.MoonVelocity
import org.chalup.utils.match
import org.chalup.utils.times
import kotlin.math.abs
import kotlin.math.sign

object Day12 {
    data class MoonLocation(val x: Long,
                            val y: Long,
                            val z: Long) {
        fun energy() = abs(x) + abs(y) + abs(z)

        operator fun get(axis: Axis) = when (axis) {
            X -> x
            Y -> y
            Z -> z
        }
    }

    data class MoonVelocity(val dx: Long,
                            val dy: Long,
                            val dz: Long) {
        fun energy() = abs(dx) + abs(dy) + abs(dz)

        operator fun get(axis: Axis) = when (axis) {
            X -> dx
            Y -> dy
            Z -> dz
        }
    }

    data class Moon(val location: MoonLocation,
                    val velocity: MoonVelocity) {
        fun energy() = location.energy() * velocity.energy()
    }

    fun parseInput(input: List<String>): List<Moon> = input
        .map(this::parseMoonLocation)
        .map {
            Moon(location = it,
                 velocity = MoonVelocity(0, 0, 0)
            )
        }

    fun parseMoonLocation(input: String): MoonLocation = match(input) {
        pattern("""\<x=(.*?), y=(.*?), z=(.*?)>""") { (x, y, z) -> MoonLocation(x.toLong(), y.toLong(), z.toLong()) }
    }

    fun simulate(moons: List<Moon>): List<Moon> {
        val velocityChanges = List(4) { MoonVelocity(0, 0, 0) }.toMutableList()

        (moons.indices * moons.indices)
            .filter { (i, j) -> i < j }
            .forEach { (i, j) ->
                val (ix, iy, iz) = moons[i].location
                val (jx, jy, jz) = moons[j].location

                val change = MoonVelocity((ix - jx).sign.toLong(), (iy - jy).sign.toLong(), (iz - jz).sign.toLong())

                velocityChanges[i] = velocityChanges[i] - change
                velocityChanges[j] = velocityChanges[j] + change
            }

        return moons
            .zip(velocityChanges) { moon, velocityChange -> moon.copy(velocity = moon.velocity + velocityChange) }
            .map { it.copy(location = it.location + it.velocity) }
    }

    fun task1(input: List<String>): Long {
        var moons = parseInput(input)

        repeat(1000) { moons = simulate(moons) }

        return moons.fold(0L) { energy, moon -> energy + moon.energy() }
    }

    fun task2(input: List<String>): Long {
        val moons = parseInput(input)

        return Axis.values()
            .map { findCycleLength(moons, it) }
            .let { cycles ->
                generateSequence(cycles) {
                    if (it.size == 1) null
                    else listOf(lcm(it[0], it[1])) + it.drop(2)
                }
            }
            .last().single()
    }

    enum class Axis { X, Y, Z }

    private fun findCycleLength(initialMoonsPosition: List<Moon>, axis: Axis): Long {
        fun data(moons: List<Moon>) = moons.map { (p, v) -> p[axis] to v[axis] }

        var moons = initialMoonsPosition
        var step = 0L
        val log: MutableMap<Any, Long> = mutableMapOf()

        while (true) {
            val moonsSnapshot = data(moons)

            val previousOccurrence = log.put(moonsSnapshot, step)

            if (previousOccurrence != null) return step - previousOccurrence

            moons = simulate(moons)
            step++
        }
    }
}

private operator fun MoonLocation.plus(v: MoonVelocity) = MoonLocation(x + v.dx, y + v.dy, z + v.dz)
private operator fun MoonVelocity.plus(change: MoonVelocity) = MoonVelocity(dx + change.dx, dy + change.dy, dz + change.dz)
private operator fun MoonVelocity.minus(change: MoonVelocity) = MoonVelocity(dx - change.dx, dy - change.dy, dz - change.dz)
