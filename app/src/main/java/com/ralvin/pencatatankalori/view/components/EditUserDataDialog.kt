package com.ralvin.pencatatankalori.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.ralvin.pencatatankalori.model.formula.ActivityLevel
import com.ralvin.pencatatankalori.model.formula.GoalType
import com.ralvin.pencatatankalori.R

enum class EditUserDataType {
	WEIGHT,
	HEIGHT,
	AGE,
	GENDER,
	ACTIVE_LEVEL,
	GOAL
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditUserDataDialog(
	editType: EditUserDataType,
	currentValue: String,
	onDismiss: () -> Unit,
	onSave: (String) -> Unit
) {
	val titleText = when (editType) {
		EditUserDataType.WEIGHT -> "Edit Weight"
		EditUserDataType.HEIGHT -> "Edit Height"
		EditUserDataType.AGE -> "Edit Age"
		EditUserDataType.GENDER -> "Edit Gender"
		EditUserDataType.ACTIVE_LEVEL -> "Edit Active Level"
		EditUserDataType.GOAL -> "Edit Goal"
	}

	var textFieldValue by remember { mutableStateOf(currentValue) }
	var selectedGender by remember { mutableStateOf(if (editType == EditUserDataType.GENDER) currentValue else "Male") }
	var selectedActivityLevel by remember {
		mutableStateOf(
			if (editType == EditUserDataType.ACTIVE_LEVEL) ActivityLevel.values()
				.find { it.getDisplayName() == currentValue } else null)
	}
	var expandedActivityLevel by remember { mutableStateOf(false) }
	var selectedGoalType by remember {
		mutableStateOf(
			if (editType == EditUserDataType.GOAL) GoalType.values()
				.find { it.getDisplayName() == currentValue } else null)
	}
	var expandedGoalType by remember { mutableStateOf(false) }


	Dialog(onDismissRequest = onDismiss) {
		Card(
			modifier = Modifier
				.fillMaxWidth()
				.widthIn(min = 320.dp)
				.padding(16.dp),
			shape = MaterialTheme.shapes.large
		) {
			Column(
				modifier = Modifier
					.padding(16.dp)
					.verticalScroll(rememberScrollState())
			) {
				Text(
					titleText,
					style = MaterialTheme.typography.headlineSmall,
					modifier = Modifier.padding(bottom = 16.dp)
				)

				when (editType) {
					EditUserDataType.WEIGHT, EditUserDataType.HEIGHT, EditUserDataType.AGE -> {
						val isDecimalAllowed =
							editType == EditUserDataType.WEIGHT || editType == EditUserDataType.HEIGHT
						val unit =
							if (editType == EditUserDataType.WEIGHT) "kg" else if (editType == EditUserDataType.HEIGHT) "cm" else "years"
						val increment = 1f
						val minValue = when (editType) {
							EditUserDataType.WEIGHT -> 1f
							EditUserDataType.HEIGHT -> 50f
							EditUserDataType.AGE -> 1f
							else -> 0f
						}
						val maxValue = when (editType) {
							EditUserDataType.WEIGHT -> 300f
							EditUserDataType.HEIGHT -> 250f
							EditUserDataType.AGE -> 120f
							else -> 999f
						}

						Column(
							horizontalAlignment = Alignment.CenterHorizontally,
							modifier = Modifier.fillMaxWidth()
						) {
							Row(
								verticalAlignment = Alignment.CenterVertically,
								horizontalArrangement = Arrangement.spacedBy(16.dp),
								modifier = Modifier.padding(vertical = 16.dp)
							) {
								IconButton(
									onClick = {
										val currentVal = textFieldValue.toFloatOrNull()
											?: (if (editType == EditUserDataType.AGE) 25f else 70f)
										val newValue = currentVal - increment
										if (newValue >= minValue) {
											textFieldValue =
												if (editType == EditUserDataType.AGE) newValue.toInt()
													.toString() else String.format("%.1f", newValue)
										}
									},
									modifier = Modifier
										.size(40.dp)
										.clip(CircleShape)
										.background(MaterialTheme.colorScheme.surfaceVariant)
								) {
									Icon(
										Icons.Filled.Remove,
										contentDescription = "Decrease",
										tint = MaterialTheme.colorScheme.onSurfaceVariant,
										modifier = Modifier.size(20.dp)
									)
								}

								OutlinedTextField(
									value = textFieldValue,
									onValueChange = { newValue ->
										if (isDecimalAllowed) {
											if (newValue.isEmpty() || newValue.toFloatOrNull() != null) {
												textFieldValue = newValue
											}
										} else {
											if (newValue.isEmpty() || newValue.all { c -> c.isDigit() }) {
												textFieldValue = newValue
											}
										}
									},
									label = {
										Text(
											editType.name.lowercase()
												.replaceFirstChar { it.titlecase() })
									},
									suffix = { Text(unit) },
									modifier = Modifier
										.width(120.dp)
										.padding(horizontal = 8.dp),
									singleLine = true,
									keyboardOptions = KeyboardOptions(keyboardType = if (isDecimalAllowed) KeyboardType.Decimal else KeyboardType.Number)
								)

								IconButton(
									onClick = {
										val currentVal = textFieldValue.toFloatOrNull()
											?: (if (editType == EditUserDataType.AGE) 25f else 70f)
										val newValue = currentVal + increment
										if (newValue <= maxValue) {
											textFieldValue =
												if (editType == EditUserDataType.AGE) newValue.toInt()
													.toString() else String.format("%.1f", newValue)
										}
									},
									modifier = Modifier
										.size(40.dp)
										.clip(CircleShape)
										.background(MaterialTheme.colorScheme.primary)
								) {
									Icon(
										Icons.Filled.Add,
										contentDescription = "Increase",
										tint = MaterialTheme.colorScheme.onPrimary,
										modifier = Modifier.size(20.dp)
									)
								}
							}
						}
					}

					EditUserDataType.GENDER -> {
						Row(modifier = Modifier.fillMaxWidth()) {
							Row(
								verticalAlignment = Alignment.CenterVertically,
								modifier = Modifier
									.weight(1f)
									.clickable { selectedGender = "Male" }) {
								RadioButton(
									selected = selectedGender == "Male",
									onClick = { selectedGender = "Male" })
								Text(stringResource(R.string.male), style = MaterialTheme.typography.bodyLarge)
							}
							Row(
								verticalAlignment = Alignment.CenterVertically,
								modifier = Modifier
									.weight(1f)
									.clickable { selectedGender = "Female" }) {
								RadioButton(
									selected = selectedGender == "Female",
									onClick = { selectedGender = "Female" })
								Text(stringResource(R.string.female), style = MaterialTheme.typography.bodyLarge)
							}
						}
					}

					EditUserDataType.ACTIVE_LEVEL -> {
						Column(modifier = Modifier
							.fillMaxWidth()
							.padding(vertical = 16.dp)) {
							ExposedDropdownMenuBox(
								expanded = expandedActivityLevel,
								onExpandedChange = {
									expandedActivityLevel = !expandedActivityLevel
								},
								modifier = Modifier.fillMaxWidth()
							) {
								OutlinedTextField(
									value = selectedActivityLevel?.getDisplayName()
										?: stringResource(R.string.select_activity_level),
									onValueChange = {},
									readOnly = true,
									label = { Text(stringResource(R.string.activity_level)) },
									trailingIcon = {
										ExposedDropdownMenuDefaults.TrailingIcon(
											expanded = expandedActivityLevel
										)
									},
									modifier = Modifier
										.menuAnchor()
										.fillMaxWidth(),
									singleLine = true
								)
								ExposedDropdownMenu(
									expanded = expandedActivityLevel,
									onDismissRequest = { expandedActivityLevel = false },
									modifier = Modifier
										.fillMaxWidth()
										.heightIn(max = 300.dp)
								) {
									ActivityLevel.values().forEach { level ->
										DropdownMenuItem(
											text = {
												Column(modifier = Modifier.fillMaxWidth()) {
													Text(
														level.getDisplayName(),
														style = MaterialTheme.typography.bodyLarge,
														fontWeight = FontWeight.Medium
													)
													Text(
														level.getDescription(),
														style = MaterialTheme.typography.bodySmall,
														color = MaterialTheme.colorScheme.onSurfaceVariant,
														modifier = Modifier.padding(top = 2.dp)
													)
												}
											},
											onClick = {
												selectedActivityLevel = level
												expandedActivityLevel = false
											},
											modifier = Modifier.fillMaxWidth()
										)
									}
								}
							}
						}
					}

					EditUserDataType.GOAL -> {
						Column(modifier = Modifier.fillMaxWidth()) {
							ExposedDropdownMenuBox(
								expanded = expandedGoalType,
								onExpandedChange = { expandedGoalType = !expandedGoalType },
								modifier = Modifier.fillMaxWidth()
							) {
								OutlinedTextField(
									value = selectedGoalType?.getDisplayName() ?: stringResource(R.string.goal),
									onValueChange = {},
									readOnly = true,
									label = { Text(stringResource(R.string.goal)) },
									trailingIcon = {
										ExposedDropdownMenuDefaults.TrailingIcon(
											expanded = expandedGoalType
										)
									},
									modifier = Modifier
										.menuAnchor()
										.fillMaxWidth(),
									singleLine = true
								)
								ExposedDropdownMenu(
									expanded = expandedGoalType,
									onDismissRequest = { expandedGoalType = false },
									modifier = Modifier
										.fillMaxWidth()
										.heightIn(max = 200.dp)
								) {
									GoalType.values().forEach { goal ->
										DropdownMenuItem(
											text = {
												Column(modifier = Modifier.fillMaxWidth()) {
													Text(
														goal.getDisplayName(),
														style = MaterialTheme.typography.bodyLarge,
														fontWeight = FontWeight.Medium
													)
													Text(
														goal.getDescription(),
														style = MaterialTheme.typography.bodySmall,
														color = MaterialTheme.colorScheme.onSurfaceVariant,
														modifier = Modifier.padding(top = 2.dp)
													)
												}
											},
											onClick = {
												selectedGoalType = goal
												expandedGoalType = false
											},
											modifier = Modifier.fillMaxWidth()
										)
									}
								}
							}
						}
					}
				}
				Spacer(modifier = Modifier.height(24.dp))
				Row(
					modifier = Modifier.fillMaxWidth(),
					horizontalArrangement = Arrangement.End
				) {
					TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
					Spacer(modifier = Modifier.width(8.dp))
					Button(onClick = {
						val result = when (editType) {
							EditUserDataType.WEIGHT, EditUserDataType.HEIGHT, EditUserDataType.AGE -> textFieldValue
							EditUserDataType.GENDER -> selectedGender
							EditUserDataType.ACTIVE_LEVEL -> selectedActivityLevel?.getDisplayName()
								?: ""

							EditUserDataType.GOAL -> selectedGoalType?.getDisplayName() ?: ""
						}
						onSave(result)
					}) { Text(stringResource(R.string.save)) }
				}
			}
		}
	}
}
