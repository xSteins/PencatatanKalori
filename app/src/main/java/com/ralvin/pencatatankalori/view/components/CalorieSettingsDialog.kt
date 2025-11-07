package com.ralvin.pencatatankalori.view.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
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
import androidx.compose.ui.unit.dp

import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.ralvin.pencatatankalori.R
import com.ralvin.pencatatankalori.model.formula.ActivityLevel
import com.ralvin.pencatatankalori.model.formula.CalorieStrategy
import com.ralvin.pencatatankalori.model.formula.GoalType
import com.ralvin.pencatatankalori.model.formula.MifflinModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalorieSettingsDialog(
	onDismiss: () -> Unit,
	onSave: (granularityValue: Int) -> Unit,
	goalType: GoalType,
	userWeight: Double = 70.0,
	userHeight: Double = 170.0,
	userAge: Int = 25,
	isMale: Boolean = true,
	activityLevel: ActivityLevel = ActivityLevel.MODERATELY_ACTIVE,
	initialGranularityValue: Int = 250
) {
	// Setting 1: Granularity Value Slider (0-500), initialized with database value
	var granularityValue by remember { mutableStateOf(initialGranularityValue) }

	Dialog(
		onDismissRequest = onDismiss,
		properties = DialogProperties(usePlatformDefaultWidth = false)
	) {
		Surface(
			modifier = Modifier
				.fillMaxWidth()
				.padding(16.dp),
			shape = MaterialTheme.shapes.large,
			tonalElevation = 8.dp
		) {
			Column(
				modifier = Modifier
					.padding(16.dp)
					.verticalScroll(rememberScrollState()),
				horizontalAlignment = Alignment.Start
			) {
				Text(
					text = "Calorie Strategy Settings",
					style = MaterialTheme.typography.headlineSmall,
					fontWeight = FontWeight.Bold,
				)

				Spacer(modifier = Modifier.height(12.dp))

				Text(
					text = "Adjust your daily calorie need",
					style = MaterialTheme.typography.bodyLarge,
					fontWeight = FontWeight.Medium
				)

				Spacer(modifier = Modifier.height(6.dp))

				Row(
					verticalAlignment = Alignment.CenterVertically,
					modifier = Modifier.fillMaxWidth()
				) {
					Text("0", style = MaterialTheme.typography.bodySmall)
					Slider(
						value = granularityValue.toFloat(),
						onValueChange = { granularityValue = it.toInt() },
						valueRange = 0f..500f,
						steps = 49, // 10 calorie increments
						modifier = Modifier
							.weight(1f)
							.padding(horizontal = 12.dp)
					)
					Text("500", style = MaterialTheme.typography.bodySmall)
				}

				Text(
					text = "Current: $granularityValue calories",
					style = MaterialTheme.typography.bodyMedium,
					fontWeight = FontWeight.Medium,
					modifier = Modifier.padding(bottom = 12.dp)
				)

				Card(
					modifier = Modifier.fillMaxWidth(),
					colors = CardDefaults.cardColors(
						containerColor = MaterialTheme.colorScheme.secondaryContainer
					)
				) {
					Text(
						text = MifflinModel.getCalorieAdjustmentExplanation(
							goalType = goalType,
							weight = userWeight,
							height = userHeight,
							age = userAge,
							isMale = isMale,
							activityLevel = activityLevel,
							granularityValue = granularityValue,
							strategy = CalorieStrategy.MODERATE,
							advancedEnabled = false
						),
						style = MaterialTheme.typography.bodySmall,
						modifier = Modifier.padding(10.dp)
					)
				}

				Spacer(modifier = Modifier.height(16.dp))

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
							onSave(granularityValue)
						}
					) {
						Text("Save")
					}
				}
			}
		}
	}
}
