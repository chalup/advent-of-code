package org.chalup.advent2022

object Day7 {
    fun task1(input: List<String>): Long {
        val root = parseFilesystem(input)
        val dirSizes = calculateDiskUsage(root)

        return dirSizes.values
            .filter { it <= 100_000 }
            .sum()
    }

    fun task2(input: List<String>): Long {
        val root = parseFilesystem(input)
        val dirSizes = calculateDiskUsage(root)

        val freeSpace = 70_000_000 - dirSizes.getValue(root)
        val spaceNeeded = 30_000_000 - freeSpace

        return dirSizes.values
            .filter { it >= spaceNeeded }
            .min()
    }

    private fun calculateDiskUsage(root: FsNode.Directory): Map<FsNode.Directory, Long> =
        mutableMapOf<FsNode.Directory, Long>().apply {
            fun FsNode.Directory.du(): Long = getOrPut(this) {
                contents.sumOf { node ->
                    when (node) {
                        is FsNode.Directory -> node.du()
                        is FsNode.File -> node.size
                    }
                }
            }

            root.du() // to fill up the cache
        }

    private fun parseFilesystem(input: List<String>): FsNode.Directory {
        val root = FsNode.Directory("/")
        var currentDirectory = root

        for (line in input) {
            when {
                line.startsWith("$") -> {
                    val (command, args) = line.substringAfter("$").trim().split(" ").let {
                        it.first() to it.drop(1)
                    }

                    when (command) {
                        "cd" -> {
                            currentDirectory = when (val dirName = args.first()) {
                                "/" -> root
                                ".." -> currentDirectory.parent!!
                                else -> currentDirectory.contents
                                    .asSequence()
                                    .filterIsInstance<FsNode.Directory>()
                                    .first { it.name == dirName }
                            }
                        }

                        "ls" -> Unit // nothing to do
                    }
                }

                else -> {
                    val (dirOrSize, name) = line.split(" ")

                    currentDirectory.contents.add(
                        when (dirOrSize) {
                            "dir" -> FsNode.Directory(name, parent = currentDirectory)
                            else -> FsNode.File(name, size = dirOrSize.toLong())
                        }
                    )
                }
            }
        }

        return root
    }

    sealed interface FsNode {
        val name: String

        data class Directory(override val name: String, val parent: Directory? = null) : FsNode {
            val contents: MutableList<FsNode> = mutableListOf()
        }

        data class File(override val name: String, val size: Long) : FsNode
    }
}