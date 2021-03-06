package org.chalup.advent2018

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class Day20Test {
    @ParameterizedTest
    @MethodSource("paths")
    fun `should find the shortest path to the most distant room`(path: String, solution: Int) {
        assertThat(Day20.part1(path)).isEqualTo(solution)
    }

    @Suppress("unused")
    companion object {
        @JvmStatic
        fun paths() = listOf<Arguments>(
            Arguments.arguments("^WNE$", 3),
            Arguments.arguments("^ENWWW(NEEE|SSE(EE|N))$", 10),
            Arguments.arguments("^ENNWSWW(NEWS|)SSSEEN(WNSE|)EE(SWEN|)NNN$", 18),
            Arguments.arguments("^ESSWWN(E|NNENN(EESS(WNSE|)SSS|WWWSSSSE(SW|NNNE)))$", 23),
            Arguments.arguments("^WSSEESWWWNW(S|NENNEEEENN(ESSSSW(NWSW|SSEN)|WSWWN(E|WWS(E|SS))))$", 31),
            Arguments.arguments("^$", 0),
            Arguments.arguments("^(NEWS|)$", 2),
            Arguments.arguments("^NNNN(E|WW)NNNN$", 10),
            Arguments.arguments("^EEEE(N|EENWW)NNNN$", 9),
            // from reddit
            Arguments.arguments("^E(NN|S)E$", 4),
            Arguments.arguments("^(N|S)N$", 2),
            Arguments.arguments("^EEE(NN|SSS)EEE$", 9),
            Arguments.arguments("^E(N|SS)EEE(E|SSS)$", 9),
            Arguments.arguments("^(N|S|E|W)(N|S|E|W)(N|S|E|W)(N|S|E|W)(N|S|E|W)(N|S|E|W)(N|S|E|W)(N|S|E|W)(N|S|E|W)(N|S|E|W)(N|S|E|W)(N|S|E|W)(N|S|E|W)(N|S|E|W)(N|S|E|W)(N|S|E|W)(N|S|E|W)(N|S|E|W)(N|S|E|W)(N|S|E|W)(N|S|E|W)(N|S|E|W)(N|S|E|W)(N|S|E|W)(N|S|E|W)(N|S|E|W)(N|S|E|W)(N|S|E|W)(N|S|E|W)(N|S|E|W)(N|S|E|W)(N|S|E|W)(N|S|E|W)(N|S|E|W)(N|S|E|W)(N|S|E|W)(N|S|E|W)(N|S|E|W)(N|S|E|W)(N|S|E|W)(N|S|E|W)(N|S|E|W)(N|S|E|W)(N|S|E|W)(N|S|E|W)(N|S|E|W)(N|S|E|W)(N|S|E|W)(N|S|E|W)(N|S|E|W)(N|S|E|W)(N|S|E|W)(N|S|E|W)(N|S|E|W)(N|S|E|W)(N|S|E|W)(N|S|E|W)(N|S|E|W)(N|S|E|W)(N|S|E|W)(N|S|E|W)(N|S|E|W)(N|S|E|W)(N|S|E|W)(N|S|E|W)(N|S|E|W)(N|S|E|W)(N|S|E|W)(N|S|E|W)(N|S|E|W)(N|S|E|W)(N|S|E|W)(N|S|E|W)(N|S|E|W)(N|S|E|W)(N|S|E|W)(N|S|E|W)(N|S|E|W)(N|S|E|W)(N|S|E|W)(N|S|E|W)(N|S|E|W)(N|S|E|W)(N|S|E|W)(N|S|E|W)(N|S|E|W)(N|S|E|W)(N|S|E|W)(N|S|E|W)(N|S|E|W)(N|S|E|W)(N|S|E|W)(N|S|E|W)$", 93)
        )
    }
}