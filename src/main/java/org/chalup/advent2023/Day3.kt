package org.chalup.advent2023

object Day3 {
    fun task1(input: List<String>): Int = input
        .let { lines -> EngineSchematic.parse(lines) }
        .let { engineSchematic ->
            engineSchematic
                .numbers
                .filter { number ->
                    number
                        .coordinates
                        .flatMapTo(mutableSetOf()) { it.neighbours() }
                        .intersect(engineSchematic.symbols.keys)
                        .isNotEmpty()
                }
        }
        .sumOf { it.value }

    fun task2(input: List<String>): Int = input
        .let { lines -> EngineSchematic.parse(lines) }
        .let { engineSchematic ->
            engineSchematic
                .symbols
                .filter { (_, symbol) -> symbol == '*' }
                .keys
                .map { gearCoordinates ->
                    val gearNeighbourhood = gearCoordinates.neighbours().toSet()

                    engineSchematic.numbers
                        .filter { number -> number.coordinates.any { it in gearNeighbourhood } }
                        .map { it.value }
                }
                .filter { it.size == 2 }
                .sumOf { (a, b) -> a * b }
        }
}

private data class EngineSchematic(
    val symbols: Map<Point, Char>,
    val numbers: List<SchematicNumber>
) {
    companion object {
        fun parse(lines: List<String>): EngineSchematic {
            val symbols = buildMap {
                lines.forEachIndexed { y, line ->
                    line.forEachIndexed { x, char ->
                        if (char != '.' && !char.isDigit()) {
                            put(Point(x, y), char)
                        }
                    }
                }
            }

            val numbers = buildList<SchematicNumber> {
                lines.forEachIndexed { y, line ->
                    val iterator = line.withIndex().iterator()
                    var currentNumber: SchematicNumber? = null

                    while (iterator.hasNext()) {
                        val (x, char) = iterator.next()

                        if (char.isDigit()) {
                            val value = char.digitToInt()
                            val coordinates = Point(x, y)

                            currentNumber = currentNumber
                                ?.copy(
                                    value = currentNumber.value * 10 + value,
                                    coordinates = currentNumber.coordinates + coordinates
                                )
                                ?: SchematicNumber(value, setOf(coordinates))
                        } else {
                            if (currentNumber != null) {
                                add(currentNumber)
                                currentNumber = null
                            }
                        }
                    }

                    if (currentNumber != null) {
                        add(currentNumber)
                    }
                }
            }

            return EngineSchematic(symbols, numbers)
        }
    }
}

private data class SchematicNumber(
    val value: Int,
    val coordinates: Set<Point>
)

private data class Point(val x: Int, val y: Int) {
    override fun toString(): String = "($x, $y)"

    fun neighbours() = sequence {
        for (dy in -1..1) {
            for (dx in -1..1) {
                if (dx != 0 || dy != 0) {
                    yield(Point(x + dx, y + dy))
                }
            }
        }
    }
}
