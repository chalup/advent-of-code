package org.chalup.advent2020

object Day25 {
    fun task1(input: List<String>): Long {
        val (cardPublicKey, doorPublicKey) = input.map { it.toLong() }

        fun cryptoSequence(subjectNumber: Long) = generateSequence(1L) { (it * subjectNumber) % 20201227 }

        return cryptoSequence(7)
            .mapIndexed { loopNumber, value ->
                val cardLoopCount = loopNumber.takeIf { value == cardPublicKey }
                val doorLoopCount = loopNumber.takeIf { value == doorPublicKey }

                cardLoopCount to doorLoopCount
            }
            .mapNotNull { (cardLoopCount, doorLoopCount) ->
                when {
                    cardLoopCount != null -> cryptoSequence(doorPublicKey).drop(cardLoopCount).first()
                    doorLoopCount != null -> cryptoSequence(cardPublicKey).drop(doorLoopCount).first()
                    else -> null
                }
            }
            .first()
    }
}
