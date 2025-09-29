package com.ralvin.pencatatankalori.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ralvin.pencatatankalori.health.model.ActivityLevel
import com.ralvin.pencatatankalori.health.model.GoalType
import com.ralvin.pencatatankalori.health.model.MifflinModel
import com.ralvin.pencatatankalori.viewmodel.OnboardingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingDialog(
    onDismiss: () -> Unit,
    onboardingViewModel: OnboardingViewModel
) {
    Dialog(onDismissRequest = onDismiss) {
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
    
    // Collect UI state
    val uiState by onboardingViewModel.uiState.collectAsStateWithLifecycle()
    
    // Validation state
    val isValid = age.isNotBlank() && age.toIntOrNull() != null && age.toInt() > 0 &&
                  weight.isNotBlank() && weight.toFloatOrNull() != null && weight.toFloat() > 0 &&
                  height.isNotBlank() && height.toFloatOrNull() != null && height.toFloat() > 0 &&
                  selectedActivityLevel != null &&
                  selectedGoalType != null

    fun handleSave() {
        if (isValid) {
            val ageInt = age.toInt()
            val weightFloat = weight.toFloat()
            val heightFloat = height.toFloat()
            val gender = if (isMale) "Male" else "Female"
            
            // Calculate daily calorie target using Mifflin-St Jeor equation
            val dailyCalorieTarget = MifflinModel.calculateDailyCalories(
                weight = weightFloat,
                height = heightFloat,
                age = ageInt,
                isMale = isMale,
                activityLevel = selectedActivityLevel!!,
                goalType = selectedGoalType!!
            )
            
            onboardingViewModel.createUserData(
                name = "User", // Default name since it's not collected anymore
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
            text = "Personalize Your Data",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "This data is used to customize your daily calorie target.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 16.dp)
        )


        // Weight field
        OutlinedTextField(
            value = weight,
            onValueChange = { if (it.isEmpty() || it.toFloatOrNull() != null) weight = it },
            label = { Text("Body Weight") },
            suffix = { Text("kg") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            isError = weight.isBlank() || weight.toFloatOrNull() == null || weight.toFloatOrNull()!! <= 0
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Height field
        OutlinedTextField(
            value = height,
            onValueChange = { if (it.isEmpty() || it.toFloatOrNull() != null) height = it },
            label = { Text("Height") },
            suffix = { Text("cm") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            isError = height.isBlank() || height.toFloatOrNull() == null || height.toFloatOrNull()!! <= 0
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Age field
        OutlinedTextField(
            value = age,
            onValueChange = { if (it.all { c -> c.isDigit() }) age = it },
            label = { Text("Age") },
            suffix = { Text("years") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = age.isBlank() || age.toIntOrNull() == null || age.toInt() <= 0
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Gender selection
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
                Text("Male", modifier = Modifier.padding(start = 8.dp))
            }
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(selected = !isMale, onClick = { isMale = false })
                Text("Female", modifier = Modifier.padding(start = 8.dp))
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Activity Level dropdown
        ExposedDropdownMenuBox(
            expanded = expandedActivityLevel,
            onExpandedChange = { expandedActivityLevel = it },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = selectedActivityLevel?.getDisplayName() ?: "Select Activity Level",
                onValueChange = {},
                readOnly = true,
                label = { Text("Activity Level") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedActivityLevel) },
                modifier = Modifier.menuAnchor(),
                singleLine = true,
                isError = selectedActivityLevel == null
            )
            ExposedDropdownMenu(
                expanded = expandedActivityLevel,
                onDismissRequest = { expandedActivityLevel = false }
            ) {
                ActivityLevel.values().forEach { level ->
                    DropdownMenuItem(
                        text = {
                            Column {
                                Text(level.getDisplayName())
                                Text(level.getDescription(), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
        
        // Goal Type selection
        Text("Goal", style = MaterialTheme.typography.bodyMedium)
        GoalType.values().forEach { goal ->
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
                    Text(goal.getDescription(), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        
        // Show error if there's one
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
                onClick = { handleSave() },
                enabled = isValid && currentUiState !is com.ralvin.pencatatankalori.viewmodel.OnboardingUiState.Loading
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