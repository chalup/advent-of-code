package org.chalup.advent2021

import org.chalup.advent2021.Day16.Packet.LiteralValue
import org.chalup.advent2021.Day16.Packet.Operator

object Day16 {
    fun task1(input: List<String>): Long = input
        .single()
        .let(this::parseHexToOnesAndZeros)
        .iterator()
        .let(this::parsePacket)
        .let(this::versionsSum)

    fun task2(input: List<String>): Long = input
        .single()
        .let(this::parseHexToOnesAndZeros)
        .iterator()
        .let(this::parsePacket)
        .let(this::eval)

    private fun parseHexToOnesAndZeros(input: String) = input
        .asSequence()
        .flatMap { char ->
            char
                .digitToInt(radix = 16)
                .toString(radix = 2)
                .padStart(length = 4, padChar = '0')
                .asSequence()
        }

    private class CountingIterator<T>(private val delegate: Iterator<T>) : Iterator<T> by delegate {
        var count: Int = 0
            private set

        override fun next(): T = delegate.next().also { count++ }
    }

    private fun parsePacket(iterator: Iterator<Char>): Packet {
        val version = iterator.readLong(3)
        val type = iterator.readLong(3)

        if (type == 4L) {
            val binaryString = buildString {
                do {
                    val hasMore = iterator.readLong(1) == 1L
                    repeat(4) { append(iterator.next()) }
                } while (hasMore)
            }

            return LiteralValue(version, type, value = binaryString.toLong(radix = 2))
        } else {
            val packets = buildList {
                when (val lengthTypeId = iterator.readLong(1)) {
                    0L -> {
                        val subPacketsLength = iterator.readLong(15)
                        val countingIterator = CountingIterator(iterator)

                        do {
                            add(parsePacket(countingIterator))
                        } while (countingIterator.count < subPacketsLength)
                    }
                    1L -> repeat(iterator.readLong(11).toInt()) { add(parsePacket(iterator)) }
                    else -> throw IllegalStateException("Unexpected length type id received: $lengthTypeId")
                }
            }

            return Operator(version, type, packets)
        }
    }

    private fun Iterator<Char>.readLong(bits: Int): Long {
        val binaryString = buildString(capacity = bits) {
            repeat(bits) { append(next()) }
        }

        return binaryString.toLong(radix = 2)
    }

    private fun versionsSum(packet: Packet): Long =
        when (packet) {
            is LiteralValue -> packet.version
            is Operator -> packet.version + packet.subPackets.sumOf { versionsSum(it) }
        }

    private fun eval(packet: Packet): Long =
        when (packet) {
            is LiteralValue -> packet.value
            is Operator -> {
                val subPacketsValues = packet.subPackets.asSequence().map(this::eval)

                when(packet.type) {
                    0L -> subPacketsValues.sum()
                    1L -> subPacketsValues.fold(1L, Long::times)
                    2L -> subPacketsValues.minOrNull()!!
                    3L -> subPacketsValues.maxOrNull()!!
                    5L -> {
                        val (one, other) = subPacketsValues.toList()
                        if (one > other) 1L else 0L
                    }
                    6L -> {
                        val (one, other) = subPacketsValues.toList()
                        if (one < other) 1L else 0L
                    }
                    7L -> {
                        val (one, other) = subPacketsValues.toList()
                        if (one == other) 1L else 0L
                    }
                    else -> throw IllegalArgumentException("Unexpected packet type ${packet.type}")
                }
            }
        }

    sealed class Packet {
        data class LiteralValue(
            val version: Long,
            val type: Long,
            val value: Long,
        ) : Packet()

        data class Operator(
            val version: Long,
            val type: Long,
            val subPackets: List<Packet>,
        ) : Packet()
    }
}
