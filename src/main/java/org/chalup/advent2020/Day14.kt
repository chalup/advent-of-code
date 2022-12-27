package org.chalup.advent2020

import org.chalup.utils.match

object Day14 {
    fun task1(input: List<String>) = input
        .map(::parseSeaPortInstruction)
        .let { seaPortInstructions ->
            data class ValueMask(
                val unchangedBitsMask: Long = 0x0000_FFFF_FFFF_FFFF,
                val onesMask: Long = 0x0000_0000_0000_0000,
            ) {
                constructor(mask: String) : this(
                    unchangedBitsMask = mask.map { if (it == 'X') 1 else 0 }.joinToString(separator = "").toLong(radix = 2),
                    onesMask = mask.map { if (it == '1') 1 else 0 }.joinToString(separator = "").toLong(radix = 2),
                )
            }

            var currentMask = ValueMask()
            val memory = mutableMapOf<Long, Long>()

            seaPortInstructions.forEach {
                when (it) {
                    is SeaPortInstruction.SetMask -> currentMask = ValueMask(it.mask)
                    is SeaPortInstruction.SetMemory -> memory[it.address] = run {
                        (it.value and currentMask.unchangedBitsMask) or currentMask.onesMask
                    }
                }
            }

            memory.values.sum()
        }

    fun task2(input: List<String>) = input
        .map(::parseSeaPortInstruction)
        .let { seaPortInstructions ->
            data class AddressMask(
                val unchangedBitsMask: Long = 0x0000_FFFF_FFFF_FFFF,
                val onesMask: Long = 0x0000_0000_0000_0000,
                val floatingBits: List<Int> = emptyList()
            ) {
                constructor(mask: String) : this(
                    unchangedBitsMask = mask.map { if (it == '0') 1 else 0 }.joinToString(separator = "").toLong(radix = 2),
                    onesMask = mask.map { if (it == '1') 1 else 0 }.joinToString(separator = "").toLong(radix = 2),
                    floatingBits = mask.reversed().mapIndexedNotNull { index, c -> index.takeIf { c == 'X' } }
                )

                fun floatingAddresses(bits: List<Int>): Sequence<Long> = sequence {
                    if (bits.isEmpty()) {
                        yield(0L)
                    } else {
                        val firstBitMask = 1L shl bits.first()

                        floatingAddresses(bits.drop(1)).forEach { address ->
                            yield(address)
                            yield(address or firstBitMask)
                        }
                    }
                }

                fun addresses(baseAddress: Long) = floatingAddresses(floatingBits).map { floatingMask ->
                    (baseAddress and unchangedBitsMask) or onesMask or floatingMask
                }
            }

            var currentMask = AddressMask()
            val memory = mutableMapOf<Long, Long>()

            seaPortInstructions.forEach {
                when (it) {
                    is SeaPortInstruction.SetMask -> currentMask = AddressMask(it.mask)
                    is SeaPortInstruction.SetMemory -> {
                        currentMask.addresses(it.address).forEach { address ->
                            memory[address] = it.value
                        }
                    }
                }
            }

            memory.values.sum()
        }
}

private fun parseSeaPortInstruction(input: String) = match(input) {
    pattern("""mask = (.*?)""") { (mask) -> SeaPortInstruction.SetMask(mask) }
    pattern("""mem\[(\d+)\] = (\d+)""") { (address, value) -> SeaPortInstruction.SetMemory(address.toLong(), value.toLong()) }
}

private sealed interface SeaPortInstruction {
    data class SetMask(val mask: String) : SeaPortInstruction
    data class SetMemory(val address: Long, val value: Long) : SeaPortInstruction
}