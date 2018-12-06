package org.chalup.advent2015

object Day2 {
    private fun parse(dimensions: String) = dimensions.split("x").map { it.toInt() }

    private fun paperAmountForPackage(l: Int, w: Int, h: Int) = listOf(l * w, w * h, l * h)
        .sorted()
        .let { (a, b, c) -> 2 * (a + b + c) + a }

    fun paperAmountForAllPackages(dimensions: List<String>) = dimensions
        .map { parse(it) }
        .map { (l, w, h) -> paperAmountForPackage(l, w, h) }
        .sum()

    fun ribbonAmountForAllPackages(dimensions: List<String>) = dimensions
        .map { parse(it) }
        .map { (l, w, h) -> ribbonAmountToWrapThePackage(l, w, h) + ribbonAmountForTheBow(l, w, h) }
        .sum()

    private fun ribbonAmountToWrapThePackage(l: Int, w: Int, h: Int) = listOf(l + w, w + h, l + h)
        .map { it * 2 }
        .min()!!

    private fun ribbonAmountForTheBow(l: Int, w: Int, h: Int) = l * w * h
}