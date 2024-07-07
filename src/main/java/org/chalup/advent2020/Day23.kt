package org.chalup.advent2020

object Day23 {
    fun task1(input: List<String>): String {
        var cups = input.single()

        repeat(100) {
            fun previousCup(cup: Char) = if (cup == '1') '9' else (cup - 1)

            val removedCups = cups.drop(1).take(3)
            var destinationCup = previousCup(cups.first())
            while (destinationCup in removedCups) { destinationCup = previousCup(destinationCup) }

            cups = cups
                .replace(removedCups, "")
                .replace(destinationCup.toString(), "$destinationCup$removedCups")
                .let { cupsAfterMove -> cupsAfterMove.drop(1) + cupsAfterMove.take(1) }
        }

        return cups.substringAfter('1') + cups.substringBefore('1')
    }
}
