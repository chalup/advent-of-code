package org.chalup.advent2018

import org.chalup.advent2018.Day4.Event.FallAsleep
import org.chalup.advent2018.Day4.Event.WakeUp
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object Day4 {
    private val dateRegex = """^\[(.*?)] (.*?)$""".toRegex()
    private val guardNoRegex = """Guard #(\d+) begins shift""".toRegex()
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

    private sealed class Event {
        data class GuardChange(val guardId: Int) : Event()
        object FallAsleep : Event()
        object WakeUp : Event()
    }

    private data class TimestampedEvent(val datetime: LocalDateTime,
                                        val event: Event)

    private fun parse(logLine: String): TimestampedEvent {
        val (date, eventLog) = dateRegex.matchEntire(logLine)!!.destructured

        val event = when (eventLog) {
            "falls asleep" -> FallAsleep
            "wakes up" -> WakeUp
            else -> Event.GuardChange(guardNoRegex.matchEntire(eventLog)!!.groupValues[1].toInt())
        }

        return TimestampedEvent(datetime = LocalDateTime.parse(date, dateFormatter),
                                event = event)
    }

    private data class SleepEntry(val guardId: Int,
                                  val minute: Int)

    private data class GuardState(val activeGuard: Int? = null,
                                  val sleepsSince: LocalDateTime? = null,
                                  val sleepLog: List<SleepEntry> = emptyList()) {
        private fun addSleepLogEntries(till: LocalDateTime): GuardState {
            if (activeGuard == null || sleepsSince == null) return this

            val newEntries = mutableListOf<SleepEntry>().apply {
                var current: LocalDateTime = sleepsSince
                while (current != till) {
                    add(SleepEntry(activeGuard, current.minute))
                    current = current.plusMinutes(1)
                }
            }
            return copy(sleepLog = sleepLog + newEntries)
        }

        fun changeGuard(datetime: LocalDateTime, guardId: Int) =
            addSleepLogEntries(datetime).copy(activeGuard = guardId, sleepsSince = null)

        fun wakeUp(datetime: LocalDateTime) = addSleepLogEntries(datetime).copy(sleepsSince = null)

        fun fallAsleep(datetime: LocalDateTime) = copy(sleepsSince = datetime)
    }

    private fun getSleepLog(input: List<String>) = input
        .map { parse(it) }
        .sortedBy { it.datetime }
        .fold(GuardState()) { state, (datetime, event) ->

            when (event) {
                is Event.GuardChange -> state.changeGuard(datetime, event.guardId)
                FallAsleep -> state.fallAsleep(datetime)
                WakeUp -> state.wakeUp(datetime)
            }
        }
        .let { it.sleepLog }

    private fun <T, R> List<T>.maxCountBy(selector: (T) -> R) = this
        .groupingBy(selector)
        .eachCount()
        .maxBy { it.value }!!
        .key

    fun strategy1(input: List<String>) = getSleepLog(input)
        .let { sleepLog ->
            val sleepyHead = sleepLog.maxCountBy { it.guardId }

            val sleepyMinute = sleepLog
                .filter { it.guardId == sleepyHead }
                .maxCountBy { it.minute }

            sleepyHead * sleepyMinute
        }

    fun strategy2(input: List<String>) = getSleepLog(input)
        .maxCountBy { it }
        .let { (guardId, minute) -> guardId * minute }
}