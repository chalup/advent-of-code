package org.chalup.utils

data class Cube(
    val xs: LongRange,
    val ys: LongRange,
    val zs: LongRange,
) {
    fun points() = buildSet {
        for (x in xs)
            for (y in ys)
                for (z in zs)
                    add(Vector3(x, y, z))
    }
}

infix fun Cube.intersects(other: Cube): Boolean =
    other.xs intersects this.xs && other.ys intersects this.ys && other.zs intersects this.zs