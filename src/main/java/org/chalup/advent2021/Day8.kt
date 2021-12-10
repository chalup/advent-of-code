package org.chalup.advent2021

import org.chalup.utils.match

object Day8 {
    fun task1(input: List<String>): Int {
        return input
            .asSequence()
            .map(this::parseWireSchematics)
            .flatMap { it.outputs }
            .map { it.size }
            .count { it < 5 || it == 7 }
    }

    fun task2(input: List<String>): Int {
        return input
            .asSequence()
            .map(this::parseWireSchematics)
            .map(this::decode)
            .sum()
    }

    private fun decode(schematics: WireSchematics): Int {
        val one = schematics.inputs.single { it.size == 2 }
        val seven = schematics.inputs.single { it.size == 3 }
        val four = schematics.inputs.single { it.size == 4 }

        val twoThreeFive = schematics.inputs.filter { it.size == 5 }

        val segmentA = (seven - one).single()
        val segmentG = twoThreeFive
            .map { digitSegments -> digitSegments.filterTo(mutableSetOf()) { it !in seven && it !in four } }
            .reduce<Set<Char>, Set<Char>>(Set<Char>::intersect)
            .single()

        val segmentE = twoThreeFive
            .map { digitSegments -> digitSegments.filterTo(mutableSetOf()) { it !in seven && it !in four && it != segmentG } }
            .flatten()
            .single()

        val segmentD = twoThreeFive
            .single { it.containsAll(one) }
            .let { it - one - segmentG - segmentA }
            .single()

        val segmentB = (four - one - segmentD).single()
        val segmentC = twoThreeFive
            .single { segmentE in it }
            .let { it - segmentA - segmentD - segmentE - segmentG }
            .single()

        val segmentF = one.single { it != segmentC }

        val mapping = mapOf(
            setOf(segmentC, segmentF) to 1,
            setOf(segmentA, segmentC, segmentD, segmentE, segmentG) to 2,
            setOf(segmentA, segmentC, segmentD, segmentF, segmentG) to 3,
            setOf(segmentB, segmentC, segmentD, segmentF) to 4,
            setOf(segmentA, segmentB, segmentD, segmentF, segmentG) to 5,
            setOf(segmentA, segmentB, segmentD, segmentE, segmentF, segmentG) to 6,
            setOf(segmentA, segmentC, segmentF) to 7,
            setOf(segmentA, segmentB, segmentC, segmentD, segmentE, segmentF, segmentG) to 8,
            setOf(segmentA, segmentB, segmentC, segmentD, segmentF, segmentG) to 9,
            setOf(segmentA, segmentB, segmentC, segmentE, segmentF, segmentG) to 0,
        )

        return schematics
            .outputs
            .fold(0) { acc, digit -> acc * 10 + mapping.getValue(digit) }
    }

    private fun parseWireSchematics(input: String) = match<WireSchematics>(input) {
        pattern("""(.*) \| (.*)""") { (inputs, outputs) ->
            WireSchematics(
                inputs = inputs.split(" ").map { it.toSet() },
                outputs = outputs.split(" ").map { it.toSet() },
            )
        }
    }

    data class WireSchematics(
        val inputs: List<Set<Char>>,
        val outputs: List<Set<Char>>
    )
}
