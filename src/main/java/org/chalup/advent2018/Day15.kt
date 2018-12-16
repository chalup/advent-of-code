package org.chalup.advent2018

import org.chalup.advent2018.Day15.Race.ELF
import org.chalup.advent2018.Day15.Race.GOBLIN
import org.chalup.utils.Point
import org.chalup.utils.Vector
import org.chalup.utils.bounds
import org.chalup.utils.plus
import java.util.PriorityQueue

object Day15 {
    const val FLOOR = '.'
    const val WALL = '#'

    enum class Race(val symbol: Char) {
        ELF('E'),
        GOBLIN('G')
    }

    data class Entity(var position: Point,
                      val race: Race,
                      var readingOrder: Int = 0,
                      var hp: Int = 200,
                      val attack: Int = 3) {
        val isDead: Boolean
            get() = hp <= 0
    }

    data class State(val map: Set<Point>, // wall tiles
                     val entities: List<Entity>,
                     val turnsPassed: Int = 0,
                     val wasFullRound: Boolean = true) {
        constructor(input: List<String>, bonusElfPower: Int = 0) : this(
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
                                       attack = when (race) {
                                           ELF -> 3 + bonusElfPower
                                           GOBLIN -> 3
                                       },
                                       race = race)
                            }
                    }
                }
                .flatten()
                .filterNotNull()
                .updateReadingOrder()
        )
    }

    fun State.print() {
        println("Turn #$turnsPassed ${if (wasFullRound) "" else "(terminated early)"}")

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

    fun List<Entity>.updateReadingOrder() = also { entities ->
        entities
            .sortedWith(compareBy(pointComparator) { it.position })
            .mapIndexed { index, entity -> entity.readingOrder = index }
    }

    fun simulate(initial: State) = generateSequence(initial) { state ->
        with(state) {
            if (entities.map { it.race }.toSet().size == 1) return@generateSequence null

            val wasFullRound = entities
                .sortedBy { it.readingOrder }
                .all { entity -> entity.act(state) }

            State(map,
                  entities.filterNot { it.isDead }.updateReadingOrder(),
                  turnsPassed + 1,
                  wasFullRound)
        }
    }

    enum class Direction(val vector: Vector) {
        UP(Vector(0, -1)),
        DOWN(Vector(0, 1)),
        LEFT(Vector(-1, 0)),
        RIGHT(Vector(1, 0))
    }

    fun Entity.act(state: State): Boolean {
        if (isDead) return true

        val enemies = state
            .entities
            .filterNot { it.isDead }
            .filter { it.race != race } // damn, that's racist

        if (enemies.isEmpty()) return false

        val adjacentTiles = position.adjacentTiles().toSet()

        val enemyToAttack = enemies
            .filter { it.position in adjacentTiles }
            .sortedWith(compareBy({ it.hp }, { it.readingOrder }))
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
                .sortedWith(compareBy<Point> { floodFill.getValue(it) }.then(pointComparator))
                .firstOrNull() ?: return true

            val backtrackedPath = backtrack(moveDestination, floodFill)
            val step = backtrackedPath
                .filter { it in adjacentTiles }
                .sortedWith(pointComparator)
                .first()

            check(step !in blockedTiles)

            position = step

            // attack after move
            val enemyToAttackAfterMove = enemies
                .filter { it.position in position.adjacentTiles() }
                .sortedWith(compareBy({ it.hp }, { it.readingOrder }))
                .firstOrNull()

            if (enemyToAttackAfterMove != null) {
                enemyToAttackAfterMove.hp -= attack
            }
        }

        return true
    }

    fun backtrack(destination: Point, floodFill: Map<Point, Int>): List<Point> =
        destination
            .adjacentTiles()
            .filter { it in floodFill.keys }
            .filter { floodFill.getValue(it) < floodFill.getValue(destination) }
            .flatMap { backtrack(it, floodFill) + destination }

    fun Point.adjacentTiles() = Direction.values().map { direction -> this + direction.vector }

    fun floodFill(startingPosition: Point, blockedTiles: Set<Point>): Map<Point, Int> {
        val result = mutableMapOf<Point, Int>()

        val enqueuedPoints = mutableSetOf<Point>()
        val queue = PriorityQueue<Pair<Point, Int>>(compareBy { (_, distance) -> distance })

        fun enqueue(point: Point, distance: Int) {
            queue.add(point to distance)
            enqueuedPoints.add(point)
        }

        fun takeFirst() = queue
            .remove()
            .also { (point, _) -> enqueuedPoints.remove(point) }

        enqueue(startingPosition, 0)

        while (queue.isNotEmpty()) {
            val (point, distance) = takeFirst()

            result[point] = distance

            point
                .adjacentTiles()
                .filterNot { it in blockedTiles }
                .filterNot { it in result.keys }
                .filterNot { it in enqueuedPoints }
                .forEach { enqueue(it, distance + 1) }
        }

        return result
    }

    fun State.score(): Int {
        var fullRounds = turnsPassed
        if (!wasFullRound) fullRounds--

        val hitPointsLeft = entities.sumBy { it.hp }

        return fullRounds * hitPointsLeft
    }

    fun part1(input: List<String>) = simulate(State(input))
        .onEach { println("Round ${it.turnsPassed}") }
        .last()
        .run { println(score()) }

    fun part2(input: List<String>) {
        fun State.elvesCount() = entities.count { it.race == ELF }

        println("Trying to make all ${State(input).elvesCount()} survive")

        generateSequence(0) { it + 1 }
            .onEach { println("Trying bonus elf power $it") }
            .map { simulate(State(input, it)).onEach { print(".") }.last() }
            .onEach { println(": ${it.elvesCount()}") }
            .dropWhile { finalState -> finalState.elvesCount() < State(input).elvesCount() }
            .first()
            .run { println(score()) }
    }
}