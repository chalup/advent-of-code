package org.chalup.advent2021

object Day21 {
    fun task1(input: List<String>): Int = input
        .let(this::parseStartingPositions)
        .let(::PracticeGameState)
        .let { initialGameState ->
            generateSequence(initialGameState) { gameState ->
                val roll = sequence { repeat(3) { yield(gameState.deterministicDice.next()) } }.sum()

                PracticeGameState(
                    playersState = gameState.playersState.toMutableList().apply {
                        set(
                            gameState.nextPlayerIndex,
                            get(gameState.nextPlayerIndex).let { activePlayerState ->
                                val finalPosition = (activePlayerState.position + roll) % 10

                                PlayerState(
                                    position = finalPosition,
                                    score = activePlayerState.score + (finalPosition + 1)
                                )
                            }
                        )
                    },
                    rollsCount = gameState.rollsCount + 3,
                    nextPlayerIndex = (gameState.nextPlayerIndex + 1) % gameState.playersState.size,
                    deterministicDice = gameState.deterministicDice
                )
            }
        }
        .dropWhile { gameState -> gameState.playersState.none { it.score >= 1000 } }
        .first()
        .let { finalGameState ->
            val loserScore = finalGameState.playersState.single { it.score < 1000 }.score

            loserScore * finalGameState.rollsCount
        }

    data class PlayerState(val position: Int, val score: Int)

    class PracticeGameState(
        val playersState: List<PlayerState>,
        val nextPlayerIndex: Int,
        val rollsCount: Int,
        val deterministicDice: Iterator<Int>
    ) {
        constructor(startingPositions: List<Int>) : this(
            playersState = startingPositions.map { position -> PlayerState(position - 1, score = 0) },
            nextPlayerIndex = 0,
            rollsCount = 0,
            deterministicDice = (generateSequence(1) { if (it == 100) 1 else it + 1 }).iterator()
        )
    }

    private fun parseStartingPositions(input: List<String>) =
        input.map { line -> line.substringAfterLast(' ').toInt() }
}
