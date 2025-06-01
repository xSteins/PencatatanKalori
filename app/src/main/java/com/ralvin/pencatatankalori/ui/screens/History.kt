package com.ralvin.pencatatankalori.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert // Placeholder icon
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.ralvin.pencatatankalori.ui.components.LogsDetailedModal
import com.ralvin.pencatatankalori.ui.components.LogItem
import com.ralvin.pencatatankalori.ui.components.LogType
import com.ralvin.pencatatankalori.ui.theme.PencatatanKaloriTheme
import java.text.SimpleDateFormat
import java.util.*
import com.ralvin.pencatatankalori.ui.components.HistoryDatePicker

// Data class HistoryItemData tetap sama
data class HistoryItemData(
    val date: String,
    val caloriesText: String,
    val intakeBurnedText: String,
    val mealWorkoutText: String,
    val id: UUID = UUID.randomUUID()
)
@Preview(showBackground = true)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun History() {
    // Generate dummy data for today and 3 previous days
    val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy")
    val calendar = Calendar.getInstance()
    val days = (0..3).map { offset ->
        calendar.timeInMillis = System.currentTimeMillis() - offset * 24 * 60 * 60 * 1000L
        dateFormat.format(calendar.time)
    }
    val logsPerDay = days.mapIndexed { idx, date ->
        date to listOf(
            LogItem(idx * 10 + 1, LogType.FOOD, 600 + idx * 100, "Ribeye Steak", "${600 + idx * 100} Calories | 60.5g Protein | 50.5g Carbs"),
            LogItem(idx * 10 + 2, LogType.WORKOUT, 400 + idx * 50, "Jogging", "${4.5 + idx} km"),
            LogItem(idx * 10 + 3, LogType.FOOD, 500 + idx * 80, "Chicken Salad", "${500 + idx * 80} Calories | 30g Protein | 20g Carbs")
        )
    }

    var showModal by remember { mutableStateOf(false) }
    var selectedDayIdx by remember { mutableStateOf(0) }
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf(System.currentTimeMillis()) }

    val selectedDateString = remember(selectedDate) {
        dateFormat.format(Date(selectedDate))
    }

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
            if (showDatePicker) {
                HistoryDatePickerDialog(
                    onDismiss = { showDatePicker = false },
                    onDateSelected = { millis ->
                        selectedDate = millis
                        showDatePicker = false
                    }
                )
            }

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

@Composable
fun HistoryDatePickerDialog(onDismiss: () -> Unit, onDateSelected: (Long) -> Unit) {
    var showDialog by remember { mutableStateOf(true) }
    if (showDialog) {
        HistoryDatePicker()
    }
}

@Composable
fun HistoryListItem(item: HistoryItemData, onClick: () -> Unit) { // Add onClick parameter
    Column(
        modifier = Modifier.clickable { onClick() } // Make the entire item clickable
    ) {
        Text(
            text = item.date,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(bottom = 4.dp, start = 4.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
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
                        .align(Alignment.CenterVertically),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.MoreVert,
                        contentDescription = "Activity Visual",
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HistoryPreview() {
    PencatatanKaloriTheme { 
        History()
    }
}