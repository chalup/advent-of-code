package org.chalup.advent2021

import java.util.LinkedList

object Day18 {
    fun task1(input: List<String>): Long = input
        .map(this::parseSnailfishNumber)
        .reduce { a, b -> a + b }
        .magnitude()

    fun task2(input: List<String>): Long = input
        .map(this::parseSnailfishNumber)
        .let { numbers ->
            numbers.flatMap { one ->
                numbers.mapNotNull { other ->
                    (one to other).takeUnless { one === other }
                }
            }
        }
        .maxOf { (one, other) -> (one + other).magnitude() }

    internal fun parseSnailfishNumber(input: String): Node {
        val numbers = LinkedList<Node>()

        for (c in input) {
            when (c) {
                '[' -> continue
                ',' -> continue
                ']' -> {
                    val right = numbers.pop()
                    val left = numbers.pop()
                    numbers.push(Node.Pair(left, right))
                }
                else -> numbers.push(Node.Number(c - '0')).also { check(c.isDigit()) }
            }
        }

        return numbers.single()
    }

    internal sealed class Node {
        data class Pair(val left: Node, val right: Node) : Node() {
            override fun toString() = "[$left,$right]"
        }

        data class Number(val value: Int) : Node() {
            override fun toString() = "$value"
        }
    }

    internal fun Node.magnitude(): Long = when (this) {
        is Node.Number -> value.toLong()
        is Node.Pair -> 3 * left.magnitude() + 2 * right.magnitude()
    }

    internal operator fun Node.plus(other: Node): Node = reduce(Node.Pair(this, other))

    private fun Node.addToRightmostNumber(value: Int): Node = when(this) {
        is Node.Number -> copy(value = this.value + value)
        is Node.Pair -> copy(right = right.addToRightmostNumber(value))
    }


    internal fun reduce(nodeToReduce: Node): Node {
        val nodes = LinkedList<Node>()

        var didExplode = false
        var pendingIncrement: Int? = null

        fun explode(node: Node, depth: Int = 0) {
            when (node) {
                is Node.Number -> {
                    nodes.push(
                        pendingIncrement
                            ?.let {
                                Node.Number(node.value + it)
                                    .also { pendingIncrement = null }
                            }
                            ?: node
                    )
                }
                is Node.Pair -> {
                    if (depth == 4 && !didExplode) {
                        didExplode = true

                        check(node.left is Node.Number)
                        check(node.right is Node.Number)

                        val leftNode = nodes.pollFirst()
                        if (leftNode != null) {
                            nodes.push(leftNode.addToRightmostNumber(node.left.value))
                        }

                        nodes.push(Node.Number(0))

                        pendingIncrement = node.right.value
                    } else {
                        explode(node.left, depth + 1)
                        explode(node.right, depth + 1)

                        nodes.push(Node.Pair(right = nodes.pop(), left = nodes.pop()))
                    }
                }
            }
        }

        explode(nodeToReduce)
        nodes
            .pop()
            .also { check(nodes.isEmpty()) }
            .takeIf { didExplode }
            ?.run { return reduce(this) }

        var didSplit = false
        fun split(node: Node) {
            if (didSplit) {
                nodes.push(node)
            } else {
                when (node) {
                    is Node.Number -> if (node.value > 9) {
                        didSplit = true
                        nodes.push(
                            Node.Pair(
                                left = Node.Number(node.value / 2),
                                right = Node.Number(node.value / 2 + node.value % 2)
                            )
                        )
                    } else {
                        nodes.push(node)
                    }
                    is Node.Pair -> {
                        split(node.left)
                        split(node.right)

                        nodes.push(Node.Pair(right = nodes.pop(), left = nodes.pop()))
                    }
                }
            }
        }

        split(nodeToReduce)
        nodes.pop()
            .also { check(nodes.isEmpty()) }
            .takeIf { didSplit }
            ?.run { return reduce(this) }

        return nodeToReduce
    }
}

