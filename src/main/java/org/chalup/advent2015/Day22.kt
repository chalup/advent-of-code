package org.chalup.advent2015

import java.util.PriorityQueue

object Day22 {
    fun task1(input: List<String>): Int = runWizardSimulator(input, hardMode = false)
    fun task2(input: List<String>): Int = runWizardSimulator(input, hardMode = true)

    private fun runWizardSimulator(input: List<String>, hardMode: Boolean): Int {
        val (bossHp, bossDamage) = input.map { line -> line.split(" ").last().toInt() }

        val queue = PriorityQueue<State>(compareBy { it.manaSpent }).apply {
            add(State(bossHp, bossDamage, playersTurn = true))
        }

        while (queue.isNotEmpty()) {
            var state = queue.poll()

            if (state.playersTurn && hardMode) {
                state = state.copy(playerHp = state.playerHp - 1)

                if (state.playerHp <= 0) continue
            } else if (state.playerMana < Spell.entries.minOf { it.cost }) {
                // On each of your turns, you must select one of your spells to cast.
                // If you cannot afford to cast any spell, you lose.
                continue
            }

            state = state.applyEffects()

            if (state.bossHp <= 0) {
                // win
                return state.manaSpent
            }

            if (state.playersTurn) {
                queue += Spell.entries
                    .asSequence()
                    .mapNotNull(state::tryToCast)
            } else {
                state = state.applyBossAttack()
                if (state.playerHp >= 0) {
                    queue += state
                }
            }
        }

        throw IllegalStateException("Could not find the solution :(")
    }

    private data class State(
        val bossHp: Int,
        val bossDamage: Int,
        val playersTurn: Boolean,
        val playerHp: Int = 50,
        val playerMana: Int = 500,
        val effects: Map<Effect, Int> = emptyMap(),
        val manaSpent: Int = 0,
    ) {
        fun applyBossAttack(): State {
            if (bossHp <= 0) return this

            val effectiveDamage = (bossDamage - (if (Effect.SHIELD in effects) 7 else 0)).coerceAtLeast(1)

            return copy(
                playerHp = playerHp - effectiveDamage,
                playersTurn = true
            )
        }

        fun tryToCast(spell: Spell): State? {
            if (spell.cost > playerMana) return null
            when (spell) {
                Spell.SHIELD -> if (Effect.SHIELD in effects) return null
                Spell.POISON -> if (Effect.POISON in effects) return null
                Spell.RECHARGE -> if (Effect.RECHARGE in effects) return null
                else -> Unit
            }

            return when (spell) {
                Spell.MAGIC_MISSILE -> copy(bossHp = bossHp - 4)
                Spell.DRAIN -> copy(
                    bossHp = bossHp - 2,
                    playerHp = playerHp + 2
                )

                Spell.SHIELD -> copy(effects = effects + (Effect.SHIELD to 6))
                Spell.POISON -> copy(effects = effects + (Effect.POISON to 6))
                Spell.RECHARGE -> copy(effects = effects + (Effect.RECHARGE to 5))
            }.copy(
                playerMana = playerMana - spell.cost,
                manaSpent = manaSpent + spell.cost,
                playersTurn = false
            )
        }

        fun applyEffects(): State = effects.entries.fold(this) { state, (effect, turns) ->
            when (effect) {
                Effect.POISON -> state.copy(bossHp = state.bossHp - 3)
                Effect.SHIELD -> state
                Effect.RECHARGE -> state.copy(playerMana = state.playerMana + 101)
            }.copy(
                effects = if (turns == 1) state.effects - effect else state.effects + (effect to turns - 1)
            )
        }
    }

    private enum class Effect {
        POISON, SHIELD, RECHARGE
    }

    private enum class Spell(val cost: Int) {
        MAGIC_MISSILE(53),
        DRAIN(73),
        SHIELD(113),
        POISON(173),
        RECHARGE(229)
    }
}
