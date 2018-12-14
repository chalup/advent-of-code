package org.chalup.advent2018

import org.chalup.advent2018.Day13.Direction.DOWN
import org.chalup.advent2018.Day13.Direction.LEFT
import org.chalup.advent2018.Day13.Direction.RIGHT
import org.chalup.advent2018.Day13.Direction.UP
import org.chalup.advent2018.Day13.OnIntersection.GO_STRAIGHT
import org.chalup.advent2018.Day13.OnIntersection.TURN_LEFT
import org.chalup.advent2018.Day13.OnIntersection.TURN_RIGHT
import org.chalup.utils.Point
import java.util.LinkedList

object Day13 {
    inline fun <reified T : Enum<T>> Enum<T>.cycleNext(): T = enumValues<T>().run { get((ordinal + 1) % size) }
    inline fun <reified T : Enum<T>> Enum<T>.cyclePrev(): T = enumValues<T>().run { get((ordinal + size - 1) % size) }

    enum class OnIntersection {
        TURN_LEFT, GO_STRAIGHT, TURN_RIGHT;
    }

    enum class Direction(val symbol: Char) {
        LEFT('<'),
        UP('^'),
        RIGHT('>'),
        DOWN('v');

        companion object {
            fun fromMapTile(tile: Char) = values().find { it.symbol == tile }
        }
    }

    data class Cart(val position: Point,
                    val direction: Direction,
                    val onIntersection: OnIntersection = TURN_LEFT)

    data class State(val map: List<String>,
                     val carts: List<Cart>,
                     val crashes: Set<Point> = emptySet()) {
        constructor(map: List<String>) : this(
            map = map.map {
                it
                    .replace('>', '-')
                    .replace('<', '-')
                    .replace('v', '|')
                    .replace('^', '|')
            },
            carts = map
                .mapIndexed { y, row ->
                    row.mapIndexed { x, char ->
                        Direction.fromMapTile(char)?.let { direction -> Cart(Point(x, y), direction) }
                    }
                }
                .flatten()
                .filterNotNull()
        )
    }

    operator fun List<String>.get(position: Point): Char = position.let { (x, y) -> this[y][x] }

    infix fun Point.moveIn(direction: Direction) = let { (x, y) ->
        when (direction) {
            LEFT -> Point(x - 1, y)
            RIGHT -> Point(x + 1, y)
            UP -> Point(x, y - 1)
            DOWN -> Point(x, y + 1)
        }
    }

    infix fun Direction.change(onIntersection: OnIntersection) = when (onIntersection) {
        TURN_LEFT -> this.cyclePrev()
        GO_STRAIGHT -> this
        TURN_RIGHT -> this.cycleNext()
    }

    fun simulate(initialState: State, cleanupCrashes: Boolean = false) = generateSequence(initialState) { state ->
        with(state) {
            if (carts.isEmpty()) return@generateSequence null

            val cartQueue = carts
                .sortedWith(compareBy({ it.position.y }, { it.position.x }))
                .let { LinkedList(it) }

            val newCrashes = crashes.toMutableSet()
            val remainingCarts = mutableListOf<Cart>()

            while (cartQueue.isNotEmpty()) {
                val cart: Cart = cartQueue.removeFirst()

                val newCartPosition = with(cart) { position moveIn direction }

                val otherCart = (cartQueue + remainingCarts).find { it.position == newCartPosition }
                when {
                    otherCart != null -> {
                        if (!cleanupCrashes) {
                            newCrashes += newCartPosition
                        }
                        // remove cart we just crashed at
                        cartQueue.remove(otherCart)
                        remainingCarts.remove(otherCart)
                    }
                    newCartPosition in newCrashes -> {
                        // nothing to do here, we're just piling upon the existing crash, lol
                    }
                    else -> with(cart) {
                        val newCartDirection = when (map[newCartPosition]) {
                            in "-|" -> direction
                            '+' -> direction change onIntersection
                            '/' -> when (direction) {
                                LEFT -> DOWN
                                UP -> RIGHT
                                DOWN -> LEFT
                                RIGHT -> UP
                            }
                            '\\' -> when (direction) {
                                LEFT -> UP
                                UP -> LEFT
                                DOWN -> RIGHT
                                RIGHT -> DOWN
                            }
                            else -> throw IllegalStateException("WTH is on that map?!")
                        }

                        val newOnIntersection =
                            if (map[newCartPosition] == '+') onIntersection.cycleNext()
                            else onIntersection

                        remainingCarts += Cart(newCartPosition,
                                               newCartDirection,
                                               newOnIntersection)
                    }
                }
            }

            State(map, remainingCarts, newCrashes)
        }
    }

    fun printMap(state: State) = with(state) {
        val cartsByPosition = carts.associateBy { it.position }

        map.forEachIndexed { y, row ->
            row.forEachIndexed { x, tile ->
                val position = Point(x, y)

                val cart = cartsByPosition[position]

                val symbol = when {
                    position in crashes -> 'X'
                    cart != null -> cart.direction.symbol
                    else -> tile
                }
                print(symbol)
            }
            println()
        }
        println()
    }
}