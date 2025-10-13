package com.ralvin.pencatatankalori.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ralvin.pencatatankalori.ui.components.LogsDetailedModal
import com.ralvin.pencatatankalori.ui.components.LogItem
import com.ralvin.pencatatankalori.ui.components.LogType
import com.ralvin.pencatatankalori.ui.components.AddOrEditLogModal
import com.ralvin.pencatatankalori.ui.theme.PencatatanKaloriTheme
import com.ralvin.pencatatankalori.viewmodel.HistoryViewModel
import com.ralvin.pencatatankalori.viewmodel.HistoryUiState
import com.ralvin.pencatatankalori.data.database.entities.ActivityLog
import com.ralvin.pencatatankalori.data.database.entities.ActivityType
import java.text.SimpleDateFormat
import java.util.*
import com.ralvin.pencatatankalori.ui.components.HistoryDatePicker

data class HistoryItemData(
    val date: String,
    val caloriesText: String,
    val intakeBurnedText: String,
    val mealWorkoutText: String,
    val id: UUID = UUID.randomUUID()
)

fun ActivityLog.toLogItem(): LogItem {
    return LogItem(
        id = this.id.hashCode(),
        type = when(this.type) {
            ActivityType.CONSUMPTION -> LogType.FOOD
            ActivityType.WORKOUT -> LogType.WORKOUT
        },
        calories = this.calories ?: 0,
        name = this.name ?: when(this.type) {
            ActivityType.CONSUMPTION -> "Unknown Food"
            ActivityType.WORKOUT -> "Unknown Workout"
        },
        details = when(this.type) {
            ActivityType.CONSUMPTION -> {
                val caloriesStr = "${this.calories ?: 0} Calories"
                "$caloriesStr${this.notes?.let { " | $it" } ?: ""}"
            }
            ActivityType.WORKOUT -> {
                val caloriesStr = "${this.calories ?: 0} Calories"
                "$caloriesStr${this.notes?.let { " | $it" } ?: ""}"
            }
        },
        pictureId = this.pictureId,
        activityId = this.id
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun History(
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val allActivities by viewModel.allActivities.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val dateRange by viewModel.dateRange.collectAsState()
    
    val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault())
    
    val isDefaultRange = remember(dateRange) {
        val (startDate, endDate) = dateRange
        val calendar = Calendar.getInstance()
        val defaultEndDate = calendar.time
        calendar.add(Calendar.DAY_OF_YEAR, -6)
        val defaultStartDate = calendar.time
        
        val daysDiff = (endDate.time - defaultEndDate.time).let { if (it < 0) -it else it } / (24 * 60 * 60 * 1000)
        val startDaysDiff = (startDate.time - defaultStartDate.time).let { if (it < 0) -it else it } / (24 * 60 * 60 * 1000)
        daysDiff <= 1 && startDaysDiff <= 1
    }
    
    val daysData = remember(allActivities, dateRange) {
        val allDaysData = if (isDefaultRange) {
            viewModel.getLastNDaysData(7)
        } else {
            viewModel.getDayDataForSelectedRange()
        }
        
        // Filter to only include days that have activities
        allDaysData.filter { dayData ->
            allActivities.any { activity ->
                val activityCalendar = Calendar.getInstance()
                activityCalendar.time = activity.timestamp
                
                val dayCalendar = Calendar.getInstance()
                dayCalendar.time = dayData.date
                
                activityCalendar.get(Calendar.YEAR) == dayCalendar.get(Calendar.YEAR) &&
                activityCalendar.get(Calendar.DAY_OF_YEAR) == dayCalendar.get(Calendar.DAY_OF_YEAR)
            }
        }
    }
    
    val filteredDaysWithLogs = remember(daysData, allActivities) {
        daysData.mapNotNull { dayData ->
            val dateString = dateFormat.format(dayData.date)
            val activitiesForDay = allActivities.filter { activity ->
                val activityCalendar = Calendar.getInstance()
                activityCalendar.time = activity.timestamp
                
                val dayCalendar = Calendar.getInstance()
                dayCalendar.time = dayData.date
                
                activityCalendar.get(Calendar.YEAR) == dayCalendar.get(Calendar.YEAR) &&
                activityCalendar.get(Calendar.DAY_OF_YEAR) == dayCalendar.get(Calendar.DAY_OF_YEAR)
            }
            
            // Only include days that have activities
            if (activitiesForDay.isNotEmpty()) {
                Triple(dayData, dateString, activitiesForDay.map { it.toLogItem() })
            } else {
                null
            }
        }
    }

    var showModal by remember { mutableStateOf(false) }
    var selectedDayIdx by remember { mutableStateOf(0) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showAddModal by remember { mutableStateOf(false) }
    var addModalType by remember { mutableStateOf(LogType.FOOD) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "User Calories History",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.Medium
                    )
                },
                actions = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Filled.CalendarToday, contentDescription = "Select date")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            val currentUiState = uiState
            when (currentUiState) {
                is HistoryUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is HistoryUiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = currentUiState.message,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                is HistoryUiState.Success -> {
                    if (showDatePicker) {
                        HistoryDatePicker(
                            onDismiss = { showDatePicker = false },
                            onDateRangeSelected = { startDate, endDate ->
                                viewModel.selectDateRange(startDate, endDate)
                                showDatePicker = false
                            }
                        )
                    }

                    if (filteredDaysWithLogs.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "No activity data found",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Start logging your meals and workouts to see your history",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp)
                        ) {
                            items(filteredDaysWithLogs.size) { idx ->
                                val (dayData, date, logs) = filteredDaysWithLogs[idx]
                                val profile = userProfile
                                val dailyTarget = profile?.dailyCalorieTarget ?: 2000
                                
                                HistoryListItem(
                                    item = HistoryItemData(
                                        date = date,
                                        caloriesText = "${dayData.caloriesConsumed} / $dailyTarget Calories",
                                        intakeBurnedText = "${dayData.caloriesConsumed} Intake | ${dayData.caloriesBurned} Burned",
                                        mealWorkoutText = "${logs.count { it.type == LogType.FOOD }} Meal | ${logs.count { it.type == LogType.WORKOUT }} Workout"
                                    ),
                                    onClick = {
                                        selectedDayIdx = idx
                                        showModal = true
                                    }
                                )
                            }
                        }
                    }

                    if (showModal) {
                        val (_, date, logs) = filteredDaysWithLogs[selectedDayIdx]
                        LogsDetailedModal(
                            onDismissRequest = { showModal = false },
                            date = date,
                            logs = logs,
                            onAddFood = {
                                addModalType = LogType.FOOD
                                showAddModal = true
                            },
                            onAddWorkout = {
                                addModalType = LogType.WORKOUT
                                showAddModal = true
                            }
                        )
                    }
                    
                    if (showAddModal) {
                        AddOrEditLogModal(
                            type = addModalType,
                            onSubmit = { name, calories, protein, carbs, portion, duration, imagePath ->
                                val activityType = when (addModalType) {
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
                                showAddModal = false
                            },
                            onCancel = { showAddModal = false }
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun HistoryListItem(item: HistoryItemData, onClick: () -> Unit) {
    Column {
        Text(
            text = item.date,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(bottom = 4.dp, start = 4.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.caloriesText,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = item.intakeBurnedText,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = item.mealWorkoutText,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .align(Alignment.CenterVertically)
                        .clickable { onClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.MoreVert,
                        contentDescription = "More Options",
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryPreview() {
    PencatatanKaloriTheme {
        val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault())
        val calendar = Calendar.getInstance()
        val days = (0..3).map { offset ->
            calendar.timeInMillis = System.currentTimeMillis() - offset * 24 * 60 * 60 * 1000L
            dateFormat.format(calendar.time)
        }
        val logsPerDay = days.mapIndexed { idx, date ->
            date to listOf(
                LogItem(idx * 10 + 1, LogType.FOOD, 600 + idx * 100, "Ribeye Steak", "${600 + idx * 100} Calories | 60.5g Protein | 50.5g Carbs", activityId = "sample-id-${idx * 10 + 1}"),
                LogItem(idx * 10 + 2, LogType.WORKOUT, 400 + idx * 50, "Jogging", "${4.5 + idx} km", activityId = "sample-id-${idx * 10 + 2}"),
                LogItem(idx * 10 + 3, LogType.FOOD, 500 + idx * 80, "Chicken Salad", "${500 + idx * 80} Calories | 30g Protein | 20g Carbs", activityId = "sample-id-${idx * 10 + 3}")
            )
        }

        var showModal by remember { mutableStateOf(false) }
        var selectedDayIdx by remember { mutableStateOf(0) }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "User Calories History",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = FontWeight.Medium
                        )
                    },
                    actions = {
                        IconButton(onClick = { }) {
                            Icon(Icons.Filled.CalendarToday, contentDescription = "Select date")
                        }
                    }
                )
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp)
                ) {
                    items(logsPerDay.size) { idx ->
                        val date = logsPerDay[idx].first
                        val logs = logsPerDay[idx].second
                        HistoryListItem(
                            item = HistoryItemData(
                                date = date,
                                caloriesText = "${logs.sumOf { it.calories }} / 2000 Calories",
                                intakeBurnedText = "${logs.filter { it.type == LogType.FOOD }.sumOf { it.calories }} Intake | ${logs.filter { it.type == LogType.WORKOUT }.sumOf { it.calories }} Burned",
                                mealWorkoutText = "${logs.count { it.type == LogType.FOOD }} Meal | ${logs.count { it.type == LogType.WORKOUT }} Workout"
                            ),
                            onClick = {
                                selectedDayIdx = idx
                                showModal = true
                            }
                        )
                    }
                }

                if (showModal) {
                    val (date, logs) = logsPerDay[selectedDayIdx]
                    LogsDetailedModal(
                        onDismissRequest = { showModal = false },
                        date = date,
                        logs = logs
                    )
                }
            }
        }
    }
}