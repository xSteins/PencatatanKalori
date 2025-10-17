package com.ralvin.pencatatankalori.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.ralvin.pencatatankalori.viewmodel.OverviewViewModel
import com.ralvin.pencatatankalori.viewmodel.HistoryViewModel

data class LogItem(
    val id: Int,
    val type: LogType,
    val calories: Int,
    val name: String,
    val details: String,
    val pictureId: String? = null,
    val activityId: String? = null
)

enum class LogType {
    FOOD, WORKOUT
}



@Composable
fun AddActivityButtons(
    onAddFood: () -> Unit,
    onAddWorkout: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Button(
            onClick = onAddFood,
            modifier = Modifier
                .weight(1f)
                .height(56.dp),
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            enabled = enabled
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Add Food")
            Spacer(modifier = Modifier.width(4.dp))
            Text("Add Consumption Data")
        }
        Button(
            onClick = onAddWorkout,
            modifier = Modifier
                .weight(1f)
                .height(56.dp),
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
            enabled = enabled
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Add Workout")
            Spacer(modifier = Modifier.width(4.dp))
            Text("Add Workout Data")
        }
    }
}

@Composable
fun LogsDetailedModal(
    onDismissRequest: () -> Unit,
    date: String,
    logs: List<LogItem>,
    onAddFood: () -> Unit = {},
    onAddWorkout: () -> Unit = {},
    dayData: com.ralvin.pencatatankalori.viewmodel.DayData? = null,
    overviewViewModel: OverviewViewModel = hiltViewModel(),
    historyViewModel: HistoryViewModel = hiltViewModel()
) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    var showEditModal by remember { mutableStateOf(false) }
    var editLog by remember { mutableStateOf<LogItem?>(null) }

    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 560.dp)
                .heightIn(max = screenHeight * 0.85f)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Text(
                    text = date,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                // Daily Summary Information
                dayData?.let { data ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            // Text(
                            //     text = "Daily Summary",
                            //     style = MaterialTheme.typography.titleSmall,
                            //     fontWeight = FontWeight.Bold,
                            //     color = MaterialTheme.colorScheme.primary
                            // )
                            Text(
                                text = "Target Calorie: ${data.tdee}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "Consumed: ${data.caloriesConsumed} Calorie \nBurned: ${data.caloriesBurned} Calorie",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "Meal: ${data.mealCount} | Workouts: ${data.workoutCount}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "Goal: ${data.goalType.getDisplayName()}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                if (logs.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No activities recorded on this date.",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 24.dp)
                        )
                        AddActivityButtons(
                            onAddFood = onAddFood,
                            onAddWorkout = onAddWorkout,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f, fill = false)
                            .wrapContentHeight()
                    ) {
                        items(logs) { item ->
                            LogListItem(item = item, onEdit = {
                                editLog = item
                                showEditModal = true
                            }, viewModel = overviewViewModel)
                            Divider(color = Color.LightGray, thickness = 1.dp)
                        }
                    }
                }

                Button(
                    onClick = onDismissRequest,
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues()
                ) {
                    Text("Close", color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }

    if (showEditModal && editLog != null) {
        var initialImagePath by remember(editLog!!.pictureId) { mutableStateOf<String?>(null) }
        
        LaunchedEffect(editLog!!.pictureId) {
            initialImagePath = null 
            editLog!!.pictureId?.let { pictureId ->
                overviewViewModel.getPicture(pictureId) { path ->
                    initialImagePath = path
                }
            }
        }
        
        Dialog(onDismissRequest = { showEditModal = false }) {
            AddOrEditLogModal(
                type = editLog!!.type,
                initialName = editLog!!.name,
                initialCalories = editLog!!.calories.toString(),
                initialNotes = "",
                initialImagePath = initialImagePath,
                isEditMode = true,
                onSubmit = { name, calories, notes, imagePath ->
                    val activityId = editLog!!.activityId
                    if (activityId != null) {
                        if (imagePath != null && imagePath != initialImagePath) {
                            historyViewModel.savePicture(imagePath,
                                onSuccess = { pictureId ->
                                    historyViewModel.updateActivity(
                                        activityId = activityId,
                                        name = name,
                                        calories = calories.toIntOrNull() ?: 0,
                                        notes = notes,
                                        pictureId = pictureId
                                    )
                                },
                                onError = { _ ->
                                    historyViewModel.updateActivity(
                                        activityId = activityId,
                                        name = name,
                                        calories = calories.toIntOrNull() ?: 0,
                                        notes = notes,
                                        pictureId = editLog!!.pictureId
                                    )
                                }
                            )
                        } else {
                            historyViewModel.updateActivity(
                                activityId = activityId,
                                name = name,
                                calories = calories.toIntOrNull() ?: 0,
                                notes = notes,
                                pictureId = editLog!!.pictureId
                            )
                        }
                    }
                    showEditModal = false
                },
                onCancel = { showEditModal = false },
                onDelete = {
                    val activityId = editLog!!.activityId
                    if (activityId != null) {
                        historyViewModel.deleteActivity(activityId)
                    }
                    showEditModal = false
                }
            )
        }
    }
}

