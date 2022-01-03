package org.chalup.advent2016

import org.chalup.utils.match

object Day4 {
    fun task1(input: List<String>): Int = input
        .map(this::parseRoomInfo)
        .filter { it.checksum == calculateChecksum(it.roomName) }
        .sumOf { it.sectorId }

    fun task2(input: List<String>): Int = input
        .map(this::parseRoomInfo)
        .filter { it.checksum == calculateChecksum(it.roomName) }
        .single { roomInfo ->
            val shift = roomInfo.sectorId % LETTERS_COUNT

            val decryptedName = roomInfo
                .roomName
                .map { c ->
                    when (c) {
                        '-' -> ' '
                        else -> (c + shift).let { if (it > 'z') it - LETTERS_COUNT else it }
                    }
                }
                .joinToString(separator = "")

            decryptedName == "northpole object storage"
        }
        .sectorId

    private const val LETTERS_COUNT = ('z' - 'a' + 1)

    private fun calculateChecksum(roomName: String): String {
        val counts = mutableMapOf<Char, Int>()

        for (c in roomName) {
            if (!c.isLetter()) continue

            counts[c] = counts.getOrDefault(c, 0) + 1
        }

        return counts
            .entries
            .sortedWith(
                compareByDescending<MutableMap.MutableEntry<Char, Int>> { (_, count) -> count }
                    .thenBy { (letter, _) -> letter }
            )
            .asSequence()
            .take(5)
            .map { (letter, _) -> letter }
            .joinToString(separator = "")
    }

    private fun parseRoomInfo(input: String): RoomInfo = match(input) {
        //language=RegExp
        pattern("""([a-z\-]+)-(\d+)\[([a-z]+)]""") { (roomName, sectorId, checksum) ->
            RoomInfo(roomName, sectorId.toInt(), checksum)
        }
    }

    data class RoomInfo(
        val roomName: String,
        val sectorId: Int,
        val checksum: String,
    )
}