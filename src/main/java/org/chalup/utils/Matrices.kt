package org.chalup.utils

import java.math.BigInteger

interface Matrix {
    val rows: Int
    val cols: Int
    operator fun get(row: Int, col: Int): BigInteger
}

fun Matrix.asString() = buildString {
    val maxValueLength = (0 until rows)
        .flatMap { r -> (0 until cols).map { c -> get(r, c).toString().length } }
        .max()

    for (r in 0 until rows) {
        append("|")
        (0 until cols)
            .joinToString(separator = " ") { c -> get(r, c).toString().padStart(maxValueLength) }
            .run { append(this) }

        appendLine("|")
    }
}

data class ValueMatrix(
    override val rows: Int,
    override val cols: Int,
    private val valuesByRow: List<BigInteger>
) : Matrix {
    init {
        check(valuesByRow.size == rows * cols)
    }

    override fun get(row: Int, col: Int): BigInteger = valuesByRow[(row * cols) + col]
}

fun Matrix.transposed(): Matrix = TransposedMatrix(this)

private class TransposedMatrix(private val src: Matrix) : Matrix {
    override val rows: Int = src.cols
    override val cols: Int = src.rows

    override fun get(row: Int, col: Int): BigInteger = src[col, row]
}

operator fun Matrix.times(scalar: BigInteger): Matrix {
    val src = this
    return object : Matrix  by src {
        override fun get(row: Int, col: Int): BigInteger =
            src[row, col] * scalar
    }
}

operator fun Matrix.div(scalar: BigInteger): Matrix {
    val src = this
    return object : Matrix  by src {
        override fun get(row: Int, col: Int): BigInteger =
            src[row, col]
                .also { check(it.abs() % scalar == BigInteger.ZERO) }
                .let { it / scalar }
    }
}

operator fun Matrix.times(other: Matrix): Matrix = MultipliedMatrix(this, other)

fun Matrix.getRow(r: Int) = (0 until cols).asSequence().map { get(r, it) }
fun Matrix.getColumn(c: Int) = (0 until rows).asSequence().map { get(it, c) }

private class MultipliedMatrix(
    private val lhs: Matrix,
    private val rhs: Matrix,
) : Matrix {
    init {
        check(lhs.cols == rhs.rows)
    }

    override val rows: Int = lhs.rows
    override val cols: Int = rhs.cols

    override fun get(row: Int, col: Int): BigInteger =
        lhs.getRow(row)
            .zip(rhs.getColumn(col), BigInteger::times)
            .reduce(BigInteger::plus)
}

fun Matrix.withoutColumn(c: Int): Matrix {
    check(c in 0 until cols)

    val src = this
    return object : Matrix {
        override val rows: Int = src.rows
        override val cols: Int = src.cols - 1

        override fun get(row: Int, col: Int): BigInteger =
            if (col < c) src[row, col]
            else src[row, col + 1]
    }
}

fun Matrix.withoutRow(r: Int): Matrix {
    check(r in 0 until rows)

    val src = this
    return object : Matrix {
        override val rows: Int = src.rows - 1
        override val cols: Int = src.cols

        override fun get(row: Int, col: Int): BigInteger =
            if (row < r) src[row, col]
            else src[row + 1, col]
    }
}

fun Matrix.determinant(): BigInteger {
    check(rows == cols)

    if (rows == 1) return get(0, 0)

    return (0 until cols).sumOf { c ->
        get(0, c) * (if (c % 2 == 0) BigInteger.ONE else BigInteger.valueOf(-1)) * this.withoutRow(0).withoutColumn(c).determinant()
    }
}

fun Matrix.matrixOfMinors(): Matrix {
    val src = this

    return object : Matrix by src {
        override fun get(row: Int, col: Int): BigInteger =
            src.withoutColumn(col).withoutRow(row).determinant()
    }
}

fun Matrix.matrixOfCoefficients(): Matrix {
    val src = this

    return object : Matrix by src {
        override fun get(row: Int, col: Int): BigInteger =
            src[row, col] * (if ((row + col) % 2 == 0) BigInteger.ONE else BigInteger.valueOf(-1))
    }
}