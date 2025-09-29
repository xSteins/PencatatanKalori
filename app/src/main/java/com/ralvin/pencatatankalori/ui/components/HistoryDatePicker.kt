package com.ralvin.pencatatankalori.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryDatePicker(
    onDismiss: () -> Unit,
    onDateRangeSelected: (startDate: Date, endDate: Date) -> Unit
) {
    val calendar = Calendar.getInstance()
    val endDate = calendar.time
    calendar.add(Calendar.DAY_OF_YEAR, -6) // 7 days including today
    val startDate = calendar.time
    
    val dateRangePickerState = rememberDateRangePickerState(
        initialSelectedStartDateMillis = startDate.time,
        initialSelectedEndDateMillis = endDate.time
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val start = dateRangePickerState.selectedStartDateMillis
                    val end = dateRangePickerState.selectedEndDateMillis
                    
                    if (start != null && end != null) {
                        onDateRangeSelected(Date(start), Date(end))
                    }
                    onDismiss()
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        DateRangePicker(
            state = dateRangePickerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp),
            title = {
                Text(
                    text = "Select date range to view your history",
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp)
                )
            },
            headline = {
                val startDate = dateRangePickerState.selectedStartDateMillis?.let {
                    SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(it))
                } ?: "Start date"
                val endDate = dateRangePickerState.selectedEndDateMillis?.let {
                    SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(it))
                } ?: "End date"

                Text(
                    text = "$startDate to $endDate",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        )
    }
}