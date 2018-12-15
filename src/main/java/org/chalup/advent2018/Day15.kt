package org.chalup.advent2018

import org.chalup.utils.Point

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

    data class State(val map: List<String>,
                     val entities: List<Entity>,
                     val turnsPassed: Int = 0) {
        constructor(input: List<String>) : this(
            map = input.map { inputRow ->
                Race
                    .values()
                    .fold(inputRow) { row, race ->
                        row.replace(race.symbol, FLOOR)
                    }
            },
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

        map.forEachIndexed { y, row ->

            val rowEntities = mutableListOf<Entity>()

            row.forEachIndexed { x, tile ->
                val entity = entitiesByPosition[Point(x, y)]

                if (entity != null) {
                    rowEntities += entity
                    print(entity.race.symbol)
                } else {
                    print(tile)
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

    fun simulate(initial: State) = generateSequence(initial) { state ->
        with(state) {
            entities
                .sortedWith(compareBy({ it.position.y }, { it.position.x }))
                .forEach { entity ->
                    if (!entity.act(state)) return@generateSequence null
                }

            State(map,
                  entities.filterNot { it.isDead },
                  turnsPassed + 1)
        }
    }

    fun Entity.act(state: State): Boolean {
        // list enemies
        // return false if empty

        // if standing next to enemies
        //   sort the enemies around me in reading order
        //   attack the first one
        // otherwise
        //   transform list of enemies to list of adjacent tiles
        //   filter out wall tiles
        //   flood fill
        //   limit the target tiles to the ones in flood fill
        //   order target tiles by path length then by reading order
        //   backtrack to find the path
        //   choose the next step from the backtrack by ordering in reading order

        return true
    }
}