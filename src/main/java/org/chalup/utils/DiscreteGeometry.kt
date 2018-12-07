package org.chalup.utils

data class Point(val x: Int, val y: Int)
data class Rect(val topLeft: Point, val bottomRight: Point) {
    constructor(left: Int, top: Int, right: Int, bottom: Int) : this(Point(left, top),
                                                                     Point(right, bottom))
}

fun Rect.points() = ((topLeft.x..bottomRight.x) * (topLeft.y..bottomRight.y)).map { (x, y) -> Point(x, y) }

infix fun Point.isOnTheEdgeOf(bounds: Rect) = with(bounds) {
    when {
        x == topLeft.x -> true
        x == bottomRight.x -> true
        y == topLeft.y -> true
        y == bottomRight.y -> true
        else -> false
    }
}
