package org.chalup.advent2017

import org.chalup.utils.primes

object Day23 {
    fun task1(input: List<String>): Int {
        var executedMuls = 0

        var pc = 0
        val registers = mutableMapOf<String, Long>().withDefault { 0 }

        while (true) {
            val instruction = input.getOrNull(pc)?.split(" ") ?: break

            fun getValue(i: Int) = instruction[i].toLongOrNull() ?: registers.getValue(instruction[i])

            when (instruction[0]) {
                "set" -> registers[instruction[1]] = getValue(2)
                "sub"-> registers[instruction[1]] = getValue(1) - getValue(2)
                "mul"-> {
                    executedMuls++
                    registers[instruction[1]] = getValue(1) * getValue(2)
                }
                "jnz"-> if (getValue(1) != 0L) {
                    pc += getValue(2).toInt()
                    continue
                }
            }

            pc++
        }

        return executedMuls
    }

    fun task2(input: List<String>): Int {
        // the assembly code checks how many numbers in range are not primes
        val start = 109_900
        val end = start + 17_000

        val primes = primes(end).toSet()

        return (start..end step 17).count { it !in primes }
    }
}