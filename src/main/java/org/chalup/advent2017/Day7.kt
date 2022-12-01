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
}

private fun parse(text: String): Program = match(text) {
    //language=RegExp
    pattern("""(.*?) \((\d+)\)(?: -> (.*))?""") { (name, weight, subprograms) ->
        Program(name, weight.toInt(), subprograms.splitToSequence(", ").toSet())
    }
}

private data class Program(val name: String, val weight: Int, val namesOfSupportedPrograms: Set<String>)