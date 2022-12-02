package org.chalup.advent2022

import org.chalup.advent2018.cycleNext
import org.chalup.advent2018.cyclePrev

object Day2 {
    fun task1(input: List<String>): Int = input
        .sumOf { line ->
            val (opponentChar, playerChar) = line.split(" ")

            val opponent = when (opponentChar) {
                "A" -> Shape.ROCK
                "B" -> Shape.PAPER
                "C" -> Shape.SCISSORS
                else -> throw IllegalArgumentException("Unsupported opponent symbol $opponentChar in $line")
            }

            val player = when (playerChar) {
                "X" -> Shape.ROCK
                "Y" -> Shape.PAPER
                "Z" -> Shape.SCISSORS
                else -> throw IllegalArgumentException("Unsupported player symbol $opponentChar in $line")
            }

            val outcome = when (player) {
                opponent.cycleNext() -> RoundResult.WIN
                opponent -> RoundResult.DRAW
                else -> RoundResult.LOSS
            }

            player.points + outcome.points
        }

    fun task2(input: List<String>): Int = input
        .sumOf { line ->
            val (opponentChar, roundChar) = line.split(" ")

            val opponent = when (opponentChar) {
                "A" -> Shape.ROCK
                "B" -> Shape.PAPER
                "C" -> Shape.SCISSORS
                else -> throw IllegalArgumentException("Unsupported opponent symbol $opponentChar in $line")
            }

            val expectedOutcome = when (roundChar) {
                "X" -> RoundResult.LOSS
                "Y" -> RoundResult.DRAW
                "Z" -> RoundResult.WIN
                else -> throw IllegalArgumentException("Unsupported round symbol $roundChar in $line")
            }

            val player = when (expectedOutcome) {
                RoundResult.DRAW -> opponent
                RoundResult.WIN -> opponent.cycleNext()
                RoundResult.LOSS -> opponent.cyclePrev()
            }

            player.points + expectedOutcome.points
        }

    private enum class RoundResult(val points: Int) {
        WIN(6), DRAW(3), LOSS(0)
    }

    private enum class Shape(val points: Int) {
        ROCK(1), PAPER(2), SCISSORS(3)
    }
}