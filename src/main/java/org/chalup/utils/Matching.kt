package org.chalup.utils

import kotlin.text.MatchResult.Destructured

fun Regex.matchOrThrow(text: String): MatchResult = matchEntire(text) ?: throw IllegalArgumentException("Can't parse [$text] with [$this]'")

inline fun <T : Any> match(input: String, block: Matcher<T>.() -> Unit): T = Matcher<T>(input).apply(block).result()

class Matcher<T : Any>(private val input: String) {
    private var result: T? = null

    fun result() = result ?: throw IllegalArgumentException("Failed to parse '$input'")

    fun pattern(pattern: String, block: (Destructured) -> T) {
        result = result ?: pattern.toRegex().matchEntire(input)?.destructured?.let(block)
    }

    fun parser(block: (String) -> T) {
        result = result ?: block(input)
    }
}