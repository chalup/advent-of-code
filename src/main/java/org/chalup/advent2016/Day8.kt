package org.chalup.advent2016

import org.chalup.advent2016.Day8.DisplayInstruction.Rect
import org.chalup.advent2016.Day8.DisplayInstruction.RotateColumn
import org.chalup.advent2016.Day8.DisplayInstruction.RotateRow
import org.chalup.utils.match

object Day8 {
    fun task1(input: List<String>): Int = input
        .let(this::decodeImage)
        .enabledPixelsCount()

    fun task2(input: List<String>): String = input
        .let(this::decodeImage)
        .dump()

    private fun decodeImage(input: List<String>) = input
        .map(this::parseInstruction)
        .fold(Display()) { display, instruction ->
            when (instruction) {
                is Rect -> display.addRect(instruction.width, instruction.height)
                is RotateColumn -> display.rotateColumn(instruction.index, instruction.offset)
                is RotateRow -> display.rotateRow(instruction.index, instruction.offset)
            }
        }

    private fun parseInstruction(instruction: String) = match<DisplayInstruction>(instruction) {
        pattern("""rect (\d+)x(\d+)""") { (width, height) -> Rect(width.toInt(), height.toInt()) }
        pattern("""rotate column x=(\d+) by (\d+)""") { (index, offset) -> RotateColumn(index.toInt(), offset.toInt()) }
        pattern("""rotate row y=(\d+) by (\d+)""") { (index, offset) -> RotateRow(index.toInt(), offset.toInt()) }
    }

    private sealed class DisplayInstruction {
        data class RotateRow(val index: Int, val offset: Int) : DisplayInstruction()
        data class RotateColumn(val index: Int, val offset: Int) : DisplayInstruction()
        data class Rect(val width: Int, val height: Int) : DisplayInstruction()
    }

    class Display(private val pixels: Array<Boolean> = Array(WIDTH * HEIGHT) { false }) {
        fun enabledPixelsCount() = pixels.count { it }

        fun dump() = buildString {
            appendLine()
            repeat(HEIGHT) { y ->
                repeat(WIDTH) { x ->
                    append(if (pixels[y * WIDTH + x]) "#" else " ")
                }
                appendLine()
            }
        }

        fun addRect(width: Int, height: Int) = copy {
            for (x in 0 until width) {
                for (y in 0 until height) {
                    set(y * WIDTH + x, true)
                }
            }
        }

        fun rotateColumn(columnIndex: Int, offset: Int) = copy {
            for (y in 0 until HEIGHT) {
                set(y * WIDTH + columnIndex, pixels[((y + HEIGHT - (offset % HEIGHT)) % HEIGHT) * WIDTH + columnIndex])
            }
        }

        fun rotateRow(rowIndex: Int, offset: Int) = copy {
            for (x in 0 until WIDTH) {
                set(rowIndex * WIDTH + x, pixels[rowIndex * WIDTH + ((x + WIDTH - (offset % WIDTH)) % WIDTH)])
            }
        }

        private fun copy(block: Array<Boolean>.() -> Unit) = pixels
            .copyOf()
            .apply(block)
            .let(::Display)
    }

    private const val WIDTH = 50
    private const val HEIGHT = 6
}