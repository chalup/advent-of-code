package org.chalup.advent2020

import org.chalup.utils.match

object Day4 {
    fun task1(input: List<String>): Int = parsePassports(input)
        .count { it.keys.containsAll(REQUIRED_FIELDS) }

    fun task2(input: List<String>): Int = parsePassports(input)
        .count { isValid(it) }

    private fun parsePassports(input: List<String>): Sequence<Map<String, String>> = sequence {
        var passport = mutableMapOf<String, String>()
        input.forEach { line ->
            if (line.isBlank()) {
                yield(passport)
                passport = mutableMapOf()
            } else {
                line
                    .split(" ")
                    .map { it.split(":") }
                    .forEach { (key, value) -> passport[key] = value }
            }
        }

        if (passport.isNotEmpty()) yield(passport)
    }

    private fun isValid(passport: Map<String, String>): Boolean {
        passport["byr"]?.toIntOrNull()?.takeIf { it in 1920..2002 } ?: return false
        passport["iyr"]?.toIntOrNull()?.takeIf { it in 2010..2020 } ?: return false
        passport["eyr"]?.toIntOrNull()?.takeIf { it in 2020..2030 } ?: return false

        passport["hgt"]
            ?.let {
                try {
                    match<Height>(it) {
                        pattern("""(\d+)in""") { (inches) -> Height.Imperial(inches = inches.toInt()) }
                        pattern("""(\d+)cm""") { (cm) -> Height.Metric(centimeters = cm.toInt()) }
                    }
                } catch (e: Exception) {
                    null
                }
            }
            ?.takeIf { it.isValid() }
            ?: return false

        passport["hcl"]?.takeIf { it.matches("""#[0-9a-f]{6}""".toRegex()) } ?: return false
        passport["ecl"]?.takeIf { it in VALID_EYE_COLORS } ?: return false

        passport["pid"]
            ?.takeIf { passportId -> passportId.length == 9 && passportId.all { it.isDigit() } }
            ?: return false

        return true
    }

    private sealed class Height {
        data class Imperial(val inches: Int) : Height()
        data class Metric(val centimeters: Int) : Height()
    }

    private fun Height.isValid(): Boolean = when (this) {
        is Height.Imperial -> inches in 59..76
        is Height.Metric -> centimeters in 150..193
    }

    private val VALID_EYE_COLORS = setOf(
        "amb", "blu", "brn", "gry", "grn", "hzl", "oth"
    )

    private val REQUIRED_FIELDS = setOf(
        "byr",
        "iyr",
        "eyr",
        "hgt",
        "hcl",
        "ecl",
        "pid"
    )
}