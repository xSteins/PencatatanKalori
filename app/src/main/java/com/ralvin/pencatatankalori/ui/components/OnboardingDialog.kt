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
import com.ralvin.pencatatankalori.health.model.ActivityLevel
import com.ralvin.pencatatankalori.health.model.GoalType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = MaterialTheme.shapes.large,
            tonalElevation = 8.dp
        ) {
            OnboardingScreenContent(onDismiss = onDismiss)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreenContent(onDismiss: () -> Unit) {
    var age by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var isMale by remember { mutableStateOf(true) }
    var selectedActivityLevel by remember { mutableStateOf<ActivityLevel?>(null) }
    var selectedGoalType by remember { mutableStateOf<GoalType?>(null) }
    var expandedActivityLevel by remember { mutableStateOf(false) }

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

        OutlinedTextField(
            value = weight,
            onValueChange = { if (it.all { c -> c.isDigit() }) weight = it },
            label = { Text("Body Weight") },
            suffix = { Text("kg") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        OutlinedTextField(
            value = height,
            onValueChange = { if (it.all { c -> c.isDigit() }) height = it },
            label = { Text("Height") },
            suffix = { Text("cm") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        OutlinedTextField(
            value = age,
            onValueChange = { if (it.all { c -> c.isDigit() }) age = it },
            label = { Text("Age") },
            suffix = { Text("years") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(8.dp))
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
        Spacer(modifier = Modifier.height(8.dp))
//        Text("Activity Level", style = MaterialTheme.typography.bodyMedium)
        ExposedDropdownMenuBox(
            expanded = expandedActivityLevel,
            onExpandedChange = { expandedActivityLevel = it },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = selectedActivityLevel?.getDisplayName() ?: "Select Activity Level",
                onValueChange = {},
                readOnly = true,
                label = { Text("Select Activity Level") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedActivityLevel) },
                modifier = Modifier.menuAnchor(),
                singleLine = true
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
        Spacer(modifier = Modifier.height(8.dp))
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
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onDismiss) { Text("Cancel") }
            Button(onClick = onDismiss) { Text("Save") }
        }
    }
} 