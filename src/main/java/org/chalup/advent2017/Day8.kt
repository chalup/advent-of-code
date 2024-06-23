package org.chalup.advent2017

object Day8 {
    fun task1(input: List<String>): Int {
        val registers = mutableMapOf<String, Int>().withDefault { 0 }

        input.forEach { line ->
            val (action, condition) = line.split(" if ")

            val (conditionRegister, operator, valueAsTest) = condition.trim().split(" ")

            val currentRegisterValue = registers.getValue(conditionRegister)
            val conditionValue = valueAsTest.toInt()

            val isConditionMet = when (operator) {
                ">" -> currentRegisterValue > conditionValue
                "<" -> currentRegisterValue < conditionValue
                ">=" -> currentRegisterValue >= conditionValue
                "<=" -> currentRegisterValue <= conditionValue
                "==" -> currentRegisterValue == conditionValue
                "!=" -> currentRegisterValue != conditionValue
                else -> throw IllegalArgumentException("Dunno how to handle $condition")
            }

            if (isConditionMet) {
                val (actionRegister, operation, changeAsText) = action.trim().split(" ")

                val multiplier = when (operation) {
                    "inc" -> +1
                    "dec" -> -1
                    else -> throw IllegalArgumentException("Dunno how to handle $action")
                }

                registers[actionRegister] = registers.getValue(actionRegister) + multiplier * changeAsText.toInt()
            }
        }

        return registers.values.max()
    }
}