@Composable
fun LogListItem(item: LogItem, onEdit: () -> Unit, viewModel: OverviewViewModel = hiltViewModel()) {
    var imagePath by remember(item.pictureId) { mutableStateOf<String?>(null) }
    
    LaunchedEffect(item.pictureId) {
        imagePath = null // Reset first
        item.pictureId?.let { pictureId ->
            viewModel.getPicture(pictureId) { path ->
                imagePath = path
            }
        }
    }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val currentImagePath = imagePath
        if (currentImagePath != null) {
            val imageModel = if (currentImagePath.startsWith("android.resource://")) {
                val assetPath = currentImagePath.substringAfter("assets/")
                "file:///android_asset/$assetPath"
            } else {
                java.io.File(currentImagePath)
            }
            
            coil.compose.AsyncImage(
                model = coil.request.ImageRequest.Builder(androidx.compose.ui.platform.LocalContext.current)
                    .data(imageModel)
                    .crossfade(true)
                    .build(),
                contentDescription = item.name,
                modifier = Modifier
                    .size(60.dp)
                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(8.dp)),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                fallback = androidx.compose.ui.res.painterResource(android.R.drawable.ic_menu_gallery)
            )
            Spacer(modifier = Modifier.width(12.dp))
        }
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = if (item.type == LogType.FOOD) "${item.calories} Calories Added" else "${item.calories} Calories Burned",
                fontSize = 12.sp,
                color = Color.Gray
            )
            Text(
                text = item.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = item.details,
                fontSize = 14.sp
            )
        }
        IconButton(onClick = onEdit) {
            Icon(Icons.Filled.Edit, contentDescription = "Edit Log")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LogsDetailedModalPreview() {
    MaterialTheme {
        LogsDetailedModal(onDismissRequest = {/*do nothing*/}, date = "Thursday, 24th April", logs = listOf(
            LogItem(1, LogType.FOOD, 1600, "Ribeye Steak", "600 Calories | 60.5g Protein | 50.5g Carbs", activityId = "sample-id-1"),
            LogItem(2, LogType.WORKOUT, 600, "Jogging", "4.50km", activityId = "sample-id-2"),
            LogItem(3, LogType.FOOD, 1600, "Ribeye Steak", "600 Calories | 60.5g Protein | 50.5g Carbs", activityId = "sample-id-3")
        ))
    }
}

@Preview(showBackground = true)
@Composable
fun LogsDetailedModalEmptyPreview() {
    MaterialTheme {
        LogsDetailedModal(
            onDismissRequest = {/*do nothing*/}, 
            date = "Sunday, 28 September 2025", 
            logs = emptyList(),
            onAddFood = {/*do nothing*/},
            onAddWorkout = {/*do nothing*/}
        )
    }
}
