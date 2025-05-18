package com.example.calculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.calculator.ui.theme.CalculatorTheme
import androidx.lifecycle.viewmodel.compose.viewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalculatorTheme {
                Surface(
                    color = Color(0xFF00162a),
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(modifier = Modifier) {
                        val viewModel: CalculatorViewModel = viewModel()
                        CalculatorScreen(viewModel = viewModel)
                    }
                }
            }
        }
    }
}

/* Calculator App GamePlan:
*   Features:
*       - Main Buttons:
*           - Remove the last digit -> divide by 10
* */







