package org.chalup.advent2018

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

class Day7Test {
    @Test
    fun `should create a proper list of steps`() {
        assertThat(Day7.getTaskList(steps)).isEqualTo("CABDFE")
    }

    @Test
    fun `should calculate construction time`() {
        assertThat(Day7.calculateConstructionTime(steps, workers = 2, baseTaskLength = 0)).isEqualTo(15)
    }

    companion object {
        val steps = listOf("Step C must be finished before step A can begin.",
                           "Step C must be finished before step F can begin.",
                           "Step A must be finished before step B can begin.",
                           "Step A must be finished before step D can begin.",
                           "Step B must be finished before step E can begin.",
                           "Step D must be finished before step E can begin.",
                           "Step F must be finished before step E can begin.")
    }
}