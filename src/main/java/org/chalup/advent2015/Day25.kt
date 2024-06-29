package org.chalup.advent2015

object Day25 {
    fun task1(input: List<String>): Long {
        val (inputRow, inputColumn) = input.first().split(" ").mapNotNull { it.trim(',', '.').toIntOrNull() }

        data class Code(val row: Int, val column: Int, val code: Long)

        val codesSequence = generateSequence(Code(1, 1, 20151125L)) { (row, column, code) ->
            val nextCode = (code * 252533L) % 33554393L
            val nextRow = if (row == 1) column + 1 else row - 1
            val nextColumn = if (row == 1) 1 else column + 1

            Code(nextRow, nextColumn, nextCode)
        }

        return codesSequence
            .first { (row, column, _) -> row == inputRow && column == inputColumn }
            .let { (_, _, code) -> code }
    }
}
