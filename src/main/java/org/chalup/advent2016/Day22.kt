package org.chalup.advent2016

import org.chalup.utils.Point

object Day22 {
    fun task1(input: List<String>): Int {
        val nodes = input.drop(2).map {
            """/dev/grid/node-x(\d+)-y(\d+)\s+(\d+)T\s+(\d+)T.*"""
                .toRegex()
                .matchEntire(it)!!
                .groupValues
                .drop(1)
                .map(String::trim)
                .map(String::toInt)
                .let { (x, y, size, used) -> Node(location = Point(x, y), size, used) }
        }

        return nodes.sumOf { n1 ->
            nodes.count { n2 ->
                n1.used > 0 && n1 != n2 && (n2.size - n2.used) >= n1.used
            }
        }
    }

    private data class Node(
        val location: Point,
        val size: Int,
        val used: Int
    )
}
