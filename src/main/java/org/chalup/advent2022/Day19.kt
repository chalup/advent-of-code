package org.chalup.advent2022

import org.chalup.advent2022.ResourceType.CLAY
import org.chalup.advent2022.ResourceType.GEODE
import org.chalup.advent2022.ResourceType.OBSIDIAN
import org.chalup.advent2022.ResourceType.ORE
import org.chalup.utils.parseNumbers
import java.util.LinkedList
import java.util.concurrent.TimeUnit

object Day19 {
    fun task1(input: List<String>) = input
        .let(::parseBlueprints)
        .associateWith { calculateMaxGeodeYield(it, timeLimit = 24) }
        .map { (blueprint, geodeYield) -> blueprint.id * geodeYield }
        .sum()

    fun task2(input: List<String>) = input
        .let(::parseBlueprints)
        .take(3)
        .map { calculateMaxGeodeYield(it, timeLimit = 32) }
        .reduce(Int::times)

    private fun parseBlueprints(input: List<String>) = input
        .map(::parseNumbers)
        .map {
            Blueprint(
                id = it[0],
                robotCosts = mapOf(
                    ORE to mapOf(ORE to it[1]),
                    CLAY to mapOf(ORE to it[2]),
                    OBSIDIAN to mapOf(ORE to it[3], CLAY to it[4]),
                    GEODE to mapOf(ORE to it[5], OBSIDIAN to it[6]),
                )
            )
        }

    private fun calculateMaxGeodeYield(blueprint: Blueprint, timeLimit: Int): Int {
        println("Maximizing blueprint ${blueprint.id}")

        val resourceTypes = ResourceType.values()
        val maxCost = IntArray(3) {
            blueprint.robotCosts.maxOf { (_, costs) -> costs.getOrDefault(resourceTypes[it], 0) }
        }

        class State(
            val time: Int = 0,
            val resources: IntArray = IntArray(4) { 0 },
            val robots: IntArray = IntArray(4) { if (it == 0) 1 else 0 },
        ) {
            operator fun get(resourceType: ResourceType) = resources[resourceType.ordinal]

            fun tick() = State(
                time = time + 1,
                robots = robots,
                resources = resources.copyOf().also {
                    for (r in resourceTypes) {
                        it[r.ordinal] = resources[r.ordinal] + robots[r.ordinal]
                    }
                }
            )

            fun canBuildRobotFor(resourceType: ResourceType) =
                blueprint.robotCosts.getValue(resourceType).all { (type, cost) ->
                    cost <= get(type)
                }

            fun withRobotFor(resourceType: ResourceType) = State(
                time = time,
                robots = robots.copyOf().also {
                    it[resourceType.ordinal] = it[resourceType.ordinal] + 1
                },
                resources = resources.copyOf().also {
                    blueprint.robotCosts.getValue(resourceType).forEach { (type, cost) ->
                        it[type.ordinal] = it[type.ordinal] - cost
                    }
                }
            )

            override fun toString(): String = run {
                fun IntArray.dump() = resourceTypes
                    .map { it to this[it.ordinal] }
                    .filter { (_, count) -> count > 0 }
                    .joinToString(prefix = "[", postfix = "]") { (r, count) -> "$count${r.symbol}" }

                "State(t=$time, resources=${resources.dump()}, robot=${robots.dump()})"
            }
        }

        var bestYield: State? = null
        val queue = LinkedList<State>().apply { add(State()) }

        var processedNodes = 0
        var fastReject = 0
        var time = System.nanoTime()
        while (queue.isNotEmpty()) {
            if (++processedNodes % 5_000_000 == 0) {
                val now = System.nanoTime()
                val elapsed = now - time
                time = now

                val nps = 5_000_000L * 1000 / TimeUnit.NANOSECONDS.toMillis(elapsed)

                println("#${blueprint.id}: $processedNodes ($fastReject rejects, $nps nps) queue size = ${queue.size}, best yield so far=$bestYield, sample state I'm considering: ${queue.first()}")
            }
            val state = queue.remove()

            val nextStateBase = state.tick()
            if (nextStateBase.time == timeLimit) {
                if ((nextStateBase[GEODE]) > (bestYield?.get(GEODE) ?: 0)) {
                    bestYield = nextStateBase
                }
            } else {
                val ticksLeft = timeLimit - nextStateBase.time
                val potentialObsidianYield = (1..ticksLeft).sum() + // future production
                        (ticksLeft * nextStateBase.robots[OBSIDIAN.ordinal]) + // current production
                        nextStateBase[OBSIDIAN] // current yield

                val potentialGeodeRobotFactories = potentialObsidianYield / blueprint.robotCosts.getValue(GEODE).getValue(OBSIDIAN)
                val potentialGeodeYield = (1..ticksLeft).reversed().take(potentialGeodeRobotFactories).sum() + // future production
                        (ticksLeft * nextStateBase.robots[GEODE.ordinal]) + // current production
                        nextStateBase[GEODE] // current yield

                if (potentialGeodeYield < (bestYield?.get(GEODE) ?: 1)) {
                    fastReject++
                    continue
                }

                if (state.canBuildRobotFor(GEODE)) {
                    queue.addFirst(nextStateBase.withRobotFor(GEODE))
                } else {
                    queue.addFirst(nextStateBase) // assume we decided we don't want to build anything

                    if (state.canBuildRobotFor(ORE) && state.robots[ORE.ordinal] < maxCost[ORE.ordinal]) {
                        // consider building ore robots only if we cannot build any robot each turn
                        queue.addFirst(nextStateBase.withRobotFor(ORE))
                    }

                    if (state.canBuildRobotFor(CLAY) && state.robots[CLAY.ordinal] < maxCost[CLAY.ordinal]) {
                        queue.addFirst(nextStateBase.withRobotFor(CLAY))
                    }

                    if (state.canBuildRobotFor(OBSIDIAN) && state.robots[OBSIDIAN.ordinal] < maxCost[OBSIDIAN.ordinal]) {
                        queue.addFirst(nextStateBase.withRobotFor(OBSIDIAN))
                    }
                }
            }
        }

        println("Blueprint $blueprint (processed: $processedNodes): $bestYield")

        return bestYield?.get(GEODE) ?: 0
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val startAt = System.nanoTime()
        task1(
            """
            Blueprint 1:  Each ore robot costs 4 ore.  Each clay robot costs 2 ore.  Each obsidian robot costs 3 ore and 14 clay.  Each geode robot costs 2 ore and 7 obsidian.
            Blueprint 2:  Each ore robot costs 2 ore.  Each clay robot costs 3 ore.  Each obsidian robot costs 3 ore and 8 clay.  Each geode robot costs 3 ore and 12 obsidian.
            """.trimIndent().lines()
        ).also(::println)

        val endAt = System.nanoTime()

        println("Took ${TimeUnit.NANOSECONDS.toMillis(endAt - startAt)}ms")
    }
}

enum class ResourceType(val symbol: Char) {
    ORE('I'), CLAY('C'), OBSIDIAN('O'), GEODE('G')
}

data class Blueprint(
    val id: Int,
    val robotCosts: Map<ResourceType, Cost>
)

typealias Cost = Map<ResourceType, Int>
