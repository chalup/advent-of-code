package org.chalup.advent2021

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import org.chalup.advent2021.Day18.magnitude
import org.chalup.advent2021.Day18.plus

class Day18Test : FreeSpec({
    "Snailfish numbers reduction" - {
        val testCases = listOf(
            "[[[[[4,3],4],4],[7,[[8,4],9]]],[1,1]]" to "[[[[0,7],4],[[7,8],[6,0]]],[8,1]]",
            "[[[[[9,8],1],2],3],4]" to "[[[[0,9],2],3],4]",
            "[7,[6,[5,[4,[3,2]]]]]" to "[7,[6,[5,[7,0]]]]",
            "[[6,[5,[4,[3,2]]]],1]" to "[[6,[5,[7,0]]],3]",
            "[[3,[2,[1,[7,3]]]],[6,[5,[4,[3,2]]]]]" to "[[3,[2,[8,0]]],[9,[5,[7,0]]]]",
        )

        testCases.forEach { (input, expected) ->
            "$input should reduce to $expected" {
                val actual = input
                    .let(Day18::parseSnailfishNumber)
                    .let(Day18::reduce)
                    .toString()

                actual shouldBe expected
            }
        }
    }

    "Snailfish numbers adding" - {
        val testCases = listOf(
            """
                [1,1]
                [2,2]
                [3,3]
                [4,4]
            """.trimIndent() to "[[[[1,1],[2,2]],[3,3]],[4,4]]",
            """
                [1,1]
                [2,2]
                [3,3]
                [4,4]
                [5,5]
            """.trimIndent() to "[[[[3,0],[5,3]],[4,4]],[5,5]]",
            """
                [1,1]
                [2,2]
                [3,3]
                [4,4]
                [5,5]
                [6,6]
            """.trimIndent() to "[[[[5,0],[7,4]],[5,5]],[6,6]]",
            """
                [[[0,[4,5]],[0,0]],[[[4,5],[2,6]],[9,5]]]
                [7,[[[3,7],[4,3]],[[6,3],[8,8]]]]
            """.trimIndent() to "[[[[4,0],[5,4]],[[7,7],[6,0]]],[[8,[7,7]],[[7,9],[5,0]]]]",
            """
                [[[0,[4,5]],[0,0]],[[[4,5],[2,6]],[9,5]]]
                [7,[[[3,7],[4,3]],[[6,3],[8,8]]]]
                [[2,[[0,8],[3,4]]],[[[6,7],1],[7,[1,6]]]]
                [[[[2,4],7],[6,[0,5]]],[[[6,8],[2,8]],[[2,1],[4,5]]]]
                [7,[5,[[3,8],[1,4]]]]
                [[2,[2,2]],[8,[8,1]]]
                [2,9]
                [1,[[[9,3],9],[[9,0],[0,7]]]]
                [[[5,[7,4]],7],1]
                [[[[4,2],2],6],[8,7]]
            """.trimIndent() to "[[[[8,7],[7,7]],[[8,6],[7,7]]],[[[0,7],[6,6]],[8,7]]]"
        )

        testCases.forEachIndexed { i, (input, expected) ->
            "Addition case $i" {
                val actual = input
                    .lines()
                    .map(String::trim)
                    .map(Day18::parseSnailfishNumber)
                    .reduce { a, b -> a + b }
                    .toString()

                actual shouldBe expected
            }
        }
    }

    "Snailfish numbers magnitude" - {
        val testCases = listOf(
            "[[1,2],[[3,4],5]]" to 143,
            "[[[[0,7],4],[[7,8],[6,0]]],[8,1]]" to 1384,
            "[[[[1,1],[2,2]],[3,3]],[4,4]]" to 445,
            "[[[[3,0],[5,3]],[4,4]],[5,5]]" to 791,
            "[[[[5,0],[7,4]],[5,5]],[6,6]]" to 1137,
            "[[[[8,7],[7,7]],[[8,6],[7,7]]],[[[0,7],[6,6]],[8,7]]]" to 3488,
        )

        testCases.forEach { (input, expected) ->
            "magnitude($input) = $expected" {
                val actual = input
                    .let(Day18::parseSnailfishNumber)
                    .magnitude()

                actual shouldBe expected
            }
        }
    }
})