package org.chalup.advent2020

object Day13 {
    fun task1(input: List<String>): Long = input
        .let { (time, buses) ->
            BusInfo(
                estimatedTimeOfArrival = time.toLong(),
                busNumbers = buses.split(',').mapNotNull { it.toLongOrNull() }
            )
        }
        .let { (time, buses) ->
            buses
                .asSequence()
                .map { busNumber ->
                    busNumber to ((busNumber - (time % busNumber)) % busNumber)
                }
                .minByOrNull { (_, waitTime) -> waitTime }!!
                .let { (busNumber, waitTime) -> busNumber * waitTime }
        }
}

private data class BusInfo(
    val estimatedTimeOfArrival: Long,
    val busNumbers: List<Long>
)