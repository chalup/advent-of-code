package org.chalup.advent2015

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser

object Day12 {
    val isNotRed: (JsonObject) -> Boolean = { jsonObject ->
        jsonObject.entrySet().none { (_, value) -> value.isJsonPrimitive && value.asJsonPrimitive.asString == "red" }
    }

    private fun JsonElement.extractNumbers(objectFilter: (JsonObject) -> Boolean = { true }): Iterable<Int> =
        when {
            isJsonNull -> emptyList()
            isJsonPrimitive -> with(asJsonPrimitive) {
                if (isNumber) listOf(asNumber.toInt())
                else emptyList()
            }
            isJsonArray -> asJsonArray.flatMap { it.extractNumbers(objectFilter) }
            isJsonObject -> asJsonObject
                .takeIf(objectFilter)
                ?.entrySet()
                ?.flatMap { (_, element) -> element.extractNumbers(objectFilter) }
                ?: emptyList()
            else -> throw IllegalStateException("Don't know what to do with $this")
        }

    fun sumNumbersInJson(json: String, objectFilter: (JsonObject) -> Boolean = { true }): Int =
        JsonParser.parseString(json).extractNumbers(objectFilter).sum()
}