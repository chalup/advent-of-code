package org.chalup.advent2017

import java.util.LinkedList

object Day18 {
    fun task1(input: List<String>): Long {
        val registers = mutableMapOf<Char, Long>().withDefault { 0 }

        var pc = 0
        var lastSoundFrequency: Long? = null

        while (true) {
            val command = input[pc].split(" ")

            fun getValue(operand: String) = operand.toLongOrNull() ?: registers.getValue(operand.single())

            when (command[0]) {
                "snd" -> lastSoundFrequency = getValue(command[1])
                "set" -> {
                    val output = command[1].single()
                    registers[output] = getValue(command[2])
                }

                "add" -> {
                    val output = command[1].single()
                    registers[output] = registers.getValue(output) + getValue(command[2])
                }

                "mul" -> {
                    val output = command[1].single()
                    registers[output] = registers.getValue(output) * getValue(command[2])
                }

                "mod" -> {
                    val output = command[1].single()
                    registers[output] = registers.getValue(output) % getValue(command[2])
                }

                "rcv" -> if (getValue(command[1]) != 0L) return lastSoundFrequency!!
                "jgz" -> if (getValue(command[1]) > 0) {
                    pc += getValue(command[2]).toInt()
                    continue
                }
            }

            pc++
        }
    }

    fun task2(input: List<String>): Int {
        class Runner(
            val pid: Long,
            private val otherPid: Long,
            private val inboxesPerPid: Map<Long, LinkedList<Long>>,
        ) {
            var sendCounts = 0
            private var pc = 0
            private val registers = mutableMapOf('p' to pid).withDefault { 0 }

            fun runStep(): Boolean {
                val command = input[pc].split(" ")

                fun getValue(operand: String) = operand.toLongOrNull() ?: registers.getValue(operand.single())

                when (command[0]) {
                    "snd" -> {
                        sendCounts++
                        inboxesPerPid.getValue(otherPid).add(getValue(command[1]))
                    }
                    "set" -> {
                        val output = command[1].single()
                        registers[output] = getValue(command[2])
                    }

                    "add" -> {
                        val output = command[1].single()
                        registers[output] = registers.getValue(output) + getValue(command[2])
                    }

                    "mul" -> {
                        val output = command[1].single()
                        registers[output] = registers.getValue(output) * getValue(command[2])
                    }

                    "mod" -> {
                        val output = command[1].single()
                        registers[output] = registers.getValue(output) % getValue(command[2])
                    }

                    "rcv" -> {
                        val receivedValue = inboxesPerPid.getValue(pid).poll() ?: return false
                        val output = command[1].single()

                        registers[output] = receivedValue
                    }

                    "jgz" -> if (getValue(command[1]) > 0) {
                        pc += getValue(command[2]).toInt()
                        return true
                    }
                }

                pc++
                return true
            }
        }

        val runners = listOf(0L, 1L).let { pids ->
            val inboxes = pids.associateWith { LinkedList<Long>() }

            pids.zip(pids.asReversed()) { pid, otherPid ->
                Runner(pid, otherPid, inboxes)
            }
        }

        do {
            val madeProgress = runners.any { it.runStep() }
        } while (madeProgress)

        return runners.single { it.pid == 1L }.sendCounts
    }
}