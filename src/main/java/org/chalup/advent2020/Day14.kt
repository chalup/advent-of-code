package org.chalup.advent2020

import org.chalup.utils.match

object Day14 {
    fun task1(input: List<String>) = input
        .map {
            match(it) {
                pattern("""mask = (.*?)""") { (mask) -> SeaPortInstruction.SetMask(mask) }
                pattern("""mem\[(\d+)\] = (\d+)""") { (address, value) -> SeaPortInstruction.SetMemory(address.toLong(), value.toLong()) }
            }
        }
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
}

private sealed interface SeaPortInstruction {
    data class SetMask(val mask: String) : SeaPortInstruction
    data class SetMemory(val address: Long, val value: Long) : SeaPortInstruction
}