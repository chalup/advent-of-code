package org.chalup.advent2023

object Day25 {
    fun task1(input: List<String>): Int {
        val wires: Set<Pair<String, String>> = input
            .flatMapTo(mutableSetOf()) { line ->
                val a = line.substringBefore(':').trim()

                line.substringAfter(":")
                    .trim()
                    .split(" ")
                    .map { b ->
                        minOf(a, b) to maxOf(a, b)
                    }
            }

        while (true) {
            var edgeWeights = wires.associateWith { 1 }
            val nodeWeights = mutableMapOf<String, Int>().apply {
                for ((from, to) in wires) {
                    put(from, 1)
                    put(to, 1)
                }
            }

            while (edgeWeights.size > 1) {
                val edge = edgeWeights.keys.random()
                val (from, to) = edge

                val mergedNode = from + to

                nodeWeights[mergedNode] = nodeWeights.remove(from)!! + nodeWeights.remove(to)!!

                edgeWeights = edgeWeights.entries
                    .filter { (e, _) -> e != edge }
                    .map { (e, w) ->
                        val (a, b) = e

                        val newA = a.takeUnless { it == from || it == to } ?: mergedNode
                        val newB = b.takeUnless { it == from || it == to } ?: mergedNode

                        (minOf(newA, newB) to maxOf(newA, newB)) to w
                    }
                    .groupingBy { (e, _) -> e }
                    .fold(0) { acc, (_, w) -> acc + w }
            }


            val (edge, weight) = edgeWeights.entries.single()
            if (weight == 3) {
                val (from, to) = edge
                return nodeWeights.getValue(from) * nodeWeights.getValue(to)
            }
        }
    }
}
