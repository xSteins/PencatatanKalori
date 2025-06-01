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
import com.ralvin.pencatatankalori.ui.components.AddOrEditLogModal
import com.ralvin.pencatatankalori.ui.components.LogType
import com.ralvin.pencatatankalori.ui.theme.PencatatanKaloriTheme
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun OverviewScreen() {
    var showLogModal by remember { mutableStateOf(false) }
    var modalType by remember { mutableStateOf(LogType.FOOD) }
    var editData by remember { mutableStateOf<Triple<String, String, String?>?>(null) } // name, calories, extra (protein/duration)

    val currentDate = SimpleDateFormat("EEEE, dd MMMM", Locale.getDefault()).format(Date())
    val todayConsumedCalorie = 1600
    val todayBurnedCalorie = 600
    val dailyCalorieTarget = 2000
    val currentIntake = todayConsumedCalorie - todayBurnedCalorie
    val activities = listOf(
        ActivityItemData(
            description = "Ribeye Steak, Vanilla...",
            calorieTextForDisplay = "+1600 Calories",
            caloriesForModal = "1600",
            type = LogType.FOOD,
            icon = Icons.Default.Restaurant,
            detailForModal = "30"
        ),
        ActivityItemData(
            description = "Morning Run",
            calorieTextForDisplay = "-600 Calories",
            caloriesForModal = "600",
            type = LogType.WORKOUT,
            icon = Icons.Default.FitnessCenter,
            detailForModal = "45"
        ),
        ActivityItemData(
            description = "Chicken Salad",
            calorieTextForDisplay = "+300 Calories",
            caloriesForModal = "300",
            type = LogType.FOOD,
            icon = Icons.Default.Restaurant,
            detailForModal = "25"
        )
    )
    // TODO: IMPLEMENT FROM DB->ViewModel->Screen ini
    val bmiValue = 22.5f
    val bmiStatus = "Normal"
    val bmiRange = "18.5 - 24.9"
    val bmiStatusColor = Color(0xFF4CAF50)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 16.dp)
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
                CalorieInfoRow("Remaining Calories", currentIntake, Color.Magenta, dailyCalorieTarget)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // BMI Card
        BmiCard(
            bmiValue = bmiValue,
            bmiStatus = bmiStatus,
            bmiRange = bmiRange,
            statusColor = bmiStatusColor
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
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 16.dp)
            )
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(activities) { activity ->
                     ActivityItem(
                         item = activity,
                         onClick = { clickedItem ->
                             modalType = clickedItem.type
                             editData = Triple(clickedItem.description, clickedItem.caloriesForModal, clickedItem.detailForModal)
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
                initialName = editData?.first ?: "",
                initialCalories = editData?.second ?: "",
                initialProtein = if (modalType == LogType.FOOD) editData?.third ?: "" else "",
                initialCarbs = "",
                initialPortion = "",
                initialDuration = if (modalType == LogType.WORKOUT) editData?.third ?: "" else "",
                onSubmit = { name, calories, protein, carbs, portion, duration ->
                    Log.d("OverviewScreen", "Log Submitted: Type: $modalType, Name: $name, Calories: $calories, Protein: $protein, Carbs: $carbs, Portion: $portion, Duration: $duration")
                    // TODO: Implement save logic (e.g., call ViewModel)
                    showLogModal = false
                },
                onCancel = { showLogModal = false }
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
    statusColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
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
                    trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
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
                    text = bmiStatus,
                    style = MaterialTheme.typography.bodyLarge,
                    color = statusColor,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Healthy Range: $bmiRange",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
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
fun ActivityItem(item: ActivityItemData, onClick: (ActivityItemData) -> Unit) {
    Card(
        modifier = Modifier
            .width(150.dp)
            .height(180.dp)
            .clickable { onClick(item) },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
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
        BmiCard(bmiValue = 22.5f, bmiStatus = "Normal", bmiRange = "18.5 - 24.9", statusColor = Color(0xFF4CAF50))
    }
}