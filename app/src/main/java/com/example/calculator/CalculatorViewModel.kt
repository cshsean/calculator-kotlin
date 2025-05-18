package com.example.calculator

import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.compose.runtime.mutableStateOf
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.Locale
import javax.xml.xpath.XPathExpression
import kotlin.collections.ArrayDeque
import kotlin.math.exp

val operators = listOf('+', '-', '/', '×')

class CalculatorViewModel(
    private val savedStateHandle: SavedStateHandle
): ViewModel() {
    companion object {
        private const val DISPLAY_EXPRESSION_KEY = "display_expression"
    }

    val displayExpression = mutableStateOf(
        savedStateHandle.get<String>(DISPLAY_EXPRESSION_KEY) ?: ""
    )

    val previousExpression = mutableStateOf(
        savedStateHandle.get<String>(DISPLAY_EXPRESSION_KEY) ?: ""
    )

    var isCalculationsPresentBefore = false
    var errorPresent = false

    var token = listOf<String>()
    var postFix = listOf<String>()

    fun buttonClicked(char: Char) {
        val current = displayExpression.value
        val lastChar = current.takeLast(1).singleOrNull()
        val lastTwoChars = current.takeLast(2)

        // Clear previous calculations
        if ((isCalculationsPresentBefore && char !in operators) || errorPresent) {
            displayExpression.value = ""
            isCalculationsPresentBefore = false
            errorPresent = false
        } else {
            isCalculationsPresentBefore = false
        }

        when {
            // Clear last character
            char == 'C'-> {
                if (current.isNotEmpty()) {
                    displayExpression.value = displayExpression.value.dropLast(1) }
                else {
                    previousExpression.value = ""
                }
            }

            // Handle operator replacement for negative expressions
            char in operators && (lastTwoChars == "×-" || lastTwoChars == "/-") -> {
                displayExpression.value = current.dropLast(2) + char
            }

            char == '-' && lastChar in operators && lastChar != '-' -> {
                displayExpression.value += char
            }

            // Handle decimal input
            char == '.' -> {
                when {
                    current.isEmpty() || lastChar in operators ->
                        displayExpression.value += "0."
                    lastChar?.isDigit() == true ->
                        displayExpression.value += '.'
                }
            }

            // Prevent double operators
            char in operators && lastChar in operators -> {
                // Do nothing
            }

            // Submit equation
            char == '=' -> {
                try {
                    val tempPreviousExpressionHolder = displayExpression.value

                    token = convertInputToTokens(displayExpression.value)
                    postFix = convertTokenToPostfix(token)
                    displayExpression.value = convertPostfixToAnswer(postFix)

                    previousExpression.value = tempPreviousExpressionHolder
                } catch (e: Exception) {
                    // Log.d("Error", "Cannot do")
                    if (e is ArithmeticException && e.message == "Math Error") {
                        displayExpression.value = "Math Error"
                    } else {
                        displayExpression.value = "Error"
                    }
                }

                if (!isNumeric(displayExpression.value)) errorPresent = true
                isCalculationsPresentBefore = true
            }

            // Default case for valid inputs
            char != 'C' -> {
                displayExpression.value += char
            }
        }
    }

    // For testing
    @VisibleForTesting
    fun evaluateExpression(input: String): String {
        return convertPostfixToAnswer(convertTokenToPostfix(convertInputToTokens(input)))
    }
}

