package com.ralvin.pencatatankalori.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.ralvin.pencatatankalori.health.model.CalorieStrategy
import com.ralvin.pencatatankalori.health.model.GoalType
import com.ralvin.pencatatankalori.health.model.MifflinModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalorieAdjustmentDialog(
    onDismiss: () -> Unit,
    onSave: (Int) -> Unit,
    currentValue: Int = MifflinModel.getGranularityValue(),
    goalType: GoalType
) {
    var selectedStrategy by remember {
        mutableStateOf(CalorieStrategy.fromGranularityValue(currentValue))
    }
    var baseAdjustment by remember {
        mutableStateOf(currentValue.toString())
    }

    // Validation for the base adjustment input
    val isBaseAdjustmentValid = baseAdjustment.isNotEmpty() && baseAdjustment.toIntOrNull() != null

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
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
                    text = "Calorie Strategy",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Pick how strict you want your calories for ${goalType.getDisplayName()}.\nEach option sets your base calorie adjustment and how much of your exercise calories you eat back.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                Text(
                    text = "Adjustment Options:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CalorieStrategy.values().forEach { strategy ->
                        StrategyChip(
                            strategy = strategy,
                            isSelected = selectedStrategy == strategy,
                            onClick = { selectedStrategy = strategy }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Base Calorie Adjustment Input
                Text(
                    text = "Base Calorie Adjustment:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = baseAdjustment,
                    onValueChange = { newValue ->
                        // Only allow numbers
                        if (newValue.all { it.isDigit() } || newValue.isEmpty()) {
                            baseAdjustment = newValue
                        }
                    },
                    label = { Text("Calories to ${if (goalType == GoalType.LOSE_WEIGHT) "subtract" else "add"} from TDEE") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    isError = !isBaseAdjustmentValid && baseAdjustment.isNotEmpty(),
                    supportingText = {
                        if (!isBaseAdjustmentValid && baseAdjustment.isNotEmpty()) {
                            Text("Please enter a valid number")
                        }
                    }
                )

                Text(
                    text = "Your TDEE (Total Daily Energy Expenditure) is calculated using the Mifflin-St Jeor equation. " +
                           "This value ${if (goalType == GoalType.LOSE_WEIGHT) "subtracts from" else "adds to"} your TDEE to create your ${if (goalType == GoalType.LOSE_WEIGHT) "deficit" else "surplus"}.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))


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
                            text = "${selectedStrategy.displayName} Plan",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        val baseText = if (selectedStrategy.granularityValue == 0) {
                            "This plan uses your TDEE without additional base adjustment."
                        } else {
                            "This plan ${if (goalType == GoalType.LOSE_WEIGHT) "subtracts" else "adds"} ${selectedStrategy.granularityValue} calories from your TDEE."
                        }

                        val goalSpecificText = when (goalType) {
                            GoalType.LOSE_WEIGHT -> {
                                val lossPercentage = ((1.0 - selectedStrategy.weightLossExercisePercentage) * 100).toInt()
                                "You'll eat back ${(selectedStrategy.weightLossExercisePercentage * 100).toInt()}% of exercise calories, creating an additional ${lossPercentage}% deficit for consistent fat loss while maintaining performance."
                            }
                            GoalType.GAIN_WEIGHT -> {
                                val eatBackPercentage = (selectedStrategy.weightGainExercisePercentage * 100).toInt()
                                if (selectedStrategy.weightGainAdditionalCalories > 0) {
                                    "You'll eat back ${eatBackPercentage}% of exercise calories plus ${selectedStrategy.weightGainAdditionalCalories} additional calories for moderate surplus and muscle growth."
                                } else {
                                    "You'll eat back ${eatBackPercentage}% of exercise calories for conservative, lean muscle growth."
                                }
                            }
                        }

                        Text(
                            text = "$baseText $goalSpecificText",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
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
                            val customValue = baseAdjustment.toIntOrNull() ?: selectedStrategy.granularityValue
                            onSave(customValue)
                            onDismiss()
                        },
                        enabled = isBaseAdjustmentValid
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}

@Composable
private fun StrategyChip(
    strategy: CalorieStrategy,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        onClick = onClick,
        label = {
            Column {
                Text(
                    text = strategy.displayName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                val displayValue = if (strategy.granularityValue == 0) "0" else strategy.granularityValue
                Text(
                    text = "$displayValue cal base adjustment",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = strategy.description,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        selected = isSelected,
        modifier = Modifier.fillMaxWidth()
    )
}
