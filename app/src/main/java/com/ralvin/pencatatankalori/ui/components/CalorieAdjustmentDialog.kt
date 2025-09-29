package com.ralvin.pencatatankalori.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.ralvin.pencatatankalori.health.model.MifflinModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalorieAdjustmentDialog(
    onDismiss: () -> Unit,
    onSave: (Int) -> Unit,
    currentValue: Int = MifflinModel.getGranularityValue()
) {
    var adjustmentValue by remember { mutableStateOf(currentValue.toString()) }
    var showPresets by remember { mutableStateOf(false) }
    
    val isValid = adjustmentValue.isNotBlank() && 
                  adjustmentValue.toIntOrNull() != null && 
                  adjustmentValue.toInt() in 100..1000

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = MaterialTheme.shapes.large,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Adjust Calorie Target",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Text(
                    text = "Customize how many calories to add/subtract from your base metabolism for weight goals.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = adjustmentValue,
                    onValueChange = { 
                        if (it.isEmpty() || (it.toIntOrNull() != null && it.toInt() <= 1000)) {
                            adjustmentValue = it
                        }
                    },
                    label = { Text("Calorie Adjustment") },
                    suffix = { Text("cal") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = !isValid,
                    supportingText = {
                        if (!isValid) {
                            Text(
                                text = "Please enter a value between 100-1000 calories",
                                color = MaterialTheme.colorScheme.error
                            )
                        } else {
                            Text("Recommended range: 300-700 calories")
                        }
                    }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Preset buttons
                Text(
                    text = "Quick Presets:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PresetButton(
                        text = "Conservative\n(300 cal)",
                        value = 300,
                        onClick = { adjustmentValue = "300" },
                        modifier = Modifier.weight(1f)
                    )
                    PresetButton(
                        text = "Moderate\n(500 cal)",
                        value = 500,
                        onClick = { adjustmentValue = "500" },
                        modifier = Modifier.weight(1f)
                    )
                    PresetButton(
                        text = "Aggressive\n(700 cal)",
                        value = 700,
                        onClick = { adjustmentValue = "700" },
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Information card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "How this affects your calorie target:",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Text(
                            text = "• Weight Loss: Base calories - ${adjustmentValue.toIntOrNull() ?: currentValue}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "• Weight Gain: Base calories + ${adjustmentValue.toIntOrNull() ?: currentValue}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (isValid) {
                                onSave(adjustmentValue.toInt())
                                onDismiss()
                            }
                        },
                        enabled = isValid
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}

@Composable
private fun PresetButton(
    text: String,
    value: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(56.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 2
        )
    }
}
