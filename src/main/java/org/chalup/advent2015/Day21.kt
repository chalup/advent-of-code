package org.chalup.advent2015

object Day21 {
    fun task1(input: List<String>): Int {
        val boss = boss(input)

        return possibleGear()
            .sortedBy { items -> items.sumOf { it.cost } }
            .first { items ->
                val player = Fighter(
                    hp = 100,
                    damage = items.sumOf { it.damageBonus },
                    armor = items.sumOf { it.armorBonus },
                )

                player winsWith boss
            }
            .sumOf { it.cost }
    }

    fun task2(input: List<String>): Int {
        val boss = boss(input)

        return possibleGear()
            .sortedByDescending { items -> items.sumOf { it.cost } }
            .map { items ->
                val player = Fighter(
                    hp = 100,
                    damage = items.sumOf { it.damageBonus },
                    armor = items.sumOf { it.armorBonus },
                )

                (player winsWith boss) to items.sumOf { it.cost }
            }
            .first { (isVictory, _) -> !isVictory }
            .let { (_, cost) -> cost }
    }

    private fun boss(input: List<String>) = input.map { line -> line.split(" ").last().toInt() }
        .let { (hp, damage, armor) ->
            Fighter(hp, damage, armor)
        }

    private fun possibleGear(): Set<List<Item>> {
        val weapons = listOf(
            Item(cost = 8, damageBonus = 4),
            Item(cost = 10, damageBonus = 5),
            Item(cost = 25, damageBonus = 6),
            Item(cost = 40, damageBonus = 7),
            Item(cost = 74, damageBonus = 8),
        )

        val armors = listOf(
            Item(cost = 13, armorBonus = 1),
            Item(cost = 31, armorBonus = 2),
            Item(cost = 53, armorBonus = 3),
            Item(cost = 75, armorBonus = 4),
            Item(cost = 102, armorBonus = 5),
        )

        val rings = listOf(
            Item(cost = 25, damageBonus = 1),
            Item(cost = 50, damageBonus = 2),
            Item(cost = 100, damageBonus = 3),
            Item(cost = 20, armorBonus = 1),
            Item(cost = 40, armorBonus = 2),
            Item(cost = 80, armorBonus = 3),
        )

        return buildSet<List<Item>> {
            (weapons + null).forEach { weapon ->
                (armors + null).forEach { armor ->
                    (rings + null).forEach { leftRing ->
                        (rings - leftRing + null).forEach { rightRing ->
                            add(listOfNotNull(weapon, armor, leftRing, rightRing))
                        }
                    }
                }
            }
        }
    }

    private data class Item(val cost: Int, val damageBonus: Int = 0, val armorBonus: Int = 0)

    private data class Fighter(val hp: Int, val damage: Int, val armor: Int)

    private infix fun Fighter.winsWith(other: Fighter): Boolean {
        var player = this
        var opponent = other

        val playerEffectiveDamage = (player.damage - opponent.armor).coerceAtLeast(1)
        val opponentEffectiveDamage = (opponent.damage - player.armor).coerceAtLeast(1)

        while (true) {
            opponent = opponent.copy(hp = opponent.hp - playerEffectiveDamage)
            if (opponent.hp <= 0) return true
            player = player.copy(hp = player.hp - opponentEffectiveDamage)
            if (player.hp <= 0) return false
        }
    }
}

