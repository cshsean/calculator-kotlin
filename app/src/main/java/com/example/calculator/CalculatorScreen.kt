package com.example.calculator

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.currentComposer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculator.R

@Composable
fun CalculatorScreen(
    modifier: Modifier = Modifier,
    viewModel: CalculatorViewModel
) {
    val buttonRows = listOf(
        listOf('C', '(', ')', '/'),
        listOf('7', '8', '9', '×'),
        listOf('4', '5', '6', '-'),
        listOf('1', '2', '3', '+'),
        listOf('0','.','=')
    )

    val currentExpression by viewModel.displayExpression
    val previousExpression by viewModel.previousExpression

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(dimensionResource(R.dimen.outer_padding)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DisplayScreen(
                currentExpression = currentExpression,
                previousExpression = previousExpression,
                modifier = Modifier
                    .fillMaxWidth()
            )

            Column(
                modifier = Modifier
            ) {
                buttonRows.forEach { rowValues ->
                    Row() {
                        rowValues.forEach { value ->
                            val buttonWeight = if (value == '0') 2f else 1f
                            BoxButton(
                                onClick = { value ->
                                    viewModel.buttonClicked(value)
                                          },
                                insertedValue = value,
                                modifier = Modifier
                                    .weight(buttonWeight)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DisplayScreen(
    currentExpression: String,
    previousExpression: String,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    LaunchedEffect(currentExpression, previousExpression) {
        //  Scroll to the end when text changes
        scrollState.scrollTo(scrollState.maxValue)
    }

    Surface(
        color = Color(0xff01274a),
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .padding(bottom = 10.dp)
            .clip(RoundedCornerShape(10.dp))
    ) {
        Box(
            contentAlignment = Alignment.BottomEnd
        ) {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .fillMaxHeight()
            ) {
                Row(
                    modifier = Modifier
                        .horizontalScroll(scrollState)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = previousExpression,
                        color = Color.White,
                        fontSize = 28.sp,
                        maxLines = 1,
                        softWrap = false,
                        modifier = Modifier
                            .padding(top = 25.dp, end = 2.dp)
                    )
                }
                Row(
                    modifier = Modifier
                        .horizontalScroll(scrollState)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = currentExpression,
                        color = Color.White,
                        fontSize = 60.sp,
                        softWrap = false,
                        maxLines = 1,
                    )
                }
            }
        }
    }
}

@Composable
fun BoxButton(
    onClick: (Char) -> Unit,
    insertedValue: Char,
    modifier: Modifier = Modifier
) {
    val buttonAspectRatio = if (insertedValue == '0') 2.1f else 1f
    ElevatedButton(
        onClick = { onClick(insertedValue) },
        elevation = ButtonDefaults.buttonElevation(10.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF01325f),
            contentColor = Color.White
        ),
        modifier = modifier
            .padding(5.dp)
            .aspectRatio(buttonAspectRatio)
            .size(70.dp)
    ) {
        Text(
            text = insertedValue.toString(),
            fontSize = 30.sp
        )
    }
}

@Preview
@Composable
fun Preview_DisplayScreen() {
    DisplayScreen(
        currentExpression = "18847",
        previousExpression = "12+34"
    )
}