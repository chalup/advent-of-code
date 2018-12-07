package org.chalup.advent2018

object Day7 {
    private val constraintRegex = """Step (.*?) must be finished before step (.*?) can begin.""".toRegex()

    data class Constraint(val task: String, val dependsOn: String)

    class Plan(var taskList: List<String> = emptyList(),
               var unqueuedTasks: Set<String>,
               var unresolvedConstraints: Map<String, List<String>>)

    private fun parse(constraint: String) = constraintRegex
        .matchEntire(constraint)!!
        .destructured
        .let { (dependsOn, task) -> Constraint(task, dependsOn) }

    fun getTaskList(input: List<String>): String = input
        .map { parse(it) }
        .let { constraints ->
            val plan = Plan(
                unqueuedTasks = constraints
                    .flatMap { listOf(it.task, it.dependsOn) }
                    .toSet(),
                unresolvedConstraints = constraints
                    .groupBy { it.task }
                    .mapValues { (_, constraints) -> constraints.map { it.dependsOn } })

            with(plan) {
                while (unqueuedTasks.isNotEmpty()) {
                    val nextTask = (unqueuedTasks - unresolvedConstraints.keys).sorted().first()

                    taskList += nextTask
                    unqueuedTasks -= nextTask
                    unresolvedConstraints = unresolvedConstraints
                        .mapValues { (_, constraints) -> constraints.filterNot { it == nextTask } }
                        .filterValues { it.isNotEmpty() }
                }
            }

            plan.taskList.joinToString(separator = "")
        }

    data class WorkerAllocation(val task: String, var timeUntilDone: Int) {
        fun isDone() = timeUntilDone == 0
    }

    fun calculateConstructionTime(input: List<String>, workers: Int, baseTaskLength: Int = 60): Int = input
        .map { parse(it) }
        .let { constraints ->
            val plan = Plan(
                unqueuedTasks = constraints
                    .flatMap { listOf(it.task, it.dependsOn) }
                    .toSet(),
                unresolvedConstraints = constraints
                    .groupBy { it.task }
                    .mapValues { (_, constraints) -> constraints.map { it.dependsOn } })

            val workersAllocation = mutableListOf<WorkerAllocation>()

            fun taskLength(task: String) = baseTaskLength + (task.first().toUpperCase() - 'A') + 1

            var timePassed = 0

            with(plan) {
                while (unqueuedTasks.isNotEmpty()) {
                    while (workersAllocation.size < workers) {
                        val nextTask = (unqueuedTasks - unresolvedConstraints.keys)
                            .filterNot { potentialTask -> workersAllocation.any { potentialTask == it.task } }
                            .sorted().firstOrNull() ?: break

                        workersAllocation += WorkerAllocation(nextTask, taskLength(nextTask))
                    }

                    timePassed += 1

                    workersAllocation.forEach {
                        it.timeUntilDone -= 1

                        if (it.isDone()) {
                            val task = it.task
                            with(plan) {
                                taskList += task
                                unqueuedTasks -= task
                                unresolvedConstraints = unresolvedConstraints
                                    .mapValues { (_, constraints) -> constraints.filterNot { it == task } }
                                    .filterValues { it.isNotEmpty() }
                            }
                        }
                    }
                    workersAllocation.removeAll { it.isDone() }
                }
            }

            timePassed
        }
}