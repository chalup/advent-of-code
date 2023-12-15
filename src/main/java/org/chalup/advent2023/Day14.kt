package org.chalup.advent2023

object Day14 {
    fun task1(input: List<String>): Int {
        val platform = input.mapTo(mutableListOf()) { it.toMutableList() }

        tilt(platform, Direction.N)

        return northBeamsLoad(platform)
    }

    fun task2(input: List<String>): Int {
        val platform = input.mapTo(mutableListOf()) { it.toMutableList() }

        val cycles = 1_000_000_000
        val cache = mutableMapOf<String, Int>()
        for (currentCycle in 0 until cycles) {
            val key = arrangementKey(platform)
            val previousCycle = cache.put(key, currentCycle)
            if (previousCycle == null) {
                for (d in Direction.values()) {
                    tilt(platform, d)
                }
            } else {
                val cyclesRemainder = (cycles - previousCycle) % (currentCycle - previousCycle)
                repeat(cyclesRemainder) {
                    for (d in Direction.values()) {
                        tilt(platform, d)
                    }
                }
                break
            }
        }

        return northBeamsLoad(platform)
    }

    private fun arrangementKey(platform: List<List<Char>>) = platform.joinToString("") { it.joinToString(separator = "")}

    private fun northBeamsLoad(platform: List<List<Char>>) = platform
        .asSequence()
        .mapIndexed { y: Int, line: List<Char> ->
            line.count { it == 'O' } * (platform.size - y)
        }
        .sum()

    private fun tilt(platform: MutableList<MutableList<Char>>, direction: Direction) = with(direction) {
        val edge = platform.alongEdgeIndices.mapTo(mutableListOf()) { 0 }

        for (across in platform.acrossIndices) {
            for (along in platform.alongEdgeIndices) {
                when (platform[along, across]) {
                    'O' -> {
                        platform[along, across] = '.'
                        platform[along, edge[along]] = 'O'
                        edge[along] = edge[along] + 1
                    }

                    '#' -> edge[along] = across + 1
                }
            }
        }
    }

    private enum class Direction {
        N {
            override val List<List<Char>>.alongEdgeIndices: IntRange
                get() = first().indices

            override val List<List<Char>>.acrossIndices: IntRange
                get() = indices

            override fun List<List<Char>>.get(along: Int, across: Int): Char = this[across][along]

            override fun MutableList<MutableList<Char>>.set(along: Int, across: Int, char: Char) {
                this[across][along] = char
            }
        },
        W {
            override val List<List<Char>>.alongEdgeIndices: IntRange
                get() = indices

            override val List<List<Char>>.acrossIndices: IntRange
                get() = first().indices

            override fun List<List<Char>>.get(along: Int, across: Int): Char = this[along][across]

            override fun MutableList<MutableList<Char>>.set(along: Int, across: Int, char: Char) {
                this[along][across] = char
            }
        },
        S {
            override val List<List<Char>>.alongEdgeIndices: IntRange
                get() = first().indices

            override val List<List<Char>>.acrossIndices: IntRange
                get() = indices

            override fun List<List<Char>>.get(along: Int, across: Int): Char = this[lastIndex - across][along]

            override fun MutableList<MutableList<Char>>.set(along: Int, across: Int, char: Char) {
                this[lastIndex - across][along] = char
            }
        },
        E {
            override val List<List<Char>>.alongEdgeIndices: IntRange
                get() = indices

            override val List<List<Char>>.acrossIndices: IntRange
                get() = first().indices

            override fun List<List<Char>>.get(along: Int, across: Int): Char = this[along].let { it[it.lastIndex - across] }

            override fun MutableList<MutableList<Char>>.set(along: Int, across: Int, char: Char) {
                this[along].run { set(lastIndex - across, char) }
            }
        };

        abstract val List<List<Char>>.alongEdgeIndices: IntRange
        abstract val List<List<Char>>.acrossIndices: IntRange
        abstract operator fun List<List<Char>>.get(along: Int, across: Int): Char
        abstract operator fun MutableList<MutableList<Char>>.set(along: Int, across: Int, char: Char)
    }
}
