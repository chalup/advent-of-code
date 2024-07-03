package org.chalup.advent2018

import org.chalup.utils.textBlocks

object Day24 {
    fun task1(input: List<String>): Int = simulateInfection(parseArmies(input))!!.units

    fun task2(input: List<String>): Int {
        fun Map<GroupId, UnitGroup>.withBoost(boost: Int, armyName: String) = mapValues { (_, group) ->
            if (group.id.army == armyName) group.copy(damage = group.damage + boost)
            else group
        }

        val armies = parseArmies(input)

        return generateSequence(1) { it + 1 }
            .map { simulateInfection(armies.withBoost(it, "Immune System")) }
            .filterNotNull()
            .first { it.army == "Immune System" }
            .units
    }

    private fun simulateInfection(groups: Map<GroupId, UnitGroup>): InfectionResult? {
        val allGroups = groups.toMutableMap()

        do {
            val previousStatus = allGroups.toMap()

            val targets = allGroups.values.toMutableSet()
            val plannedAttacks = mutableMapOf<GroupId, GroupId>()

            allGroups
                .values
                .sortedWith(
                    compareByDescending(UnitGroup::effectivePower)
                        .thenByDescending(UnitGroup::initiative)
                )
                .forEach { attacker ->
                    val target = targets
                        .filter { it.id.army != attacker.id.army }
                        .filter { attacker.damageType !in it.immunities }
                        .sortedWith(
                            compareByDescending(attacker::damageAgainst)
                                .thenByDescending(UnitGroup::effectivePower)
                                .thenByDescending(UnitGroup::initiative)
                        )
                        .firstOrNull()

                    if (target != null) {
                        targets -= target
                        plannedAttacks[attacker.id] = target.id
                    }
                }

            val attackersIds = allGroups.entries.sortedByDescending { (_, group) -> group.initiative }.map { (id, _) -> id }
            for (id in attackersIds) {
                val attacker = allGroups[id] ?: continue
                val defenderId = plannedAttacks[id] ?: continue
                val defender = allGroups[defenderId] ?: continue

                when (val casualties = (attacker.damageAgainst(defender) / defender.hp).coerceAtMost(defender.units)) {
                    0 -> Unit
                    defender.units -> allGroups.remove(defenderId)
                    else -> allGroups[defenderId] = defender.copy(units = defender.units - casualties)
                }
            }
        } while (allGroups != previousStatus && allGroups.values.groupBy { it.id.army }.size == 2)

        val winningArmy = allGroups.keys.mapTo(mutableSetOf()) { it.army }.singleOrNull() ?: return null

        val unitsLeft = allGroups.values.sumOf { it.units }
        return InfectionResult(winningArmy, unitsLeft)
    }

    private data class InfectionResult(val army: String, val units: Int)

    private fun parseArmies(input: List<String>) = textBlocks(input)
        .flatMap { block ->
            val armyName = block.first().trim(':')

            block.drop(1).mapIndexed { index, text -> parseGroup(GroupId(armyName, index + 1), text) }
        }
        .associateBy { it.id }

    private fun parseGroup(id: GroupId, text: String): UnitGroup {
        val matchedGroups = """^(?<count>\d+) units each with (?<hp>\d+) hit points (?:\((?<weaknessesAndImmunities>.*?)\) )?with an attack that does (?<damage>\d+) (?<attack>.*?) damage at initiative (?<initiative>\d+)${'$'}""".toRegex().matchEntire(text)!!.groups

        val (weaknesses, immunities) = matchedGroups["weaknessesAndImmunities"]
            ?.value
            ?.split("; ")
            ?.associate { specs ->
                val elements: List<String> = specs.split(" ")
                val modifier: String = elements.first()
                val types: List<String> = elements.drop(2).map { it.trim(',') }

                modifier to types
            }
            .orEmpty()
            .let { (it["weak"].orEmpty()) to (it["immune"].orEmpty()) }

        return UnitGroup(
            id = id,
            units = matchedGroups["count"]!!.value.toInt(),
            hp = matchedGroups["hp"]!!.value.toInt(),
            damage = matchedGroups["damage"]!!.value.toInt(),
            damageType = matchedGroups["attack"]!!.value,
            initiative = matchedGroups["initiative"]!!.value.toInt(),
            weaknesses = weaknesses,
            immunities = immunities
        )
    }

    private data class UnitGroup(
        val id: GroupId,
        val units: Int,
        val hp: Int,
        val damage: Int,
        val damageType: String,
        val initiative: Int,
        val weaknesses: List<String>,
        val immunities: List<String>
    ) {
        val effectivePower = units * damage

        fun damageAgainst(other: UnitGroup) = effectivePower * when (damageType) {
            in other.immunities -> 0
            in other.weaknesses -> 2
            else -> 1
        }
    }

    private data class GroupId(
        val army: String,
        val index: Int,
    )
}