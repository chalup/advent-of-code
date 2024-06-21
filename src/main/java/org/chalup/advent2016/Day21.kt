package org.chalup.advent2016

object Day21 {
    fun task1(input: List<String>): String = input.fold("abcdefgh") { text, instruction ->
        val i = instruction.split(" ")

        when ("${i[0]} ${i[1]}") {
            "swap position" -> text.swapPositions(a = i[2].toInt(), b = i[5].toInt())
            "swap letter" -> text.swapLetters(a = i[2].single(), b = i[5].single())

            "rotate based" -> {
                val index = text.indexOf(i[6].single())
                val rotation = (1 + index + (if (index >= 4) 1 else 0)) % text.length

                text.rotateRight(rotation)
            }

            "rotate right" -> text.rotateRight(i[2].toInt() % text.length)
            "rotate left" -> text.rotateLeft(i[2].toInt() % text.length)
            "reverse positions" -> text.reversePositions(from = i[2].toInt(), to = i[4].toInt())
            "move position" -> text.moveLetter(from = i[2].toInt(), to = i[5].toInt())

            else -> throw IllegalArgumentException(instruction)
        }
    }

    private fun String.rotateRight(amount: Int) =
        takeLast(amount) + dropLast(amount)

    private fun String.rotateLeft(amount: Int) =
        drop(amount) + take(amount)

    private fun String.swapPositions(a: Int, b: Int): String {
        val text = this
        return buildString {
            append(text)
            set(a, text[b])
            set(b, text[a])
        }
    }

    private fun String.swapLetters(a: Char, b: Char): String {
        val text = this

        val indicesOfAs = text.indices.filter { text[it] == a }
        val indicesOfBs = text.indices.filter { text[it] == b }

        return buildString {
            append(text)
            indicesOfAs.forEach { set(it, b) }
            indicesOfBs.forEach { set(it, a) }
        }
    }

    private fun String.reversePositions(from: Int, to: Int) = take(from) + substring(from, to + 1).reversed() + drop(to + 1)
    private fun String.moveLetter(from: Int, to: Int) = toMutableList()
        .apply { add(to, removeAt(from)) }
        .joinToString(separator = "")
}
