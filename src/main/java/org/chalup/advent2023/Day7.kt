package org.chalup.advent2023

object Day7 {
    fun task1(input: List<String>): Long = task(input, ::scoreHand)
    fun task2(input: List<String>): Long = task(input, ::scoreHandWithJokersRule)

    private fun task(input: List<String>, scoringFunction: (List<Card>) -> HandScore): Long = input
        .asSequence()
        .map(::parseHand)
        .map { it to scoringFunction(it.cards) }
        .sortedWith(
            compareByDescending<Pair<Hand, HandScore>> { (_, score) -> score.type }
                .thenBy { (_, score) -> score.cardsStrength }
        )
        .mapIndexed { rankIndex, (hand) -> hand.bid * (rankIndex + 1) }
        .sum()

}

private fun parseHand(input: String) = Hand(
    cards = input.substringBefore(' ').map { char -> Card.values().first { it.symbol == char } },
    bid = input.substringAfter(' ').toLong()
)

private data class Hand(
    val cards: List<Card>,
    val bid: Long,
)

private data class HandScore(
    val type: HandType,
    val cardsStrength: Long
)

private fun scoreHand(cards: List<Card>) = HandScore(
    type = cards.groupingBy { it }
        .eachCount()
        .values
        .sortedDescending()
        .let { counts -> HandType.values().first { it.counts == counts } },
    cardsStrength = cards.fold(0) { acc, card ->
        acc * 100 + card.ordinal
    }
)

private fun scoreHandWithJokersRule(cards: List<Card>): HandScore = HandScore(
    type = cards
        .filter { it != Card.JACK_OR_JOKER }
        .groupingBy { it }
        .eachCount()
        .values
        .sortedDescending()
        .let { if (it.isNotEmpty()) it.toMutableList() else mutableListOf(0) }
        .apply { set(0, this[0] + cards.count { it == Card.JACK_OR_JOKER }) }
        .let { counts -> HandType.values().first { it.counts == counts } },

    cardsStrength = cards.fold(0) { acc, card ->
        val cardValue = if (card == Card.JACK_OR_JOKER) 0 else card.ordinal + 1

        acc * 100 + cardValue
    }
)

private enum class HandType(val counts: List<Int>) {
    FIVE_OF_A_KIND(listOf(5)),
    FOUR_OF_A_KIND(listOf(4, 1)),
    FULL_HOUSE(listOf(3, 2)),
    THREE_OF_A_KIND(listOf(3, 1, 1)),
    TWO_PAIRS(listOf(2, 2, 1)),
    ONE_PAIR(listOf(2, 1, 1, 1)),
    HIGH_CARD(listOf(1, 1, 1, 1, 1)),
}

private enum class Card(val symbol: Char) {
    TWO('2'),
    THREE('3'),
    FOUR('4'),
    FIVE('5'),
    SIX('6'),
    SEVEN('7'),
    EIGHT('8'),
    NINE('9'),
    TEN('T'),
    JACK_OR_JOKER('J'),
    QUEEN('Q'),
    KING('K'),
    ACE('A'),
}