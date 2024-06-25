package org.chalup.advent2017

object Day16 {
    fun task1(input: List<String>): String = doTheDance(input, ('a'..'p').toList()).joinToString(separator = "")
    fun task2(input: List<String>): String {
        val cache = linkedSetOf<String>()
        val arrangementByIndex = mutableMapOf<Int, String>()

        var programsArrangement = ('a'..'p').toList()

        while (true) {
            val cacheString = programsArrangement.joinToString("")

            programsArrangement = doTheDance(input, programsArrangement)

            if (cacheString in cache) {
                val cycleStart = cache.indexOf(cacheString)
                val cycleLength = cache.size - cycleStart

                val indexFromCycleStart = (1_000_000_000 - cycleStart) % cycleLength

                return cache.asSequence().drop(cycleStart).drop(indexFromCycleStart).first()
            } else {
                arrangementByIndex[cache.size] = cacheString
                cache.add(cacheString)
            }
        }
    }

    private fun doTheDance(input: List<String>, initialProgramsArrangement: List<Char>): List<Char> {
        return input.single().split(",")
            .fold(initialProgramsArrangement) { programs, command ->
                when (command[0]) {
                    's' -> {
                        val amount = command.drop(1).toInt()
                        programs.takeLast(amount) + programs.dropLast(amount)
                    }

                    'x' -> {
                        val (a, b) = command.drop(1).split("/").map(String::toInt)

                        programs.toMutableList().apply {
                            set(a, programs[b])
                            set(b, programs[a])
                        }
                    }

                    'p' -> {
                        val (a, b) = command.drop(1).split("/").map(String::single)

                        programs.toMutableList().apply {
                            set(programs.indexOf(a), b)
                            set(programs.indexOf(b), a)
                        }
                    }

                    else -> throw IllegalArgumentException("Unsupported command")
                }
            }
    }
}