package org.chalup.advent2017

object Day24 {
    fun task1(input: List<String>): Int = findBestBridge(input, compareBy { it.strength }).strength
    fun task2(input: List<String>): Int = findBestBridge(input, compareBy<Bridge> { it.length }.thenBy { it.strength }).strength

    private fun findBestBridge(input: List<String>, comparator: Comparator<Bridge>): Bridge {
        val allComponents: Set<Component> = input
            .mapTo(mutableSetOf()) { it.split("/").map(String::toInt).let { (a, b) -> Component(a, b) } }

        val cache = mutableMapOf<Pair<Int, Set<Component>>, Bridge>()
        fun strongestBridge(startingWith: Int, availableComponents: Set<Component>): Bridge = cache.getOrPut(startingWith to availableComponents) {
            availableComponents
                .map { component ->
                    val (a, b) = component
                    when {
                        a == startingWith -> strongestBridge(b, availableComponents - component).let { it.copy(strength = it.strength + a + b) }
                        b == startingWith -> strongestBridge(a, availableComponents - component).let { it.copy(strength = it.strength + a + b) }
                        else -> Bridge(strength = 0, length = allComponents.size - availableComponents.size)
                    }
                }
                .maxWithOrNull(comparator)
                ?: Bridge(strength = 0, length = allComponents.size - availableComponents.size)
        }

        return strongestBridge(startingWith = 0, allComponents)
    }
}

private data class Component(val a: Int, val b: Int)
private data class Bridge(val length: Int, val strength: Int)