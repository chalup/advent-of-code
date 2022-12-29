package org.chalup.advent2020

import org.chalup.utils.textBlocks

object Day16 {
    fun task1(input: List<String>) = input
        .let(::parseData)
        .let {
            val ranges = it.validRangesByField.flatMap { (_, ranges) -> ranges }

            it.nearbyTickets
                .asSequence()
                .flatten()
                .filter { value -> ranges.none { range -> value in range } }
                .sum()
        }
}

private fun parseData(input: List<String>) = input
    .let(::textBlocks)
    .let { (dataBlock, myTicketBlock, nearbyTicketsBlock) ->
        val validRangesByField = dataBlock.associate {
            val (field, rangesText) = it.split(": ")
            val ranges = rangesText.split(" or ").map { rangeText ->
                rangeText.split("-").map(String::toInt).let { (a, b) -> a..b }
            }

            field to ranges
        }

        val myTicket = myTicketBlock.drop(1).single().let(::parseTicket)

        val nearbyTickets = nearbyTicketsBlock.drop(1).map(::parseTicket)

        TicketsData(validRangesByField, myTicket, nearbyTickets)
    }

private fun parseTicket(ticket: String) = ticket.split(",").map(String::toInt)

private data class TicketsData(
    val validRangesByField: Map<String, List<IntRange>>,
    val myTicket: List<Int>,
    val nearbyTickets: List<List<Int>>
)