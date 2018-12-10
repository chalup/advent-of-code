package org.chalup.utils

data class Point(val x: Int, val y: Int)
data class Rect(val topLeft: Point, val bottomRight: Point) {
    constructor(left: Int, top: Int, right: Int, bottom: Int) : this(Point(left, top),
                                                                     Point(right, bottom))
}

operator fun Rect.contains(point: Point) = with(point) {
    val (minX, minY) = topLeft
    val (maxX, maxY) = bottomRight

    (x in minX..maxX) && (y in minY..maxY)
}

fun Rect.points() = ((topLeft.x..bottomRight.x) * (topLeft.y..bottomRight.y)).map { (x, y) -> Point(x, y) }


fun Iterable<Point>.bounds(): Rect {
    val (minX, maxX) = map { it.x }.let { it.min()!! to it.max()!! }
    val (minY, maxY) = map { it.y }.let { it.min()!! to it.max()!! }

    return Rect(minX, minY, maxX, maxY)
}

infix fun Point.isOnTheEdgeOf(bounds: Rect) = with(bounds) {
    when {
        x == topLeft.x -> true
        x == bottomRight.x -> true
        y == topLeft.y -> true
        y == bottomRight.y -> true
        else -> false
    }
}
