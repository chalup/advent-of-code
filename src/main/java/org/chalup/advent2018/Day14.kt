package org.chalup.advent2018

object Day14 {
    data class State(val recipes: MutableList<Int> = mutableListOf(3, 7),
                     val elvesPositions: Pair<Int, Int> = 0 to 1)

    fun generateRecipes(initialState: State = State()) = generateSequence(initialState) { state ->
        with(state) {
            val (dobbyPosition, kreacherPosition) = elvesPositions
            val dobbyRecipe = recipes[dobbyPosition]
            val kreacherRecipe = recipes[kreacherPosition]

            val recipesSum = dobbyRecipe + kreacherRecipe
            with(recipes) {
                if (recipesSum >= 10) add(1)
                add(recipesSum % 10)
            }

            val newDobbyPosition = (dobbyPosition + 1 + dobbyRecipe) % recipes.size
            val newKreacherPosition = (kreacherPosition + 1 + kreacherRecipe) % recipes.size

            state.copy(elvesPositions = newDobbyPosition to newKreacherPosition)
        }
    }

    fun Sequence<State>.getScoresAfterSomeTrials(trials: Int, scores: Int) = this
        .dropWhile { it.recipes.size < trials + scores }
        .first()
        .recipes
        .drop(trials)
        .take(scores)
}