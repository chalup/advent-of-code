package org.chalup.advent2021

object Day21 {
    fun task1(input: List<String>): Int = input
        .let(this::parseStartingPositions)
        .let(::PracticeGameState)
        .let { initialGameState ->
            generateSequence(initialGameState) { gameState ->
                val roll = sequence { repeat(3) { yield(gameState.deterministicDice.next()) } }.sum()

                PracticeGameState(
                    playersState = gameState.playersState.update(gameState.nextPlayerIndex) { activePlayerState ->
                        activePlayerState + roll
                    },
                    rollsCount = gameState.rollsCount + 3,
                    nextPlayerIndex = (gameState.nextPlayerIndex + 1) % gameState.playersState.size,
                    deterministicDice = gameState.deterministicDice
                )
            }
        }
        .dropWhile { gameState -> gameState.playersState.none { it.score >= PracticeGameState.SCORE_TO_WIN } }
        .first()
        .let { finalGameState ->
            val loserScore = finalGameState.playersState.single { it.score < PracticeGameState.SCORE_TO_WIN }.score

            loserScore * finalGameState.rollsCount
        }

    fun task2(input: List<String>): Long = input
        .let(this::parseStartingPositions)
        .let { (playerOnePosition, playerTwoPosition) ->
            val cache = mutableMapOf<QuantumGameState, WinsCounts>()
            fun wins(gameState: QuantumGameState): WinsCounts {
                return cache.getOrPut(gameState) {
                    when {
                        gameState.playerOne.score >= QuantumGameState.SCORE_TO_WIN -> WinsCounts(1, 0)
                        gameState.playerTwo.score >= QuantumGameState.SCORE_TO_WIN -> WinsCounts(0, 1)
                        else -> sequence {
                            for (r1 in 1..3)
                                for (r2 in 1..3)
                                    for (r3 in 1..3) {
                                        yield(wins(gameState + (r1 + r2 + r3)))
                                    }
                        }.reduce(WinsCounts::plus)
                    }
                }
            }

            val initialGameState = QuantumGameState(
                playerOne = PlayerState(
                    position = playerOnePosition - 1,
                    score = 0
                ),
                playerTwo = PlayerState(
                    position = playerTwoPosition - 1,
                    score = 0
                ),
                activePlayer = 0
            )

            wins(initialGameState)
                .let { (one, two) -> maxOf(one, two) }
        }

    data class PlayerState(val position: Int, val score: Int) {
        operator fun plus(roll: Int) = ((position + roll) % 10)
            .let { newPosition ->
                PlayerState(position = newPosition, score = score + newPosition + 1)
            }
    }

    data class WinsCounts(val playerOneWins: Long, val playerTwoWins: Long) {
        operator fun plus(other: WinsCounts) = WinsCounts(
            playerOneWins = playerOneWins + other.playerOneWins,
            playerTwoWins = playerTwoWins + other.playerTwoWins,
        )
    }

    data class QuantumGameState(
        val playerOne: PlayerState,
        val playerTwo: PlayerState,
        val activePlayer: Int,
    ) {
        operator fun plus(roll: Int) = when (activePlayer) {
            0 -> copy(playerOne = playerOne + roll, activePlayer = 1)
            1 -> copy(playerTwo = playerTwo + roll, activePlayer = 0)
            else -> throw IllegalStateException()
        }

        companion object {
            const val SCORE_TO_WIN = 21
        }
    }

    private fun <T> List<T>.update(index: Int, block: (T) -> T) = toMutableList()
        .apply { set(index, block(get(index))) }

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

        companion object {
            const val SCORE_TO_WIN = 1000
        }
    }

    private fun parseStartingPositions(input: List<String>) =
        input.map { line -> line.substringAfterLast(' ').toInt() }
}
