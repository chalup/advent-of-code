package org.chalup.advent2019

import org.chalup.advent2019.Day6.MapObject.Body
import org.chalup.advent2019.Day6.MapObject.CenterOfMass
import org.chalup.utils.match
import java.util.LinkedList
import java.util.Queue

object Day6 {
    data class MapEntry(val parentBody: String, val childBody: String)

    sealed class MapObject {
        abstract val id: String
        abstract fun countOrbits(): Int

        fun orbitalPath(): List<MapObject> = generateSequence(this) {
            when (it) {
                CenterOfMass -> null
                is Body -> it.parent
            }
        }.drop(1).toList()

        object CenterOfMass : MapObject() {
            override val id: String = "COM"
            override fun countOrbits(): Int = 0
        }

        data class Body(override val id: String, val parent: MapObject) : MapObject() {
            override fun countOrbits(): Int = parent.countOrbits() + 1
        }
    }

    private fun parseInput(input: List<String>) = input.map {
        match<MapEntry>(it) {
            pattern("""(.*?)\)(.*?)""") { (parent, child) -> MapEntry(parent, child) }
        }
    }

    private fun buildMap(entries: List<MapEntry>): Map<String, MapObject> {
        val childrenIdByParentId = entries
            .groupBy { it.parentBody }
            .mapValues { (_, entries) -> entries.map { it.childBody } }

        val objects = mutableMapOf<String, MapObject>()

        processQueue<MapObject>(CenterOfMass) { queue, element ->
            objects[element.id] = element

            queue.addAll(
                childrenIdByParentId[element.id]
                    .orEmpty()
                    .map { Body(it, element) }
            )
        }

        return objects
    }

    fun task1(input: List<String>) = input
        .let(this::parseInput)
        .let(this::buildMap)
        .let { objects -> objects.values.sumBy { it.countOrbits() } }

    fun task2(input: List<String>) = input
        .let(this::parseInput)
        .let(this::buildMap)
        .let { objects ->
            val yourPath = objects.getValue("YOU").orbitalPath()
            val santaPath = objects.getValue("SAN").orbitalPath()

            val firstCommonBody = firstCommonBody(yourPath, santaPath)

            yourPath.indexOf(firstCommonBody) + santaPath.indexOf(firstCommonBody)
        }

    private fun firstCommonBody(pathA: List<MapObject>, pathB: List<MapObject>): MapObject =
        zip(pathA.walkBackwards(), pathB.walkBackwards()).last { (a, b) -> a == b }.first

    private fun <A, B> zip(a: Sequence<A>, b: Sequence<B>) = a.zip(b)
    private fun <T> List<T>.walkBackwards() = this.asReversed().asSequence()

    private fun <T> processQueue(initialElement: T, block: (queue: Queue<T>, element: T) -> Unit) {
        val queue = LinkedList<T>().apply { add(initialElement) }

        while (queue.isNotEmpty()) block(queue, queue.poll())
    }
}
