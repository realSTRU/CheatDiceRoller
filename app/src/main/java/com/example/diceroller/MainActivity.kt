package com.example.diceroller

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.diceroller.ui.theme.DiceRollerTheme
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DiceRollerTheme {
                DiceApp()
            }
        }
    }
}

@Composable
fun DiceApp() {
    var showDialog by remember { mutableStateOf(false) }
    var probabilities by remember { mutableStateOf(List(6) { 1.0 / 6 }) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dice App") },
                actions = {
                    IconButton(onClick = { showDialog = true }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        },
        content = { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                DiceWithButtonAndImage(probabilities = probabilities)
            }
        }
    )

    if (showDialog) {
        ProbabilitySettingsDialog(
            initialProbabilities = probabilities,
            onDismiss = { showDialog = false },
            onSave = { newProbabilities ->
                probabilities = newProbabilities
                showDialog = false
            }
        )
    }
}

@Composable
fun DiceWithButtonAndImage(modifier: Modifier = Modifier, probabilities: List<Double>) {
    var result by remember { mutableStateOf(1) }
    val imageResource = when (result) {
        1 -> R.drawable.dice_1
        2 -> R.drawable.dice_2
        3 -> R.drawable.dice_3
        4 -> R.drawable.dice_4
        5 -> R.drawable.dice_5
        else -> R.drawable.dice_6
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(imageResource),
            contentDescription = result.toString()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { result = rollDice(probabilities) }) {
            Text(stringResource(R.string.roll))
        }
    }
}

fun rollDice(probabilities: List<Double>): Int {
    val randomValue = Math.random()
    var cumulativeProbability = 0.0
    for ((index, probability) in probabilities.withIndex()) {
        cumulativeProbability += probability
        if (randomValue <= cumulativeProbability) {
            return index + 1
        }
    }
    return 6 // fallback en caso de errores de redondeo
}

@Composable
fun ProbabilitySettingsDialog(
    initialProbabilities: List<Double>,
    onDismiss: () -> Unit,
    onSave: (List<Double>) -> Unit
) {
    var probabilities by remember { mutableStateOf(initialProbabilities) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Set Probabilities") },
        text = {
            Column {
                (0 until probabilities.size).forEach { index ->
                    ProbabilitySlider(
                        probability = probabilities[index],
                        onProbabilityChange = { newProbability ->
                            val newProbabilities = probabilities.toMutableList()
                            newProbabilities[index] = newProbability
                            probabilities = newProbabilities
                        }
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                onSave(probabilities)
                onDismiss()
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun ProbabilitySlider(
    probability: Double,
    onProbabilityChange: (Double) -> Unit
) {
    var sliderValue by remember { mutableStateOf((probability * 100).toFloat()) }

    Column {
        Text("${(probability * 100).toInt()}%")
        Slider(
            value = sliderValue,
            onValueChange = { newValue ->
                sliderValue = newValue
                onProbabilityChange((newValue / 100).toDouble())
            },
            valueRange = 0f..100f,
            steps = 100,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
