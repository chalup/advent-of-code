package org.chalup.advent2018

import org.chalup.utils.Point
import org.chalup.utils.Vector
import org.chalup.utils.bounds
import org.chalup.utils.manhattanDistance
import org.chalup.utils.plus

object Day15 {
    const val FLOOR = '.'
    const val WALL = '#'

    enum class Race(val symbol: Char) {
        ELF('E'),
        GOBLIN('G')
    }

    data class Entity(var position: Point,
                      val race: Race,
                      var hp: Int = 200,
                      val attack: Int = 3) {
        val isDead = hp <= 0
    }

    data class State(val map: Set<Point>, // wall tiles
                     val entities: List<Entity>,
                     val turnsPassed: Int = 0) {
        constructor(input: List<String>) : this(
            map = input
                .mapIndexed { y, row ->
                    row.mapIndexed { x, tile ->
                        Point(x, y).takeIf { tile == WALL }
                    }
                }
                .flatten()
                .filterNotNull()
                .toSet()
            ,
            entities = input
                .mapIndexed { y, row ->
                    row.mapIndexed { x, tile ->
                        Race
                            .values()
                            .find { it.symbol == tile }
                            ?.let { race ->
                                Entity(position = Point(x, y),
                                       race = race)
                            }
                    }
                }
                .flatten()
                .filterNotNull()
        )
    }

    fun State.print() {
        println("Turn #$turnsPassed")

        val entitiesByPosition = entities.associateBy { it.position }
        val (topLeft, bottomRight) = map.bounds()

        for (y in topLeft.y..bottomRight.y) {

            val rowEntities = mutableListOf<Entity>()

            for (x in topLeft.x..bottomRight.x) {
                val point = Point(x, y)

                val entity = entitiesByPosition[point]

                if (entity != null) {
                    rowEntities += entity
                    print(entity.race.symbol)
                } else {
                    print(if (point in map) WALL else FLOOR)
                }
            }

            print(
                rowEntities.joinToString(separator = ", ",
                                         prefix = "   ") { "${it.race.symbol}(${it.hp})" }
            )
            println()
        }
        println()
    }

    val pointComparator: Comparator<Point> = compareBy({ it.y }, { it.x })
    val entitiesComparator: Comparator<Entity> = compareBy(pointComparator) { it.position }

    fun simulate(initial: State) = generateSequence(initial) { state ->
        with(state) {
            entities
                .sortedWith(entitiesComparator)
                .forEach { entity ->
                    val simulationOver = entity.act(state)

                    if (simulationOver) return@generateSequence null
                }

            State(map,
                  entities.filterNot { it.isDead },
                  turnsPassed + 1)
        }
    }

    enum class Direction(val vector: Vector) {
        UP(Vector(0, -1)),
        DOWN(Vector(0, 1)),
        LEFT(Vector(-1, 0)),
        RIGHT(Vector(1, 0))
    }

    fun Entity.act(state: State): Boolean {
        if (isDead) return false

        val enemies = state
            .entities
            .filterNot { it.isDead }
            .filter { it.race != race } // damn, that's racist

        if (enemies.isEmpty()) return true

        val enemyToAttack = enemies
            .filter { manhattanDistance(position, it.position) == 1 }
            .sortedWith(entitiesComparator)
            .firstOrNull()

        if (enemyToAttack != null) {
            enemyToAttack.hp -= attack
        } else {
            val blockedTiles = state
                .entities
                .filterNot { it.isDead }
                .map { it.position }
                .toSet() + state.map

            val floodFill = floodFill(position, blockedTiles)

            val moveDestination = enemies
                .flatMap { enemy -> enemy.position.adjacentTiles() }
                .filter { it in floodFill.keys }
                .sortedWith(compareByDescending<Point> { floodFill.getValue(it) }.then(pointComparator))
                .firstOrNull() ?: return false

            //   backtrack to find the path
            //   choose the next step from the backtrack by ordering in reading order
        }

        return false
    }

    fun Point.adjacentTiles() = Direction.values().map { direction -> this + direction.vector }

    fun floodFill(startingPosition: Point, blockedTiles: Set<Point>): Map<Point, Int> {
        val result = mutableMapOf<Point, Int>()

        val queue = sortedSetOf(
            compareBy { point -> manhattanDistance(point, startingPosition) },
            startingPosition
        )
        while (queue.isNotEmpty()) {
            val point = queue.pollFirst()

            result[point] = manhattanDistance(point, startingPosition)

            queue += point
                .adjacentTiles()
                .filterNot { it in blockedTiles }
                .filterNot { it in result.keys }
                .filterNot { it in queue }
        }

        return result
    }
}