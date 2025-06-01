package com.ralvin.pencatatankalori.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun HistoryDatePicker() {
    var showDatePicker by remember { mutableStateOf(true) }
    var selectedRange by remember { mutableStateOf<Pair<Long?, Long?>?>(null) }

    if (showDatePicker) {
        val dateRangePickerState = rememberDateRangePickerState()

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedRange = Pair(
                            dateRangePickerState.selectedStartDateMillis,
                            dateRangePickerState.selectedEndDateMillis
                        )
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
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
                        SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date(it))
                    } ?: "Start date"
                    val endDate = dateRangePickerState.selectedEndDateMillis?.let {
                        SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date(it))
                    } ?: "End date"

                    Text(
                        text = "$startDate to $endDate",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            )
        }
    }

    // Display selected range
    selectedRange?.let { (start, end) ->
        val formatter = SimpleDateFormat("ddMMyyyy", Locale.getDefault())
        val startStr = start?.let { formatter.format(Date(it)) } ?: "Start date"
        val endStr = end?.let { formatter.format(Date(it)) } ?: "End date"

        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Selected Range:")
            Text("$startStr-$endStr")
        }
    }
}