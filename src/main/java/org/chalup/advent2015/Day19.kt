package org.chalup.advent2015

import org.chalup.utils.textBlocks
import java.util.PriorityQueue

object Day19 {
    fun task1(input: List<String>): Int {
        val (replacements, molecule) = parseChemistry(input)

        return replacements
            .flatMapTo(mutableSetOf()) { (from, to) ->
                molecule.indicesOfSubstring(from)
                    .map { i ->
                        buildString {
                            append(molecule.take(i))
                            append(to)
                            append(molecule.drop(i).drop(from.length))
                        }
                    }
            }
            .size
    }

    private fun String.indicesOfSubstring(substring: String) = sequence {
        var start = 0
        while (true) {
            val nextIndex = indexOf(substring, start)
            if (nextIndex == -1) break

            yield(nextIndex)
            start = nextIndex + substring.length
        }
    }

    fun task2(input: List<String>): Int {
        val (replacements, targetMolecule) = parseChemistry(input)

        val seen = mutableSetOf<String>()
        val queue = PriorityQueue<Pair<String, Int>>(compareBy { (molecule, _) -> molecule.length }).apply {
            add(targetMolecule to 0)
        }

        while (queue.isNotEmpty()) {
            val (molecule, steps) = queue.poll()
            if (!seen.add(molecule)) continue

            if (molecule == "e") {
                return steps
            } else {
                replacements
                    .flatMapTo(queue) { (from, to) ->
                        molecule.indicesOfSubstring(to)
                            .map { i ->
                                buildString {
                                    append(molecule.take(i))
                                    append(from)
                                    append(molecule.drop(i).drop(to.length))
                                } to (steps + 1)
                            }
                    }
            }
        }

        throw IllegalStateException("Could not find the solution :(")
    }

    private fun parseChemistry(input: List<String>): Chemistry {
        val blocks = textBlocks(input)
        val molecule = blocks[1].single()

        val replacements = blocks[0].map {
            val (from, to) = it.split(" => ")
            from to to
        }

        return Chemistry(replacements, molecule)
    }

    private data class Chemistry(
        val replacements: List<Pair<String, String>>,
        val molecule: String
    )
}