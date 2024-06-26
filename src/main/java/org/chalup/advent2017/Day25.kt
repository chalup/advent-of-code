package org.chalup.advent2017

import org.chalup.utils.textBlocks

object Day25 {
    fun task1(input: List<String>): Int {
        val blocks = textBlocks(input)
        var currentState = blocks.first().first().trim('.').split(" ").last().single()
        val steps = blocks.first().last().split(" ").mapNotNull(String::toIntOrNull).single()

        val statesById = buildMap {
            blocks.drop(1).forEach {
                val i = it.iterator()
                val id = i.next().trim(':').split(" ").last().single()

                fun parseAction(): Action {
                    val setBit = when (i.next().trim('.').split(" ").last().toInt()) {
                        1 -> true
                        0 -> false
                        else -> throw IllegalStateException()
                    }
                    val cursorDelta = when (i.next().trim('.').split(" ").last()) {
                        "left" -> -1
                        "right" -> +1
                        else -> throw IllegalStateException()
                    }
                    val nextState = i.next().trim('.').split(" ").last().single()

                    return Action(setBit, cursorDelta, nextState)
                }

                put(id, State(buildMap {
                    i.next()
                    put(false, parseAction())
                    i.next()
                    put(true, parseAction())
                }))
            }
        }

        val tape = mutableSetOf<Int>()
        var cursor = 0

        repeat(steps) {
            val isBitSet = cursor in tape
            val action = statesById.getValue(currentState).actions.getValue(isBitSet)

            if (action.setBit) {
                tape += cursor
            } else {
                tape -= cursor
            }

            cursor += action.cursorDelta
            currentState = action.nextState
        }

        return tape.size
    }

    private data class State(val actions: Map<Boolean, Action>)
    private data class Action(val setBit: Boolean, val cursorDelta: Int, val nextState: Char)
}