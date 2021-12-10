package org.chalup.advent2021

object Day4 {
    fun task1(input: List<String>): Int {
        val data = parseInput(input)

        val (numbers) = data
        var (_, cards) = data

        for (number in numbers) {
            cards = cards.map { it.mark(number) }

            cards
                .firstOrNull { it.isWinning }
                ?.run { return score * number }
        }

        throw IllegalStateException("No winning card?")
    }

    fun task2(input: List<String>): Int {
        val data = parseInput(input)

        val (numbers) = data
        var (_, cards) = data

        for (number in numbers) {
            val potentiallyLastMarkedCard = cards.singleOrNull()

            cards = cards.mapNotNull { card -> card.mark(number).takeUnless { it.isWinning } }

            potentiallyLastMarkedCard
                .takeIf { cards.isEmpty() }
                ?.run { return (score - number) * number }
        }

        throw IllegalStateException("No winning card?")
        // 3374 is too high 482 is too low?
    }

    private fun parseInput(input: List<String>): Pair<List<Int>, List<BingoCard>> {
        val numbers = input.first().split(",").map { it.toInt() }

        val cards = parseBingoCards(input.drop(1))

        return numbers to cards
    }

    private fun parseBingoCards(input: List<String>): List<BingoCard> = input
        .chunked(6) { cardLines ->
            cardLines
                .asSequence()
                .drop(1)
                .flatMap { cardLine -> cardLine.trim().split(WHITESPACES_REGEX).map(String::toInt) }
                .mapIndexed { index, number -> number to (0b1U shl index) }
                .toMap()
                .let { BingoCard(it) }
        }

    data class BingoCard(
        private val unmarkedNumbers: Map<Int, UInt>,
        private val markedNumbersMask: UInt = 0U
    ) {
        fun mark(number: Int): BingoCard =
            if (number !in unmarkedNumbers) this
            else BingoCard(
                unmarkedNumbers - number,
                markedNumbersMask or unmarkedNumbers.getValue(number)
            )

        val isWinning: Boolean
            get() = winningMasks.any { it and markedNumbersMask == it }

        val score: Int
            get() = unmarkedNumbers.keys.sum()
    }

    private val winningMasks = setOf<UInt>(
        0b11111_00000_00000_00000_00000U,
        0b00000_11111_00000_00000_00000U,
        0b00000_00000_11111_00000_00000U,
        0b00000_00000_00000_11111_00000U,
        0b00000_00000_00000_00000_11111U,
        0b10000_10000_10000_10000_10000U,
        0b01000_01000_01000_01000_01000U,
        0b00100_00100_00100_00100_00100U,
        0b00010_00010_00010_00010_00010U,
        0b00001_00001_00001_00001_00001U,
    )

    private val WHITESPACES_REGEX = """\s+""".toRegex()
}

