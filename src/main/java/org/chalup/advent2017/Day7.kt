package org.chalup.advent2017

import org.chalup.utils.match

object Day7 {
    fun task1(input: List<String>) = input
        .map(::parse)
        .let { programs ->
            buildSet {
                programs.mapTo(this, Program::name)
                for (p in programs) removeAll(p.namesOfSupportedPrograms)
            }.single()
        }

    fun task2(input: List<String>): Int {
        val programs = input.map(::parse)
        val programsByName = programs.associateBy { it.name }

        fun stackWeight(name: String): Int {
            val program = programsByName.getValue(name)
            return program.weight + program.namesOfSupportedPrograms.sumOf { stackWeight(it) }
        }

        for (program in programs.filter { it.namesOfSupportedPrograms.size > 2 }) {
            // gotta has at least two other siblings, otherwise it would be ambiguous
            val groupedWeights = program.namesOfSupportedPrograms.groupBy { stackWeight(it) }
            if (groupedWeights.size == 2) {
                val (potentialTargetName, mismatchedGroupWeight) = groupedWeights.mapNotNull { (weight, names) -> names.singleOrNull()?.let { it to weight } }.single()
                val potentialTarget = programsByName.getValue(potentialTargetName)

                if (potentialTarget.namesOfSupportedPrograms.mapTo(mutableSetOf(), ::stackWeight).size == 1) {
                    // all children have the same weight, we found our target
                    val expectedGroupWeight = (groupedWeights.keys - mismatchedGroupWeight).single()
                    val delta = expectedGroupWeight - mismatchedGroupWeight
                    return potentialTarget.weight + delta
                }
            }
        }

        throw IllegalArgumentException("Boo")
    }
}

private fun parse(text: String): Program = match(text) {
    //language=RegExp
    pattern("""(.*?) \((\d+)\)(?: -> (.*))?""") { (name, weight, subprograms) ->
        Program(name, weight.toInt(), subprograms.split(", ").filter { it.isNotBlank() }.toSet())
    }
}

private data class Program(val name: String, val weight: Int, val namesOfSupportedPrograms: Set<String>)