package com.ralvin.pencatatankalori.view.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ralvin.pencatatankalori.model.formula.ActivityLevel
import com.ralvin.pencatatankalori.model.formula.GoalType
import com.ralvin.pencatatankalori.model.formula.MifflinModel
import com.ralvin.pencatatankalori.viewmodel.OnboardingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingDialog(
	onDismiss: () -> Unit,
	onboardingViewModel: OnboardingViewModel
) {
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
			OnboardingScreenContent(
				onDismiss = onDismiss,
				onboardingViewModel = onboardingViewModel
			)
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreenContent(
	onDismiss: () -> Unit,
	onboardingViewModel: OnboardingViewModel
) {
	var age by remember { mutableStateOf("") }
	var weight by remember { mutableStateOf("") }
	var height by remember { mutableStateOf("") }
	var isMale by remember { mutableStateOf(true) }
	var selectedActivityLevel by remember { mutableStateOf<ActivityLevel?>(null) }
	var selectedGoalType by remember { mutableStateOf<GoalType?>(null) }
	var expandedActivityLevel by remember { mutableStateOf(false) }
	var hasAttemptedSave by remember { mutableStateOf(false) }

	val uiState by onboardingViewModel.uiState.collectAsStateWithLifecycle()

	LaunchedEffect(uiState) {
		if (uiState is com.ralvin.pencatatankalori.viewmodel.OnboardingUiState.Success) {
			onDismiss()
		}
	}

	val isWeightValid = weight.isNotBlank() && weight.toFloatOrNull() != null && weight.toFloat() > 0
	val isHeightValid = height.isNotBlank() && height.toFloatOrNull() != null && height.toFloat() > 0
	val isAgeValid = age.isNotBlank() && age.toIntOrNull() != null && age.toInt() > 0
	val isActivityLevelValid = selectedActivityLevel != null
	val isGoalTypeValid = selectedGoalType != null

	val isValid = isWeightValid && isHeightValid && isAgeValid && isActivityLevelValid && isGoalTypeValid

	fun handleSave() {
		hasAttemptedSave = true
		if (isValid) {
			val ageInt = age.toInt()
			val weightFloat = weight.toFloat()
			val heightFloat = height.toFloat()
			val gender = if (isMale) "Male" else "Female"

			val dailyCalorieTarget = MifflinModel.calculateDailyCalories(
				weight = weightFloat,
				height = heightFloat,
				age = ageInt,
				isMale = isMale,
				activityLevel = selectedActivityLevel!!,
				goalType = selectedGoalType!!
			)

			onboardingViewModel.createUserData(
				age = ageInt,
				gender = gender,
				weight = weightFloat,
				height = heightFloat,
				activityLevel = selectedActivityLevel!!,
				goalType = selectedGoalType!!,
				dailyCalorieTarget = dailyCalorieTarget
			)
		}
	}

	Column(
		modifier = Modifier
			.fillMaxWidth()
			.padding(16.dp)
			.verticalScroll(rememberScrollState()),
		horizontalAlignment = Alignment.Start
	) {
		Text(
			text = "Pengisian data pribadi",
			style = MaterialTheme.typography.headlineSmall,
			modifier = Modifier.padding(bottom = 8.dp)
		)
		Text(
			text = "Data pribadi akan digunakan untuk menghitung target kalori harian. \nPerhitungan kalori menggunakan persamaan Mifflin & St. Jeor",
			style = MaterialTheme.typography.bodyMedium,
			color = MaterialTheme.colorScheme.onSurfaceVariant,
			modifier = Modifier.padding(bottom = 16.dp)
		)


		OutlinedTextField(
			value = weight,
			onValueChange = { if (it.isEmpty() || it.toFloatOrNull() != null) weight = it },
			label = { Text("Berat Badan") },
			suffix = { Text("kg") },
			modifier = Modifier.fillMaxWidth(),
			singleLine = true,
			keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
			isError = hasAttemptedSave && !isWeightValid,
			supportingText = {
				if (hasAttemptedSave && !isWeightValid) {
					Text("Data tidak boleh kosong")
				}
			}
		)

		Spacer(modifier = Modifier.height(8.dp))

		OutlinedTextField(
			value = height,
			onValueChange = { if (it.isEmpty() || it.toFloatOrNull() != null) height = it },
			label = { Text("Tinggi Badan") },
			suffix = { Text("cm") },
			modifier = Modifier.fillMaxWidth(),
			singleLine = true,
			keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
			isError = hasAttemptedSave && !isHeightValid,
			supportingText = {
				if (hasAttemptedSave && !isHeightValid) {
					Text("Data tidak boleh kosong")
				}
			}
		)

		Spacer(modifier = Modifier.height(8.dp))

		OutlinedTextField(
			value = age,
			onValueChange = { if (it.all { c -> c.isDigit() }) age = it },
			label = { Text("Usia") },
			suffix = { Text("tahun") },
			modifier = Modifier.fillMaxWidth(),
			singleLine = true,
			keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
			isError = hasAttemptedSave && !isAgeValid,
			supportingText = {
				if (hasAttemptedSave && !isAgeValid) {
					Text("Data tidak boleh kosong")
				}
			}
		)

		Spacer(modifier = Modifier.height(16.dp))

		Text("Gender", style = MaterialTheme.typography.bodyMedium)
		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.spacedBy(16.dp)
		) {
			Row(
				modifier = Modifier.weight(1f),
				verticalAlignment = Alignment.CenterVertically
			) {
				RadioButton(selected = isMale, onClick = { isMale = true })
				Text("Laki-Laki", modifier = Modifier.padding(start = 8.dp))
			}
			Row(
				modifier = Modifier.weight(1f),
				verticalAlignment = Alignment.CenterVertically
			) {
				RadioButton(selected = !isMale, onClick = { isMale = false })
				Text("Perempuan", modifier = Modifier.padding(start = 8.dp))
			}
		}

		Spacer(modifier = Modifier.height(16.dp))

		ExposedDropdownMenuBox(
			expanded = expandedActivityLevel,
			onExpandedChange = { expandedActivityLevel = it },
			modifier = Modifier.fillMaxWidth()
		) {
			OutlinedTextField(
				value = selectedActivityLevel?.getDisplayName() ?: "Pilih tingkat keaktifan",
				onValueChange = {},
				readOnly = true,
				label = { Text("Tingkat Keaktifan") },
				trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedActivityLevel) },
				modifier = Modifier
					.menuAnchor(MenuAnchorType.PrimaryEditable, enabled = true)
					.fillMaxWidth(),
				singleLine = false,
				isError = hasAttemptedSave && !isActivityLevelValid,
				supportingText = {
					if (hasAttemptedSave && !isActivityLevelValid) {
						Text("Data tidak boleh kosong")
					}
				}
			)
			ExposedDropdownMenu(
				expanded = expandedActivityLevel,
				onDismissRequest = { expandedActivityLevel = false }
			) {
				ActivityLevel.entries.forEach { level ->
					DropdownMenuItem(
						text = {
							Column {
								Text(level.getDisplayName())
								Text(
									level.getDescription(),
									style = MaterialTheme.typography.bodySmall,
									color = MaterialTheme.colorScheme.onSurfaceVariant
								)
							}
						},
						onClick = {
							selectedActivityLevel = level
							expandedActivityLevel = false
						}
					)
				}
			}
		}

		Spacer(modifier = Modifier.height(16.dp))

		Text("Goal", style = MaterialTheme.typography.bodyMedium)
		GoalType.entries.forEach { goal ->
			Row(
				modifier = Modifier.fillMaxWidth(),
				verticalAlignment = Alignment.CenterVertically
			) {
				RadioButton(
					selected = selectedGoalType == goal,
					onClick = { selectedGoalType = goal }
				)
				Column {
					Text(goal.getDisplayName())
					Text(
						goal.getDescription(),
						style = MaterialTheme.typography.bodySmall,
						color = MaterialTheme.colorScheme.onSurfaceVariant
					)
				}
			}
		}

		if (hasAttemptedSave && !isGoalTypeValid) {
			Text(
				text = "Data tidak boleh kosong",
				color = MaterialTheme.colorScheme.error,
				style = MaterialTheme.typography.bodySmall,
				modifier = Modifier.padding(start = 16.dp)
			)
		}

		val currentUiState = uiState
		if (currentUiState is com.ralvin.pencatatankalori.viewmodel.OnboardingUiState.Error) {
			Spacer(modifier = Modifier.height(8.dp))
			Text(
				text = currentUiState.message,
				color = MaterialTheme.colorScheme.error,
				style = MaterialTheme.typography.bodySmall
			)
		}

		Spacer(modifier = Modifier.height(16.dp))

		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.End
		) {
			TextButton(onClick = onDismiss) {
				Text("Cancel")
			}
			Spacer(modifier = Modifier.width(8.dp))
			Button(
				onClick = { handleSave() },
				enabled = (!hasAttemptedSave || isValid) && currentUiState !is com.ralvin.pencatatankalori.viewmodel.OnboardingUiState.Loading
			) {
				if (currentUiState is com.ralvin.pencatatankalori.viewmodel.OnboardingUiState.Loading) {
					CircularProgressIndicator(modifier = Modifier.size(16.dp))
				} else {
					Text("Save")
				}
			}
		}
	}
}