internal fun convertPostfixToAnswer(
    postFixList: List<String>
): String {
    val outputStack = ArrayDeque<String>()

    for (input in postFixList) {
        if (isNumeric(input)) {
            outputStack.addLast(input)
        } else if (outputStack.size >= 2 && input.single() in operators) {
            val op = input.single()
            when (op) {
                '+' -> {
                    val temp = outputStack.last().toDouble() + outputStack[outputStack.size-2].toDouble()
                    outputStack.removeLast()
                    outputStack.removeLast()
                    outputStack.addLast(temp.toString())
                }
                '-' -> {
                    val temp = outputStack[outputStack.size-2].toDouble() - outputStack.last().toDouble()
                    outputStack.removeLast()
                    outputStack.removeLast()
                    outputStack.addLast(temp.toString())
                }
                '×' -> {
                    val temp = outputStack[outputStack.size-2].toDouble() * outputStack.last().toDouble()
                    outputStack.removeLast()
                    outputStack.removeLast()
                    outputStack.addLast(temp.toString())
                }
                '/' -> {
                    if (outputStack.last().toDouble() == 0.0) throw ArithmeticException("Math Error")
                    val temp = outputStack[outputStack.size-2].toDouble() / outputStack.last().toDouble()
                    outputStack.removeLast()
                    outputStack.removeLast()
                    outputStack.addLast(temp.toString())
                }
                else -> {
                    throw Exception("Unknown Operator")
                }
            }
        } else {
            throw Exception("Invalid input found!")
        }
    }

    val result = outputStack.last().toDouble()
    return if (hasDecimal(result)) {
        result.toString()
    } else {
        result.toInt().toString()
    }
}

fun hasDecimal(double: Double): Boolean {
    return double != double.toInt().toDouble()
}

internal fun convertTokenToPostfix(
    tokenList: List<String>
): List<String> {
    val outputQueue = ArrayDeque<String>()
    val operationStack = ArrayDeque<String>()

    fun precedence(op: String): Int = when(op) {
        "+","-" -> 1
        "×","/" -> 2
        else -> 0
    }

    for (token in tokenList) {
        when {
            // If token is a digit
            isNumeric(token) -> {
                outputQueue.addLast(token)
            }

            // If token is a "("
            token == "(" -> {
                operationStack.addLast(token)
            }

            // If token is a ")"
            token == ")" -> {
                while (operationStack.isNotEmpty() && operationStack.last() != "(") {
                    outputQueue.addLast(operationStack.last())
                    operationStack.removeLast()
                }
                if (operationStack.isEmpty() || operationStack.last() != "(") {
                    throw Exception("Missing Parentheses")
                }
                operationStack.removeLast()
            }

            // If token is a operand
            token.single() in operators -> {
                while (operationStack.isNotEmpty() && precedence(operationStack.last()) >= precedence(token)) {
                    outputQueue.addLast(operationStack.last())
                    operationStack.removeLast()
                }
                operationStack.addLast(token)
            }

            else -> {
                throw Exception("Unknown Error: $token")
            }
        }
    }

    while (operationStack.isNotEmpty()) {
        val op = operationStack.removeLast()
        if (op == "(" || op == ")") throw Exception("Mismatched Parentheses")
        outputQueue.addLast(op)
    }

    // Check if order of postfix is correct
    /*outputQueue.forEachIndexed { index, input ->
        Log.d("SUCCESS", "the $index input is $input")
    }*/

    return outputQueue.toList()
}

fun isNumeric(input: String): Boolean {
    return input.toFloatOrNull() != null
}

internal fun convertInputToTokens(
    expression: String
): List<String> {
    var tokenList = mutableListOf<String>()
    var buffer = ""

    expression.forEachIndexed { index, char ->
        when {
            char.isDigit() || char == '.' -> {
                buffer += char
            }

            char == '-' && ((index > 0 && (expression[index-1] in operators)) || index == 0 )-> {
                buffer += '-'
            }

            char == ')' && expression[index-1] == '(' -> {
                throw Exception("Unnecessary Parentheses")
            }

            char in operators || char == '(' || char == ')' -> {
                if (buffer.isNotEmpty()) {
                    tokenList.add(buffer)
                    buffer = ""
                }
                tokenList.add(char.toString())
            }

            // handle unexpected chars?
        }
    }
    if (buffer.isNotEmpty()) {
        tokenList.add(buffer)
    }

    // To check if token list is correct
    /* tokenList.forEach { input ->
        Log.d("Tokens", "token is $input")
    }*/

    return tokenList
}









/* All Edge Cases:
* 1) if added input is operand and previous input is an operand, replace the
*       previous operand with newly input operand
* 2) if added input is a '-', and the previous input '*' or '/',
*       add it to the displayExpression
* 3) if added input is operand, and previous 2 inputs are '*-' or '/-',
*       replace previous 2 inputs with new input operand
* 4) if added input is '.', and previous input is '.', don't allow it.
* */

