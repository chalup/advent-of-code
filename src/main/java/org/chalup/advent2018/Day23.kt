package org.chalup.advent2018

import org.chalup.utils.parseNumbers
import kotlin.math.abs

object Day23 {
    data class Nanobot(val x: Int,
                       val y: Int,
                       val z: Int,
                       val signalStrength: Int)

    fun parse(input: List<String>) = input
        .map { parseNumbers(it).let { (x, y, z, signalStrength) -> Nanobot(x, y, z, signalStrength) } }

    fun part1(input: List<String>) = parse(input).let { bots ->
        val strongestBot = bots.maxByOrNull { it.signalStrength }!!

        infix fun Nanobot.isInRangeOf(bot: Nanobot): Boolean {
            val distance = abs(x - bot.x) + abs(y - bot.y) + abs(z - bot.z)
            return distance <= bot.signalStrength
        }

        bots.count { it isInRangeOf strongestBot }
    }
}