package org.chalup.advent2023

import org.chalup.utils.size
import org.chalup.advent2023.Workflow.ConditionSpec.Operator
import org.chalup.advent2023.Workflow.Destination.FinalDecision
import org.chalup.advent2023.Workflow.Destination.NamedWorkflow
import org.chalup.utils.intersection
import org.chalup.utils.textBlocks
import java.util.LinkedList

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

    fun task2(input: List<String>): Long {
        val (workflowsInput) = textBlocks(input)

        val workflowStepsByName = workflowsInput
            .map { parseWorkflow(it) }
            .associate { it.name to it.steps }

        val queue = LinkedList<WorkflowsTraversal>()
        queue.add(WorkflowsTraversal(pendingSteps = workflowStepsByName.getValue("in"), ranges = "xmas".associateWith { FULL_RANGE }))

        val acceptedRanges = buildList<AcceptedRanges> {
            while (queue.isNotEmpty()) {
                val traversal = queue.poll()

                if (traversal.ranges.isEmpty()) continue
                if (traversal.ranges.any { (_, range) -> range.isEmpty() }) continue
                val nextStep = traversal.pendingSteps.firstOrNull() ?: continue

                val rangesIfTrue: AcceptedRanges
                val rangesIfFalse: AcceptedRanges

                fun AcceptedRanges.constrain(property: Char, subrange: LongRange) = toMutableMap()
                    .apply { put(property, (getValue(property) intersection subrange) ?: LongRange.EMPTY) }

                when (nextStep.conditionSpec?.operator) {
                    Operator.GT -> {
                        rangesIfTrue = traversal.ranges.constrain(nextStep.conditionSpec.property, (nextStep.conditionSpec.value + 1)..FULL_RANGE.last)
                        rangesIfFalse = traversal.ranges.constrain(nextStep.conditionSpec.property, FULL_RANGE.first..nextStep.conditionSpec.value)
                    }

                    Operator.LT -> {
                        rangesIfTrue = traversal.ranges.constrain(nextStep.conditionSpec.property, FULL_RANGE.first until nextStep.conditionSpec.value)
                        rangesIfFalse = traversal.ranges.constrain(nextStep.conditionSpec.property, nextStep.conditionSpec.value..FULL_RANGE.last)
                    }

                    null -> {
                        rangesIfTrue = traversal.ranges
                        rangesIfFalse = emptyMap()
                    }
                }

                when (val destination = nextStep.destination) {
                    FinalDecision.ACCEPT -> add(rangesIfTrue)
                    FinalDecision.REJECT -> Unit
                    is NamedWorkflow -> queue.add(WorkflowsTraversal(workflowStepsByName.getValue(destination.name), rangesIfTrue))
                }
                queue.add(WorkflowsTraversal(pendingSteps = traversal.pendingSteps.drop(1), rangesIfFalse))
            }
        }

        return acceptedRanges.sumOf { ranges ->
            ranges.values.map { it.size() }.reduce(Long::times)
        }
    }

    private fun parseCondition(input: String): Workflow.ConditionSpec {
        val operator = Operator.values().first { it.symbol in input }
        val property = input.substringBefore(operator.symbol).single()
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
        .associate { it.substringBefore('=').single() to it.substringAfter('=').toLong() }
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
        val property: Char,
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

private typealias Part = Map<Char, Long>
private typealias AcceptedRanges = Map<Char, LongRange>
private val FULL_RANGE = 1..4000L

private data class WorkflowsTraversal(
    val pendingSteps: List<Workflow.Step>,
    val ranges: AcceptedRanges
)