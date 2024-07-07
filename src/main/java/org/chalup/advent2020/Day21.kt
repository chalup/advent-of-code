package org.chalup.advent2020

object Day21 {
    fun task1(input: List<String>): Int = parseFoodItems(input)
        .let { foodItems ->
            val unsafeIngredients = resolveAllergens(foodItems).values

            return foodItems.sumOf { it.ingredients.count { ingredient -> ingredient !in unsafeIngredients } }
        }

    fun task2(input: List<String>): String = parseFoodItems(input)
        .let { foodItems -> resolveAllergens(foodItems) }
        .entries
        .sortedBy { (allergen, _) -> allergen }
        .joinToString(separator = ",") { (_, ingredient) -> ingredient }

    private fun resolveAllergens(foodItems: List<FoodItem>): Map<String, String> {
        val allergens = mutableMapOf<String, String>()
        var allergenCandidates = foodItems
            .flatMapTo(mutableSetOf()) { it.knownAllergens }
            .associateWith { allergen ->
                foodItems
                    .filter { allergen in it.knownAllergens }
                    .map { it.ingredients }
                    .reduce { a, b -> a intersect b }
            }

        while (allergenCandidates.isNotEmpty()) {
            val (allergen, ingredient) = allergenCandidates.firstNotNullOf { (allergen, candidates) ->
                candidates.singleOrNull()?.let { allergen to it }
            }

            allergens[allergen] = ingredient
            allergenCandidates = allergenCandidates
                .filterKeys { it != allergen }
                .mapValues { (_, ingredients) -> ingredients - ingredient }
        }

        return allergens
    }

    private fun parseFoodItems(input: List<String>) = input.map { line ->
        FoodItem(
            ingredients = line.substringBefore("(").trim().split(" ").toSet(),
            knownAllergens = line
                .substringAfter("(contains ", "")
                .substringBefore(")")
                .split(", ")
                .toSet()
        )
    }

    private data class FoodItem(
        val ingredients: Set<String>,
        val knownAllergens: Set<String>
    )
}
