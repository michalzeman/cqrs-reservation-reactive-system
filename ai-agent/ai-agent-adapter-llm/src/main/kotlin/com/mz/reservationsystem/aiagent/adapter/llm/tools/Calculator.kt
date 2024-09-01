package com.mz.reservationsystem.aiagent.adapter.llm.tools

import dev.langchain4j.agent.tool.Tool
import org.springframework.stereotype.Component

@Component
class Calculator {
    @Tool("Returns the sum of two numbers.")
    fun sumOfTwoNumbers(num1: Double, num2: Double): Double {
        return num1 + num2
    }
}