package com.ralvin.pencatatankalori.view.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryDatePicker(
	onDismiss: () -> Unit,
	onDateRangeSelected: (startDate: Date, endDate: Date) -> Unit
) {
	val calendar = Calendar.getInstance()
	val today = calendar.time
	val todayMillis = today.time

	calendar.add(Calendar.DAY_OF_YEAR, -6) // 7 day range
	val defaultStartDate = calendar.time

	val dateRangePickerState = rememberDateRangePickerState(
		initialSelectedStartDateMillis = defaultStartDate.time,
		initialSelectedEndDateMillis = todayMillis,
		selectableDates = object : SelectableDates {
			override fun isSelectableDate(utcTimeMillis: Long): Boolean {
				return utcTimeMillis <= todayMillis
			}
		}
	)

	DatePickerDialog(
		onDismissRequest = onDismiss,
		confirmButton = {
			val isConfirmEnabled = dateRangePickerState.selectedStartDateMillis != null &&
					dateRangePickerState.selectedEndDateMillis != null

			TextButton(
				onClick = {
					val start = dateRangePickerState.selectedStartDateMillis
					val end = dateRangePickerState.selectedEndDateMillis

					if (start != null && end != null) {
						onDateRangeSelected(Date(start), Date(end))
					}
					onDismiss()
				},
				enabled = isConfirmEnabled
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
				.wrapContentHeight(),
			title = {
				Text(
					text = "Select date range",
					style = MaterialTheme.typography.headlineSmall,
					modifier = Modifier.padding(start = 24.dp, top = 16.dp)
				)
			},
			headline = {
				val dateFormatter = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
				val startDateText = dateRangePickerState.selectedStartDateMillis?.let {
					dateFormatter.format(Date(it))
				} ?: "Start date"
				val endDateText = dateRangePickerState.selectedEndDateMillis?.let {
					dateFormatter.format(Date(it))
				} ?: "End date"

				Text(
					text = "$startDateText - $endDateText",
					style = MaterialTheme.typography.titleLarge,
					color = MaterialTheme.colorScheme.onSurfaceVariant,
					modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
				)
			}
		)
	}
}