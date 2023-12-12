package org.chalup.advent2023

object Day12 {
    fun task1(input: List<String>): Long = input
        .map(::parseReportLine)
        .sumOf { line -> possibleArrangements(line) }

    fun task2(input: List<String>): Long = input
        .map(::parseReportLine)
        .map { line ->
            ReportLine(
                line = List(5) { line.line }.joinToString(separator = "?"),
                report = List(5) { line.report }.flatten()
            )
        }
        .sumOf { line -> possibleArrangements(line) }

    private fun possibleArrangements(reportLine: ReportLine): Long {
        val cache = mutableMapOf<ReportLine, Long>()

        fun combinations(reportLine: ReportLine): Long = cache.getOrPut(reportLine) {
            if (reportLine.line.isEmpty()) {
                if (reportLine.report.isEmpty()) 1 else 0
            } else if (reportLine.report.isEmpty()) {
                if (reportLine.line.contains('#')) 0 else 1
            } else when (val head = reportLine.line.first()) {
                '?' -> combinations(reportLine.consumeEmptySpace()) + (reportLine.consumeSprings()?.let(::combinations) ?: 0)
                '#' -> reportLine.consumeSprings()?.let(::combinations) ?: 0
                '.' -> combinations(reportLine.consumeEmptySpace())
                else -> throw IllegalArgumentException("Unexpected char $head")
            }
        }

        return combinations(reportLine)
    }
}

private fun parseReportLine(line: String) = ReportLine(
    line = line.substringBefore(' '),
    report = line.substringAfter(' ').split(',').map(String::toInt)
)

private data class ReportLine(
    val line: String,
    val report: List<Int>
) {
    fun consumeSprings(): ReportLine? {
        if (report.isEmpty()) return null
        val springsChainSize = report.first()

        if (line.length < springsChainSize) return null
        if (line.take(springsChainSize).any { it == '.' }) return null
        if (line.getOrNull(springsChainSize) == '#') return null

        return ReportLine(line.drop(springsChainSize + 1), report.drop(1))
    }
    fun consumeEmptySpace(): ReportLine = copy(line = line.drop(1))
}