package org.chalup.advent2015


object Day15 {
    fun task1(input: List<String>): Long = bestMixScore(input)
    fun task2(input: List<String>): Long = bestMixScore(input, caloriesConstraint = { it == 500L })

    private fun bestMixScore(input: List<String>, caloriesConstraint: (Long) -> Boolean = { true }): Long {
        val ingredients = input.map { line ->
            val name = line.substringBefore(":")
            val properties = line.substringAfter(":")
                .split(",")
                .associate { property ->
                    val (propertyName, effect) = property
                        .trim()
                        .split(" ")

                    propertyName to effect.toLong()
                }


            Ingredient(name, properties)
        }

        fun possibleMixes(teaspoons: Int, ingredients: List<Ingredient>): List<Map<Ingredient, Int>> {
            if (teaspoons == 0 || ingredients.isEmpty()) return listOf(mapOf())

            return ((if (ingredients.size == 1) teaspoons else 0)..teaspoons).flatMap { amount ->
                possibleMixes(teaspoons - amount, ingredients.drop(1))
                    .map { it + (ingredients.first() to amount) }
            }
        }

        return possibleMixes(100, ingredients)
            .maxOf { mix ->
                mix
                    .flatMap { (ingredient, amount) ->
                        ingredient.properties.map { (property, change) -> property to change * amount }
                    }
                    .groupBy { (property, _) -> property }
                    .mapValues { (_, changes) -> changes.sumOf { (_, change) -> change }.coerceAtLeast(0) }
                    .takeIf { caloriesConstraint(it.getValue("calories")) }
                    ?.filterKeys { it != "calories" }
                    ?.values
                    ?.fold(1L, Long::times)
                    ?: 0L
            }
    }

    data class Ingredient(
        val name: String,
        val properties: Map<String, Long>
    )
}
