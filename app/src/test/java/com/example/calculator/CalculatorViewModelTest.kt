package com.example.calculator

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class CalculatorViewModelTest {
    private val viewModel = CalculatorViewModel()

    // ✅ Happy Case Scenarios
    @Test fun testCase1() = assertEquals("1", viewModel.evaluateExpression("3+4×2/(1-5)"))
    @Test fun testCase2() = assertEquals("15", viewModel.evaluateExpression("((2+3)×4)-5"))
    @Test fun testCase3() = assertEquals("13", viewModel.evaluateExpression("10+(6/2)"))
    @Test fun testCase4() = assertEquals("70", viewModel.evaluateExpression("7×(8+(3-1))"))
    @Test fun testCase5() = assertEquals("2", viewModel.evaluateExpression("0.5×2+1"))
    @Test fun testCase6() = assertEquals("10", viewModel.evaluateExpression("4+18/(9-6)"))
    @Test fun testCase7() = assertEquals("2", viewModel.evaluateExpression("12/(2×(2+1))"))
    @Test fun testCase8() = assertEquals("5", viewModel.evaluateExpression("(5+3.5)-(2.5+1)"))
    @Test fun testCase9() = assertEquals("55", viewModel.evaluateExpression("1+2+3+4+5+6+7+8+9+10"))
    @Test fun testCase10() = assertEquals("2", viewModel.evaluateExpression("(((((1+1)))))"))

    // ❌ Rainy Case Scenarios
    @Test
    fun testIncompleteExpression() {
        assertEquals("Error", viewModel.evaluateExpression("3+"))
    }

    @Test
    fun testUnexpectedParentheses() {
        assertEquals("Error", viewModel.evaluateExpression("()4+5"))
    }

    @Test
    fun testUnmatchedLeftParenthesis() {
        assertEquals("Error", viewModel.evaluateExpression("4+(5×2"))
    }

    @Test
    fun testDivisionByZero() {
        assertEquals("Math Error", viewModel.evaluateExpression("10/(5-5)"))
    }

    @Test
    fun testInvalidDecimal() {
        assertEquals("Error", viewModel.evaluateExpression("..5+3"))
    }

    @Test
    fun testDoubleOperator() {
        assertEquals("Error", viewModel.evaluateExpression("5++2"))
    }

    @Test
    fun testUnmatchedRightParenthesis() {
        assertEquals("Error", viewModel.evaluateExpression("(3+2))"))
    }

    @Test
    fun testInvalidCharacters() {
        assertEquals("Error", viewModel.evaluateExpression("abc+1"))
    }

    @Test
    fun testInvalidNumberFormat() {
        assertEquals("Error", viewModel.evaluateExpression("5.5.5+2"))
    }

    @Test
    fun testEmptyParentheses() {
        assertEquals("Error", viewModel.evaluateExpression("7×()"))
    }

}
