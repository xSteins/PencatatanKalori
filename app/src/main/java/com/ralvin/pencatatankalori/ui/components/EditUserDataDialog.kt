package com.ralvin.pencatatankalori.ui.components

import androidx.compose.foundation.clickable
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

enum class EditUserDataType(val title: String) {
    WEIGHT("Edit Weight"),
    HEIGHT("Edit Height"),
    AGE("Edit Age"),
    GENDER("Edit Gender"),
    ACTIVE_LEVEL("Edit Active Level"),
    GOAL("Edit Goal")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditUserDataDialog(
    editType: EditUserDataType,
    currentValue: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var textFieldValue by remember { mutableStateOf(currentValue) }
    var selectedGender by remember { mutableStateOf(if (editType == EditUserDataType.GENDER) currentValue else "Male") }
    var selectedActivityLevel by remember { mutableStateOf(if(editType == EditUserDataType.ACTIVE_LEVEL) ActivityLevel.values().find { it.name.equals(currentValue, ignoreCase = true) } else null) }
    var expandedActivityLevel by remember { mutableStateOf(false) }
    var selectedGoalType by remember { mutableStateOf(if(editType == EditUserDataType.GOAL) GoalType.values().find { it.name.equals(currentValue, ignoreCase = true) } else null) }
    var expandedGoalType by remember { mutableStateOf(false) }


    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = MaterialTheme.shapes.large
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(editType.title, style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(bottom = 16.dp))

                when (editType) {
                    EditUserDataType.WEIGHT, EditUserDataType.HEIGHT, EditUserDataType.AGE -> {
                        OutlinedTextField(
                            value = textFieldValue,
                            onValueChange = { if (it.all { c -> c.isDigit() }) textFieldValue = it },
                            label = { Text(editType.name.lowercase().replaceFirstChar { it.titlecase() }) },
                            suffix = { Text(if (editType == EditUserDataType.WEIGHT) "kg" else if (editType == EditUserDataType.HEIGHT) "cm" else "years") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }
                    EditUserDataType.GENDER -> {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f).clickable { selectedGender = "Male" }) {
                                RadioButton(selected = selectedGender == "Male", onClick = { selectedGender = "Male" })
                                Text("Male")
                            }
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f).clickable { selectedGender = "Female" }) {
                                RadioButton(selected = selectedGender == "Female", onClick = { selectedGender = "Female" })
                                Text("Female")
                            }
                        }
                    }
                    EditUserDataType.ACTIVE_LEVEL -> {
                        ExposedDropdownMenuBox(
                            expanded = expandedActivityLevel,
                            onExpandedChange = { expandedActivityLevel = !expandedActivityLevel },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = selectedActivityLevel?.getDisplayName() ?: "Select Active Level",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Active Level") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedActivityLevel) },
                                modifier = Modifier.menuAnchor().fillMaxWidth(),
                                singleLine = true
                            )
                            ExposedDropdownMenu(
                                expanded = expandedActivityLevel,
                                onDismissRequest = { expandedActivityLevel = false }
                            ) {
                                ActivityLevel.values().forEach { level ->
                                    DropdownMenuItem(
                                        text = { Text(level.getDisplayName()) },
                                        onClick = {
                                            selectedActivityLevel = level
                                            expandedActivityLevel = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                    EditUserDataType.GOAL -> {
                        ExposedDropdownMenuBox(
                            expanded = expandedGoalType,
                            onExpandedChange = { expandedGoalType = !expandedGoalType },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = selectedGoalType?.getDisplayName() ?: "Select Goal",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Goal") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedGoalType) },
                                modifier = Modifier.menuAnchor().fillMaxWidth(),
                                singleLine = true
                            )
                            ExposedDropdownMenu(
                                expanded = expandedGoalType,
                                onDismissRequest = { expandedGoalType = false }
                            ) {
                                GoalType.values().forEach { goal ->
                                    DropdownMenuItem(
                                        text = { Text(goal.getDisplayName()) },
                                        onClick = {
                                            selectedGoalType = goal
                                            expandedGoalType = false
                                        }
                                    )
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
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        val result = when (editType) {
                            EditUserDataType.WEIGHT, EditUserDataType.HEIGHT, EditUserDataType.AGE -> textFieldValue
                            EditUserDataType.GENDER -> selectedGender
                            EditUserDataType.ACTIVE_LEVEL -> selectedActivityLevel?.name ?: ""
                            EditUserDataType.GOAL -> selectedGoalType?.name ?: ""
                        }
                        onSave(result)
                    }) { Text("Save") }
                }
            }
        }
    }
} 