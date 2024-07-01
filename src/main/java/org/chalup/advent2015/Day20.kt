package org.chalup.advent2015

import com.google.common.math.IntMath.pow
import org.chalup.utils.Counter
import org.chalup.utils.allDividers
import org.chalup.utils.primeFactors
import org.chalup.utils.primes

object Day20 {
    fun task1(input: List<String>): Int {
        val number = input.first().toInt()

        val primes = primes(number / 10)

        return generateSequence(1) { it + 1 }
            .first { houseNumber ->
                val sumOfDividers = Counter(primeFactors(houseNumber, primes))
                    .entries
                    .fold(1) { acc, (prime, power) -> acc * ((pow(prime, power + 1) - 1) / (prime - 1)) }

                val numberOfPresents = sumOfDividers * 10

                numberOfPresents >= number
            }
    }

    fun task2(input: List<String>): Int {
        val number = input.first().toInt()

        val primes = primes(number)

        return generateSequence(1) { it + 1 }
            .first { houseNumber ->
                val presents = allDividers(houseNumber, primes)
                    .filter { elfNumber -> houseNumber / elfNumber <= 50 }
                    .sumOf { it * 11 }

                presents >= number
            }
    }
}