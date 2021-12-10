package org.chalup.advent2021

object Day3 {
    fun task1(input: List<String>): Long = input
        .let { diagnosticReport ->
            val onesCounts = buildMap<Int, Int> {
                diagnosticReport.forEach { line ->
                    line.forEachIndexed { i, c ->
                        if (c == '1') this[i] = this.getOrDefault(i, 0) + 1
                    }
                }
            }

            val gammaString = List(onesCounts.keys.size) { i ->
                if (onesCounts.getValue(i) >= diagnosticReport.size / 2) '1' else '0'
            }.joinToString(separator = "")

            val epsilonString = gammaString.asSequence().joinToString(separator = "") { if (it == '0') "1" else "0" }

            gammaString.toLong(radix = 2) * epsilonString.toLong(radix = 2)
        }

    fun task2(input: List<String>): Long = input
        .let { diagnosticReport ->
            val oxygenRating = getRating(diagnosticReport, flipRating = false)
            val carbonDioxideRating = getRating(diagnosticReport, flipRating = true)

            oxygenRating * carbonDioxideRating
        }

    private fun getRating(diagnosticReport: List<String>, flipRating: Boolean): Long {
        var readings = diagnosticReport
        var index = 0

        while (readings.size > 1) {
            val (ones, zeros) = readings.partition { it[index] == '1' }


            readings = if ((ones.size >= zeros.size) xor flipRating) ones else zeros

            index++
        }

        return readings.first().toLong(radix = 2)
    }
}

