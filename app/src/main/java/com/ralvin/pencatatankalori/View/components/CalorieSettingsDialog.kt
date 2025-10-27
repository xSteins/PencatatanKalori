package com.ralvin.pencatatankalori.View.components

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.ralvin.pencatatankalori.R
import com.ralvin.pencatatankalori.Model.formula.ActivityLevel
import com.ralvin.pencatatankalori.Model.formula.CalorieStrategy
import com.ralvin.pencatatankalori.Model.formula.GoalType
import com.ralvin.pencatatankalori.Model.formula.MifflinModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalorieSettingsDialog(
	onDismiss: () -> Unit,
	onSave: (granularityValue: Int, strategy: CalorieStrategy?, advancedEnabled: Boolean) -> Unit,
	goalType: GoalType,
	userWeight: Double = 70.0,
	userHeight: Double = 170.0,
	userAge: Int = 25,
	isMale: Boolean = true,
	activityLevel: ActivityLevel = ActivityLevel.MODERATELY_ACTIVE,
	initialGranularityValue: Int = 250,
	initialCalorieStrategy: CalorieStrategy = CalorieStrategy.MODERATE,
	initialAdvancedEnabled: Boolean = false
) {
	// Setting 1: Granularity Value Slider (0-500), initialized with database value
	var granularityValue by remember { mutableStateOf(initialGranularityValue) }

	// Setting 3: Enable Advanced checkbox/toggle, initialized with database value
	var advancedEnabled by remember { mutableStateOf(initialAdvancedEnabled) }

	// Setting 2: Advanced Strategy options, initialized with database value
	var selectedStrategy by remember { mutableStateOf(initialCalorieStrategy) }

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
					text = stringResource(R.string.calorie_strategy_settings),
					style = MaterialTheme.typography.headlineSmall,
					fontWeight = FontWeight.Bold,
				)

				Spacer(modifier = Modifier.height(12.dp))

				Text(
					text = stringResource(R.string.adjust_daily_calorie_need),
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
					text = stringResource(R.string.current_calories, granularityValue),
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
							strategy = selectedStrategy,
							advancedEnabled = advancedEnabled
						),
						style = MaterialTheme.typography.bodySmall,
						modifier = Modifier.padding(10.dp)
					)
				}

				Spacer(modifier = Modifier.height(12.dp))
				Row(
					modifier = Modifier.fillMaxWidth(),
					verticalAlignment = Alignment.CenterVertically
				) {
					Checkbox(
						checked = advancedEnabled,
						onCheckedChange = { advancedEnabled = it }
					)
					Text(
						text = stringResource(R.string.enable_advanced_exercise),
						style = MaterialTheme.typography.bodyLarge,
						modifier = Modifier.padding(start = 8.dp)
					)
				}

				Spacer(modifier = Modifier.height(12.dp))

				// Setting 2: Advanced Options (disabled when advanced is off)
				Card(
					modifier = Modifier.fillMaxWidth(),
					colors = CardDefaults.cardColors(
						containerColor = if (advancedEnabled)
							MaterialTheme.colorScheme.surface
						else
							MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
					)
				) {
					Column(
						modifier = Modifier.padding(12.dp)
					) {
						Text(
							text = stringResource(R.string.advanced_exercise_strategy),
							style = MaterialTheme.typography.bodyLarge,
							fontWeight = FontWeight.Medium,
							color = if (advancedEnabled)
								MaterialTheme.colorScheme.onSurface
							else
								MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
						)

						Spacer(modifier = Modifier.height(8.dp))

						CalorieStrategy.values().forEach { strategy ->
							Row(
								modifier = Modifier
									.fillMaxWidth()
									.padding(vertical = 2.dp),
								verticalAlignment = Alignment.CenterVertically
							) {
								RadioButton(
									selected = selectedStrategy == strategy,
									onClick = {
										if (advancedEnabled) {
											selectedStrategy = strategy
										}
									},
									enabled = advancedEnabled
								)
								Column(
									modifier = Modifier.padding(start = 8.dp)
								) {
									Text(
										text = strategy.displayName,
										style = MaterialTheme.typography.bodyMedium,
										fontWeight = FontWeight.Medium,
										color = if (advancedEnabled)
											MaterialTheme.colorScheme.onSurface
										else
											MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
									)
									Text(
										text = strategy.description,
										style = MaterialTheme.typography.bodySmall,
										color = if (advancedEnabled)
											MaterialTheme.colorScheme.onSurfaceVariant
										else
											MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
									)
									val goalSpecificText = when (goalType) {
										GoalType.LOSE_WEIGHT -> {
											val lossPercentage =
												((1.0 - strategy.weightLossExercisePercentage) * 100).toInt()
											stringResource(R.string.eat_back_deficit_description, (strategy.weightLossExercisePercentage * 100).toInt(), lossPercentage)
										}

										GoalType.GAIN_WEIGHT -> {
											val eatBackPercentage =
												(strategy.weightGainExercisePercentage * 100).toInt()
											if (strategy.weightGainAdditionalCalories > 0) {
												stringResource(R.string.eat_back_surplus_description, eatBackPercentage, strategy.weightGainAdditionalCalories)
											} else {
												stringResource(R.string.eat_back_conservative_description, eatBackPercentage)
											}
										}
									}
									Text(
										text = goalSpecificText,
										style = MaterialTheme.typography.labelSmall,
										color = if (advancedEnabled)
											MaterialTheme.colorScheme.onSurfaceVariant
										else
											MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
									)
								}
							}
						}
					}
				}

				Spacer(modifier = Modifier.height(16.dp))

				// Action buttons
				Row(
					modifier = Modifier.fillMaxWidth(),
					horizontalArrangement = Arrangement.End
				) {
					TextButton(onClick = onDismiss) {
						Text(stringResource(R.string.cancel))
					}
					Spacer(modifier = Modifier.width(8.dp))
					Button(
						onClick = {
							onSave(
								granularityValue,
								if (advancedEnabled) selectedStrategy else null,
								advancedEnabled
							)
						}
					) {
						Text(stringResource(R.string.save))
					}
				}
			}
		}
	}
}