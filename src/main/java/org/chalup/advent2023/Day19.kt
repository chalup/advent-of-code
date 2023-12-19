package org.chalup.advent2023

import org.chalup.advent2023.Workflow.Destination.FinalDecision
import org.chalup.advent2023.Workflow.Destination.NamedWorkflow
import org.chalup.utils.textBlocks

object Day19 {
    fun task1(input: List<String>): Long {
        val (workflowsInput, partsInput) = textBlocks(input)

        val workflowsByName = workflowsInput
            .map { parseWorkflow(it) }
            .associateBy { it.name }

        tailrec fun processPart(part: Part, workflowName: String = "in"): FinalDecision {
            val workflow = workflowsByName.getValue(workflowName)

            for (step in workflow.steps) {
                if (step.condition?.invoke(part) != false) {
                    return when (val destination = step.destination) {
                        is NamedWorkflow -> processPart(part, workflowName = destination.name)
                        is FinalDecision -> destination
                    }
                }
            }

            throw IllegalStateException()
        }

        val parts = partsInput.map(::parsePart)

        return parts.filter { processPart(it) == FinalDecision.ACCEPT }
            .sumOf { it.values.sum() }
    }

    private fun parseCondition(input: String): (Part) -> Boolean {
        when {
            '<' in input -> return { p -> p.getValue(input.substringBefore('<')) < input.substringAfter('<').toLong() }
            '>' in input -> return { p -> p.getValue(input.substringBefore('>')) > input.substringAfter('>').toLong() }
            else -> throw IllegalArgumentException(input)
        }
    }

    private fun parseWorkflowStep(input: String): Workflow.Step {
        val destination = when (val destinationString = input.substringAfter(':')) {
            "A" -> FinalDecision.ACCEPT
            "R" -> FinalDecision.REJECT
            else -> NamedWorkflow(destinationString)
        }

        val condition = input
            .substringBefore(':', missingDelimiterValue = "")
            .takeIf(String::isNotEmpty)
            ?.let { parseCondition(it) }

        return Workflow.Step(condition, destination)
    }

    private fun parseWorkflow(input: String) = Workflow(
        name = input.substringBefore('{'),
        steps = input
            .substringAfter('{')
            .substringBefore('}')
            .split(",")
            .map { parseWorkflowStep(it) }
    )

    fun parsePart(input: String): Part = input
        .trim('{', '}')
        .splitToSequence(',')
        .associate { it.substringBefore('=') to it.substringAfter('=').toLong() }
}

private data class Workflow(
    val name: String,
    val steps: List<Step>,
) {
    data class Step(
        val condition: ((Part) -> Boolean)?,
        val destination: Destination
    )

    sealed interface Destination {
        data class NamedWorkflow(val name: String) : Destination
        enum class FinalDecision : Destination {
            ACCEPT, REJECT
        }
    }
}

private typealias Part = Map<String, Long>
