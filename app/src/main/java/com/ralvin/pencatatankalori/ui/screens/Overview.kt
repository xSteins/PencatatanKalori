package com.ralvin.pencatatankalori.ui.screens

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ralvin.pencatatankalori.ui.components.AddOrEditLogModal
import com.ralvin.pencatatankalori.ui.components.LogType
import com.ralvin.pencatatankalori.ui.theme.PencatatanKaloriTheme
import com.ralvin.pencatatankalori.viewmodel.OverviewViewModel
import com.ralvin.pencatatankalori.data.database.entities.ActivityLog
import com.ralvin.pencatatankalori.data.database.entities.ActivityType
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun OverviewScreen(
    viewModel: OverviewViewModel = hiltViewModel()
) {
    var showLogModal by remember { mutableStateOf(false) }
    var modalType by remember { mutableStateOf(LogType.FOOD) }
    var editData by remember { mutableStateOf<ActivityLog?>(null) }

    // Collect data from ViewModel
    val overviewData by viewModel.overviewData.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    val currentDate = SimpleDateFormat("EEEE, dd MMMM", Locale.getDefault()).format(Date())
    
    // Get data from ViewModel or use defaults
    val todayConsumedCalorie = overviewData?.caloriesConsumed ?: 0
    val todayBurnedCalorie = overviewData?.caloriesBurned ?: 0
    val dailyCalorieTarget = overviewData?.user?.dailyCalorieTarget ?: 2000
    val remainingCalories = overviewData?.remainingCalories ?: dailyCalorieTarget
    val activities = overviewData?.todayActivities ?: emptyList()
    
    // BMI calculations
    val user = overviewData?.user
    val bmiValue = user?.let {
        val heightInMeters = it.height / 100
        if (heightInMeters > 0) it.weight / (heightInMeters * heightInMeters) else 0f
    } ?: 0f
    
    val bmiStatus = when {
        bmiValue == 0f -> "No data"
        bmiValue < 18.5 -> "Underweight"
        bmiValue < 25 -> "Normal"
        bmiValue < 30 -> "Overweight"
        else -> "Obese"
    }
    
    val bmiRange = "18.5 - 24.9"
    val bmiStatusColor = when {
        bmiValue == 0f -> Color.Gray
        bmiValue < 18.5 -> Color(0xFF2196F3) // Blue
        bmiValue < 25 -> Color(0xFF4CAF50) // Green
        bmiValue < 30 -> Color(0xFFFF9800) // Orange
        else -> Color(0xFFF44336) // Red
    }
    // Show loading or error states
    val currentUiState = uiState
    when (currentUiState) {
        is com.ralvin.pencatatankalori.viewmodel.OverviewUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return
        }
        is com.ralvin.pencatatankalori.viewmodel.OverviewUiState.Error -> {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Error: ${currentUiState.message}",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge
                )
                Button(
                    onClick = { /* TODO: Add retry logic */ },
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text("Retry")
                }
            }
            return
        }
        else -> {
            // Continue with success state
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(all = 16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = currentDate,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                CalorieInfoRow("Added Calories", todayConsumedCalorie, Color.Yellow)
                Spacer(modifier = Modifier.height(8.dp))
                CalorieInfoRow("Calories Burned", todayBurnedCalorie, Color.Green)
                Spacer(modifier = Modifier.height(8.dp))
                CalorieInfoRow("Remaining Calories", remainingCalories, Color.Magenta, dailyCalorieTarget)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // BMI Card
        BmiCard(
            bmiValue = bmiValue,
            bmiStatus = bmiStatus,
            bmiRange = bmiRange,
            statusColor = bmiStatusColor,
            onWeightUpdate = { newWeight ->
                viewModel.updateUserWeight(newWeight)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = {
                    modalType = LogType.FOOD
                    editData = null
                    showLogModal = true
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Food")
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add New Food Data")
            }
            Button(
                onClick = {
                    modalType = LogType.WORKOUT
                    editData = null
                    showLogModal = true
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Workout")
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Workout Data")
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Activities:",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        if (activities.isEmpty()) {
            Text(
                text = "You haven't logged anything",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 16.dp)
            )
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(activities) { activity ->
                     ActivityItemFromDB(
                         activity = activity,
                         onClick = { clickedActivity ->
                             modalType = if (clickedActivity.type == ActivityType.CONSUMPTION) LogType.FOOD else LogType.WORKOUT
                             editData = clickedActivity
                             showLogModal = true
                         }
                     )
                }
            }
        }
    }

    if (showLogModal) {
        Dialog(
            onDismissRequest = { showLogModal = false },
            properties = DialogProperties(dismissOnClickOutside = false, dismissOnBackPress = true)
        ) {
            AddOrEditLogModal(
                type = modalType,
                initialName = when (modalType) {
                    LogType.FOOD -> editData?.foodName ?: ""
                    LogType.WORKOUT -> editData?.workoutName ?: ""
                },
                initialCalories = editData?.calories?.toString() ?: "",
                initialProtein = editData?.protein?.toString() ?: "",
                initialCarbs = editData?.carbs?.toString() ?: "",
                initialPortion = editData?.portion ?: "",
                initialDuration = editData?.duration?.toString() ?: "",
                isEditMode = editData != null,
                onSubmit = { name, calories, protein, carbs, portion, duration ->
                    Log.d("OverviewScreen", "Log Submitted: Type: $modalType, Name: $name, Calories: $calories, EditData: $editData")
                    
                    val currentEditData = editData
                    if (currentEditData != null) {
                        // Update existing activity
                        val updatedActivity = currentEditData.copy(
                            calories = calories.toIntOrNull() ?: 0,
                            foodName = if (modalType == LogType.FOOD) name else currentEditData.foodName,
                            workoutName = if (modalType == LogType.WORKOUT) name else currentEditData.workoutName,
                            protein = if (modalType == LogType.FOOD) protein?.toFloatOrNull() ?: 0f else currentEditData.protein,
                            carbs = if (modalType == LogType.FOOD) carbs?.toFloatOrNull() ?: 0f else currentEditData.carbs,
                            portion = if (modalType == LogType.FOOD) portion ?: "" else currentEditData.portion,
                            duration = if (modalType == LogType.WORKOUT) duration?.toIntOrNull() ?: 0 else currentEditData.duration
                        )
                        viewModel.updateActivity(updatedActivity)
                    } else {
                        // Create new activity
                        when (modalType) {
                            LogType.FOOD -> {
                                viewModel.logFood(
                                    foodName = name,
                                    calories = calories.toIntOrNull() ?: 0,
                                    protein = protein?.toFloatOrNull() ?: 0f,
                                    carbs = carbs?.toFloatOrNull() ?: 0f,
                                    portion = portion ?: ""
                                )
                            }
                            LogType.WORKOUT -> {
                                viewModel.logWorkout(
                                    workoutName = name,
                                    caloriesBurned = calories.toIntOrNull() ?: 0,
                                    duration = duration?.toIntOrNull() ?: 0
                                )
                            }
                        }
                    }
                    showLogModal = false
                    editData = null // Reset edit data
                },
                onCancel = { 
                    showLogModal = false 
                    editData = null // Reset edit data when cancelling
                },
                onDelete = if (editData != null) {
                    {
                        Log.d("OverviewScreen", "Delete activity: ${editData?.id}")
                        editData?.let { activity ->
                            viewModel.deleteActivity(activity.id)
                        }
                        showLogModal = false
                        editData = null // Reset edit data after deletion
                    }
                } else null
            )
        }
    }
}

@Composable
fun CalorieInfoRow(label: String, value: Int, progressBarColor: Color, target: Int? = null) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (target != null) "$value / $target" else "$value",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        LinearProgressIndicator(
            progress = { if (target != null) value.toFloat() / target.toFloat() else 0.7f },
            modifier = Modifier.fillMaxWidth().height(8.dp),
            color = progressBarColor,
            trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
        )
    }
}

