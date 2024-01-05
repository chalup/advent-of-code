package org.chalup.advent2020

import org.chalup.utils.textBlocks

object Day19 {
    fun task1(input: List<String>): Int {
        val (rulesSpecs, messages) = textBlocks(input)

        val rulesById = rulesSpecs.associate { spec ->
            val ruleId = spec.substringBefore(":").toInt()
            val ruleSpec = spec.substringAfter(": ")

            fun parseRuleListMatch(ruleSpec: String) = ruleSpec
                .trim()
                .split(" ")
                .map(String::toInt)
                .let(Rule::RuleListMatch)

            val rule = when {
                '"' in ruleSpec -> Rule.LetterMatch(ruleSpec.substringAfter('"').substringBeforeLast('"').single())
                '|' in ruleSpec -> ruleSpec
                    .split("|")
                    .map(::parseRuleListMatch)
                    .let(Rule::AlternativeRuleListsMatch)
                else -> parseRuleListMatch(ruleSpec)
            }

            ruleId to rule
        }

        fun match(text: String, index: Int, rule: Rule): Int? =
            when (rule) {
                is Rule.LetterMatch -> if (text[index] == rule.letter) index + 1 else null
                is Rule.RuleListMatch -> rule
                    .rulesIds
                    .fold<Int, Int?>(index) { idx, ruleId ->
                        idx?.let { match(text, it, rulesById.getValue(ruleId)) }
                    }
                is Rule.AlternativeRuleListsMatch -> rule
                    .alternatives
                    .firstNotNullOfOrNull { match(text, index, it) }
            }

        return messages.count { text -> match(text, index = 0, rulesById.getValue(0)) == text.length }
    }
}

private sealed class Rule {
    data class LetterMatch(val letter: Char) : Rule()
    data class RuleListMatch(val rulesIds: List<Int>) : Rule()
    data class AlternativeRuleListsMatch(val alternatives: List<RuleListMatch>) : Rule()
}