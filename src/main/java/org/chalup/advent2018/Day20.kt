package org.chalup.advent2018

import java.util.LinkedList

object Day20 {
    class Node(var path: String = "",
               val children: MutableList<Node> = mutableListOf()) {
        val allPaths: List<LinkedList<Node>> by lazy {
            children
                .flatMap { it.allPaths }
                .ifEmpty { listOf(LinkedList()) }
                .map { path ->
                    if (this.path == "") {
                        path
                    } else {
                        LinkedList(path).apply { addFirst(this@Node) }
                    }
                }
        }
    }

    fun parseMap(input: String): Node {
        val rootNode = Node()
        lateinit var currentNode: Node
        val stack = LinkedList<Node>()
        val groupEnds = LinkedList<MutableList<Node>>()

        input.forEach { token ->
            when (token) {
                '^' -> currentNode = rootNode
                '$' -> Unit
                '(' -> {
                    val parentNode = currentNode
                    currentNode = Node()
                    parentNode.children += currentNode

                    stack.push(parentNode)
                    groupEnds.push(mutableListOf())
                }
                '|' -> {
                    groupEnds.peek() += currentNode
                    currentNode = Node()
                    stack.peek().children += currentNode
                }
                ')' -> {
                    val parents = groupEnds.pop() + currentNode
                    stack.pop()
                    currentNode = Node()
                    parents.forEach { it.children += currentNode }
                }
                'N', 'E', 'S', 'W' -> currentNode.path += token
            }
        }
        return rootNode
    }

    fun shortestPathLengthToMostDistantRoom(paths: List<LinkedList<Node>>) =
        paths
            .groupBy { it.last }
            .mapValues { (_, alternativePath) ->
                alternativePath.minBy { it.sumBy { it.path.length } }!!
            }
            .values
            .map { it.sumBy { it.path.length } }
            .max()!!

    fun part1(input: String) =
        parseMap(input)
            .allPaths
            .let { shortestPathLengthToMostDistantRoom(it) }
}
