package org.chalup.advent2024

object Day9 {
    object Task1 {
        fun task1(input: List<String>) = parseDiskMap(input)
            .also { compact(it) }
            .let { checksum(it) }

        private fun parseDiskMap(input: List<String>) = input
            .single()
            .map { it.digitToInt() }
            .let { diskMap ->
                mutableListOf<Long?>().apply {
                    var programId = 0L
                    val i = diskMap.iterator()

                    while (i.hasNext()) {
                        val programSize = i.next()
                        repeat(programSize) { add(programId) }
                        programId += 1

                        if (i.hasNext()) {
                            val gapSize = i.next()
                            repeat(gapSize) { add(null) }
                        }
                    }
                }
            }

        private fun compact(disk: MutableList<Long?>) {
            var l = 0
            var r = disk.lastIndex

            while (l < r) {
                while (disk[l] != null) l++
                while (disk[r] == null) r--

                if (l > r) break
                disk[l++] = disk[r]
                disk[r--] = null
            }
        }

        private fun checksum(disk: List<Long?>) = disk
            .asSequence()
            .withIndex()
            .sumOf { (i, id) -> i * (id ?: 0) }
    }

    object Task2 {
        fun task2(input: List<String>) = parseDiskMapBlocks(input)
            .also { defragment(it) }
            .let { checksum(it) }

        private fun parseDiskMapBlocks(input: List<String>) = input
            .single()
            .map { it.digitToInt() }
            .let { diskMap ->
                mutableListOf<Block>().apply {
                    var programId = 0L
                    val i = diskMap.iterator()

                    while (i.hasNext()) {
                        add(Block.Program(id = programId++, size = i.next()))
                        if (i.hasNext()) add(Block.EmptySpace(size = i.next()))
                    }
                }
            }

        private fun defragment(disk: MutableList<Block>) {
            var r = disk.lastIndex

            program@ while (r >= 0) {
                when (val src = disk[r]) {
                    is Block.EmptySpace -> r--
                    is Block.Program -> {
                        var l = 0
                        while (l < r) {
                            when (val dst = disk[l]) {
                                is Block.Program -> l++
                                is Block.EmptySpace -> if (dst.size >= src.size) {
                                    disk.add(l++, src)
                                    disk[l] = dst.copy(size = dst.size - src.size)
                                    // no need to compact the empty space at the end
                                    disk[r + 1] = Block.EmptySpace(size = src.size)

                                    continue@program
                                } else {
                                    l++
                                }
                            }
                        }
                        r--
                    }
                }
            }
        }

        private fun checksum(disk: List<Block>) = disk
            .fold(0L to 0L) { (acc, offset), block ->
                val blockChecksum = when (block) {
                    is Block.EmptySpace -> 0L
                    is Block.Program -> (offset until offset + block.size).sum() * block.id
                }

                acc + blockChecksum to (offset + block.size)
            }
            .let { (checksum, _) -> checksum }

        private sealed interface Block {
            val size: Int

            data class Program(val id: Long, override val size: Int) : Block
            data class EmptySpace(override val size: Int) : Block
        }
    }
}