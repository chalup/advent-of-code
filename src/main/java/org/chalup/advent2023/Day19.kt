package org.chalup.advent2023

import org.chalup.advent2023.Workflow.ConditionSpec.Operator
import org.chalup.advent2023.Workflow.Destination.FinalDecision
import org.chalup.advent2023.Workflow.Destination.NamedWorkflow
import org.chalup.utils.textBlocks

object Day19 {
    fun task1(input: List<String>): Long {
        val (workflowsInput, partsInput) = textBlocks(input)

        val workflowsByName = workflowsInput
            .map { parseWorkflow(it) }
            .associateBy { it.name }

        infix fun Part.matches(spec: Workflow.ConditionSpec?): Boolean =
            when (spec?.operator) {
                null -> true
                Operator.GT -> getValue(spec.property) > spec.value
                Operator.LT -> getValue(spec.property) < spec.value
            }

        tailrec fun processPart(part: Part, workflowName: String = "in"): FinalDecision {
            val workflow = workflowsByName.getValue(workflowName)

            for (step in workflow.steps) {
                if (part matches step.conditionSpec) {
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

    private fun parseCondition(input: String): Workflow.ConditionSpec {
        val operator = Operator.values().first { it.symbol in input }
        val property = input.substringBefore(operator.symbol)
        val value = input.substringAfter(operator.symbol).toLong()

        return Workflow.ConditionSpec(
            property,
            value,
            operator
        )
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
        val conditionSpec: ConditionSpec?,
        val destination: Destination
    )

    data class ConditionSpec(
        val property: String,
        val value: Long,
        val operator: Operator,
    ) {
        enum class Operator(val symbol: Char) {
            GT('>'), LT('<')
        }
    }

    sealed interface Destination {
        data class NamedWorkflow(val name: String) : Destination
        enum class FinalDecision : Destination {
            ACCEPT, REJECT
        }
    }
}

private typealias Part = Map<String, Long>
