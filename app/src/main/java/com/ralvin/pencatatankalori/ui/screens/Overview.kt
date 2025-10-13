package com.ralvin.pencatatankalori.ui.screens

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import java.io.File
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ralvin.pencatatankalori.data.database.entities.ActivityLog
import com.ralvin.pencatatankalori.data.database.entities.ActivityType
import com.ralvin.pencatatankalori.ui.components.AddActivityButtons
import com.ralvin.pencatatankalori.ui.components.AddOrEditLogModal
import com.ralvin.pencatatankalori.ui.components.LogType
import com.ralvin.pencatatankalori.ui.theme.PencatatanKaloriTheme
import com.ralvin.pencatatankalori.viewmodel.OverviewViewModel
import com.ralvin.pencatatankalori.health.model.MifflinModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun OverviewScreen(
    viewModel: OverviewViewModel = hiltViewModel()
) {
    var showLogModal by remember { mutableStateOf(false) }
    var modalType by remember { mutableStateOf(LogType.FOOD) }
    var editData by remember { mutableStateOf<ActivityLog?>(null) }

    val overviewData by viewModel.overviewData.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    val currentDate = SimpleDateFormat("EEEE, dd MMMM", Locale.getDefault()).format(Date())
    
    val todayConsumedCalorie = overviewData?.caloriesConsumed ?: 0
    val todayBurnedCalorie = overviewData?.caloriesBurned ?: 0
    val hasUserProfile = overviewData?.user != null
    val dailyCalorieTarget = if (hasUserProfile) (overviewData?.user?.dailyCalorieTarget ?: 0) else 0
    val remainingCalories = if (hasUserProfile) (overviewData?.remainingCalories ?: 0) else 0
    val netCalories = if (hasUserProfile) (overviewData?.netCalories ?: 0) else 0
    val activities = if (hasUserProfile) (overviewData?.todayActivities ?: emptyList()) else emptyList()
    val goalType = overviewData?.user?.goalType
    
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
        bmiValue < 18.5 -> Color(0xFF2196F3)
        bmiValue < 25 -> Color(0xFF4CAF50)
        bmiValue < 30 -> Color(0xFFFF9800)
        else -> Color(0xFFF44336)
    }
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
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
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
                CalorieInfoRow("Net Calories", netCalories, Color.Cyan)
                Spacer(modifier = Modifier.height(8.dp))
                CalorieInfoRow("Remaining Calories", remainingCalories, Color.Magenta, dailyCalorieTarget)

            }
        }

        Spacer(modifier = Modifier.height(16.dp))

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

        AddActivityButtons(
            onAddFood = {
                modalType = LogType.FOOD
                editData = null
                showLogModal = true
            },
            onAddWorkout = {
                modalType = LogType.WORKOUT
                editData = null
                showLogModal = true
            },
            enabled = hasUserProfile
        )
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Activities:",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        
        if (activities.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "You haven't logged anything",
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(vertical = 8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
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
        var initialImagePath by remember(editData?.pictureId) { mutableStateOf<String?>(null) }
        val editPictureId = editData?.pictureId
        LaunchedEffect(editPictureId) {
            if (editPictureId != null) {
                viewModel.getPicture(editPictureId) { path ->
                    initialImagePath = path
                }
            } else {
                initialImagePath = null
            }
        }
        Dialog(
            onDismissRequest = { showLogModal = false },
            properties = DialogProperties(dismissOnClickOutside = false, dismissOnBackPress = true)
        ) {
            AddOrEditLogModal(
                type = modalType,
                initialName = editData?.name ?: "",
                initialCalories = editData?.calories?.toString() ?: "",
                initialProtein = "",
                initialCarbs = "",
                initialPortion = "",
                initialDuration = "",
                initialImagePath = initialImagePath,
                isEditMode = editData != null,
                onSubmit = { name, calories, protein, carbs, portion, duration, imagePath ->
                    Log.d("OverviewScreen", "Log Submitted: Type: $modalType, Name: $name, Calories: $calories, EditData: $editData, ImagePath: $imagePath")
                    
                    val currentEditData = editData
                    if (currentEditData != null) {
                        if (imagePath != null && imagePath != initialImagePath) {
                            viewModel.savePicture(imagePath, 
                                onSuccess = { pictureId ->
                                    val updatedActivity = currentEditData.copy(
                                        name = name,
                                        calories = calories.toIntOrNull() ?: 0,
                                        pictureId = pictureId
                                    )
                                    viewModel.updateActivity(updatedActivity)
                                },
                                onError = { error ->
                                    Log.e("OverviewScreen", "Failed to save image: $error")
                                    val updatedActivity = currentEditData.copy(
                                        name = name,
                                        calories = calories.toIntOrNull() ?: 0
                                    )
                                    viewModel.updateActivity(updatedActivity)
                                }
                            )
                        } else {
                            val updatedActivity = currentEditData.copy(
                                name = name,
                                calories = calories.toIntOrNull() ?: 0
                            )
                            viewModel.updateActivity(updatedActivity)
                        }
                    } else {
                        val activityType = when (modalType) {
                            LogType.FOOD -> com.ralvin.pencatatankalori.data.database.entities.ActivityType.CONSUMPTION
                            LogType.WORKOUT -> com.ralvin.pencatatankalori.data.database.entities.ActivityType.WORKOUT
                        }
                        
                        if (imagePath != null) {
                            viewModel.savePicture(imagePath,
                                onSuccess = { pictureId ->
                                    viewModel.logActivity(
                                        name = name,
                                        calories = calories.toIntOrNull() ?: 0,
                                        type = activityType,
                                        pictureId = pictureId
                                    )
                                },
                                onError = { error ->
                                    Log.e("OverviewScreen", "Failed to save image: $error")
                                    viewModel.logActivity(
                                        name = name,
                                        calories = calories.toIntOrNull() ?: 0,
                                        type = activityType
                                    )
                                }
                            )
                        } else {
                            viewModel.logActivity(
                                name = name,
                                calories = calories.toIntOrNull() ?: 0,
                                type = activityType
                            )
                        }
                    }
                    showLogModal = false
                    editData = null
                },
                onCancel = { 
                    showLogModal = false 
                    editData = null
                },
                onDelete = if (editData != null) {
                    {
                        Log.d("OverviewScreen", "Delete activity: ${editData?.id}")
                        editData?.let { activity ->
                            viewModel.deleteActivity(activity.id)
                        }
                        showLogModal = false
                        editData = null
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
                text = if (target != null && target > 0) "$value / $target" else "$value",
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
            progress = {
                when {
                    target != null && target > 0 -> (value.coerceAtLeast(0)).toFloat() / target.toFloat()
                    value > 0 -> minOf(1.0f, value.toFloat() / 2000f) // Normalize to 2000 calories max
                    else -> 0.0f // Start from 0 when value is 0 or negative
                }
            },
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
                    text = "$bmiStatus ($bmiRange)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = statusColor,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(1.dp))
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
    val calorieText = "$calories Calories"
    val description = activity.name ?: when {
        isFood -> "Food Item"
        else -> "Workout"
    }
    
    val viewModel: OverviewViewModel = hiltViewModel()
    var imagePath by remember { mutableStateOf<String?>(null) }
    
    activity.pictureId?.let { pictureId ->
        viewModel.getPicture(pictureId) { path ->
            imagePath = path
        }
    }
    
    Card(
        modifier = Modifier
            .fillMaxHeight()
            .aspectRatio(3f / 4f)
            .clickable { onClick(activity) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                if (imagePath != null) {
                    val imageModel = if (imagePath!!.startsWith("android.resource://")) {
                        val assetPath = imagePath!!.substringAfter("assets/")
                        "file:///android_asset/$assetPath"
                    } else {
                        File(imagePath!!)
                    }
                    
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(imageModel)
                            .crossfade(true)
                            .build(),
                        contentDescription = description,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop,
                        fallback = painterResource(android.R.drawable.ic_menu_gallery)
                    )
                } else {
                    Icon(
                        imageVector = icon,
                        contentDescription = description,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Text(
                text = calorieText,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
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