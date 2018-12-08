package org.chalup.advent2018

object Day8 {
    private class Node(private val nodes: List<Node>,
                       private val metadata: List<Int>) {
        fun collectMetadata(): List<Int> = metadata + nodes.flatMap { it.collectMetadata() }

        fun value(): Int = when {
            nodes.isEmpty() -> metadata.sum()
            else -> metadata.sumBy { nodes.getOrNull(it - 1)?.value() ?: 0 }
        }
    }

    private data class NodeBuilder(val parentNodeBuilder: NodeBuilder? = null,
                                   val childrenToRead: Int? = null,
                                   val metadataToRead: Int? = null,
                                   val nodes: List<Node> = emptyList(),
                                   val metadata: List<Int> = emptyList()) {
        fun consume(data: Int): NodeBuilder =
            when {
                childrenToRead == null -> copy(childrenToRead = data)
                metadataToRead == null -> copy(metadataToRead = data)
                childrenToRead > 0 -> NodeBuilder(parentNodeBuilder = copy(childrenToRead = childrenToRead - 1)).consume(data)
                metadataToRead > 0 -> copy(metadataToRead = metadataToRead - 1,
                                           metadata = metadata + data)
                metadataToRead == 0 -> parentNodeBuilder!!.copy(nodes = parentNodeBuilder.nodes + build()).consume(data)
                else -> throw IllegalStateException("This should not happen! Current state: $this")
            }

        fun build(): Node {
            check(childrenToRead == 0)
            check(metadataToRead == 0)

            return Node(nodes, metadata)
        }
    }

    private fun buildRootNode(license: String): Node = license
        .split(" ")
        .map { it.trim().toInt() }
        .fold(NodeBuilder()) { builder, data -> builder.consume(data) }
        .run {
            check(parentNodeBuilder == null)
            build()
        }

    fun calculateRootNodeChecksum(license: String): Int = buildRootNode(license)
        .collectMetadata()
        .sum()

    fun calculateRootNodeValue(license: String): Int = buildRootNode(license).value()
}