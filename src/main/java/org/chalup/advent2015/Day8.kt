package org.chalup.advent2015

object Day8 {
    fun unescape(escaped: String) = escaped
        .drop(1).dropLast(1)
        .replace("""\"""", "\"")
        .replace("""\\""", "\\")
        .let {
            var text = it
            while (true) {
                val match = """\\x([0-9a-f]{2})""".toRegex().find(text) ?: break
                val (hex) = match.destructured
                text = text.replaceRange(match.range, hex.toInt(16).toChar().toString())
            }
            text
        }

    fun escape(input: String) = input
        .replace("\\", "\\\\")
        .replace("\"", "\\\"")
        .let { "\"$it\"" }
}