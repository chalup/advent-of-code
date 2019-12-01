package org.chalup.advent2019

object Day1 {
    private fun parseInput(input: List<String>) = input.map { it.toInt() }

    fun fuelRequirement(mass: Int) = (mass / 3) - 2

    fun totalFuelRequirement(moduleMass: Int) = generateSequence(moduleMass) { mass -> fuelRequirement(mass) }
        .drop(1)
        .takeWhile { it > 0 }
        .sum()

    fun task1(input: List<String>): Int = parseInput(input).map { fuelRequirement(it) }.sum()
    fun task2(input: List<String>): Int = parseInput(input).map { totalFuelRequirement(it) }.sum()
}