@Composable
fun BmiCard(
    bmiValue: Float,
    bmiStatus: String,
    bmiRange: String,
    statusColor: Color,
    onWeightUpdate: (Float) -> Unit = {}
) {
    var showWeightDialog by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showWeightDialog = true }
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(100.dp)
            ) {
                CircularProgressIndicator(
                    progress = { bmiValue / 40f },
                    modifier = Modifier.fillMaxSize(),
                    color = statusColor,
                    strokeWidth = 8.dp,
                    trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                )
                Text(
                    text = "%.1f".format(bmiValue),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Body Mass Index (BMI)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$bmiStatus\n(Healthy Range: $bmiRange)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = statusColor,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(1.dp))
//                Text(
//                    text = "Healthy Range: $bmiRange",
//                    style = MaterialTheme.typography.bodyMedium,
//                    color = MaterialTheme.colorScheme.onSurfaceVariant
//                )
                Text(
                    text = "Click this widget to update your weight.",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
    
    // Weight Update Dialog
    if (showWeightDialog) {
        var newWeight by remember { mutableStateOf("") }
        
        AlertDialog(
            onDismissRequest = { showWeightDialog = false },
            title = { Text("Update Weight") },
            text = {
                Column {
                    Text("Enter your new weight:")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newWeight,
                        onValueChange = { newWeight = it },
                        label = { Text("Weight (kg)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        newWeight.toFloatOrNull()?.let { weight ->
                            onWeightUpdate(weight)
                        }
                        showWeightDialog = false
                    }
                ) {
                    Text("Update")
                }
            },
            dismissButton = {
                TextButton(onClick = { showWeightDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

data class ActivityItemData(
    val description: String,
    val calorieTextForDisplay: String,
    val caloriesForModal: String,
    val type: LogType,
    val icon: ImageVector,
    val detailForModal: String? = null
)

@Composable
fun ActivityItemFromDB(activity: ActivityLog, onClick: (ActivityLog) -> Unit) {
    val isFood = activity.type == ActivityType.CONSUMPTION
    val icon = if (isFood) Icons.Default.Restaurant else Icons.Default.FitnessCenter
    val calories = activity.calories ?: 0
    val calorieText = if (isFood) "+$calories Calories" else "-$calories Calories"
    val description = when {
        isFood -> activity.foodName ?: "Food Item"
        else -> activity.workoutName ?: "Workout"
    }
    
    Card(
        modifier = Modifier
            .width(150.dp)
            .height(180.dp)
            .clickable { onClick(activity) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.weight(1f)
            ) {
                 Icon(
                    imageVector = icon,
                    contentDescription = description,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = calorieText,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
fun ActivityItem(item: ActivityItemData, onClick: (ActivityItemData) -> Unit) {
    Card(
        modifier = Modifier
            .width(150.dp)
            .height(180.dp)
            .clickable { onClick(item) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.weight(1f)
            ) {
                 Icon(
                    imageVector = item.icon,
                    contentDescription = item.description,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = item.calorieTextForDisplay,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = item.description,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                lineHeight = 16.sp
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun OverviewScreenPreview() {
    PencatatanKaloriTheme {
        OverviewScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun BmiCardPreview() {
    PencatatanKaloriTheme {
        BmiCard(bmiValue = 22.5f, bmiStatus = "Normal (55kg, 170cm)", bmiRange = "18.5 - 24.9", statusColor = Color(0xFF4CAF50))
    }
}