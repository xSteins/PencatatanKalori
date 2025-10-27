package com.ralvin.pencatatankalori.view.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ralvin.pencatatankalori.Viewmodel.UserDataDebugUiState
import com.ralvin.pencatatankalori.Viewmodel.UserDataDebugViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun UserDataDebugDialog(
	onDismiss: () -> Unit,
	viewModel: UserDataDebugViewModel = hiltViewModel()
) {
	val uiState by viewModel.uiState.collectAsStateWithLifecycle()

	Dialog(onDismissRequest = onDismiss) {
		Surface(
			modifier = Modifier.fillMaxSize(),
			color = MaterialTheme.colorScheme.surface
		) {
			Column(
				modifier = Modifier
					.padding(16.dp)
					.fillMaxSize()
			) {
				Row(
					modifier = Modifier.fillMaxWidth(),
					horizontalArrangement = Arrangement.SpaceBetween,
					verticalAlignment = Alignment.CenterVertically
				) {
					Text(
						text = "User Data Debug",
						style = MaterialTheme.typography.titleMedium,
						fontWeight = FontWeight.Bold
					)

					Row(
						horizontalArrangement = Arrangement.spacedBy(8.dp)
					) {
						TextButton(
							onClick = { viewModel.refreshData() },
							modifier = Modifier.widthIn(min = 80.dp)
						) {
							Text("Refresh")
						}
						TextButton(
							onClick = onDismiss,
							modifier = Modifier.widthIn(min = 80.dp)
						) {
							Text("Close")
						}
					}
				}

				Spacer(modifier = Modifier.height(16.dp))

				when (val currentState = uiState) {
					is UserDataDebugUiState.Loading -> {
						Box(
							modifier = Modifier.fillMaxSize(),
							contentAlignment = Alignment.Center
						) {
							CircularProgressIndicator()
						}
					}

					is UserDataDebugUiState.Error -> {
						Box(
							modifier = Modifier.fillMaxSize(),
							contentAlignment = Alignment.Center
						) {
							Text(
								text = currentState.message,
								color = MaterialTheme.colorScheme.error
							)
						}
					}

					is UserDataDebugUiState.Success -> {
						DebugDataContent(debugData = currentState.debugData)
					}
				}
			}
		}
	}
}

@Composable
private fun DebugDataContent(
	debugData: com.ralvin.pencatatankalori.Viewmodel.DebugData
) {
	LazyColumn(
		verticalArrangement = Arrangement.spacedBy(8.dp)
	) {
		item {
			Text(
				text = "Database Summary",
				style = MaterialTheme.typography.titleMedium,
				fontWeight = FontWeight.Bold,
				modifier = Modifier.padding(vertical = 8.dp)
			)
			HorizontalDivider()
			Spacer(modifier = Modifier.height(8.dp))

			Text("Total Activities: ${debugData.totalActivities}")
			Text("Food Entries: ${debugData.totalConsumptionActivities}")
			Text("Workout Entries: ${debugData.totalWorkoutActivities}")
			Text("Total Calories Consumed: ${debugData.totalCaloriesConsumed}")
			Text("Total Calories Burned: ${debugData.totalCaloriesBurned}")

			Spacer(modifier = Modifier.height(16.dp))
		}

		item {
			Text(
				text = "User Table",
				style = MaterialTheme.typography.titleMedium,
				fontWeight = FontWeight.Bold,
				modifier = Modifier.padding(vertical = 8.dp)
			)
			HorizontalDivider()
			Spacer(modifier = Modifier.height(8.dp))

			val userData = debugData.userData
			if (userData != null) {
				Text("ID: ${userData.id}")
				Text("Name: ${userData.name}")
				Text("Age: ${userData.age}")
				Text("Gender: ${userData.gender}")
				Text("Weight: ${userData.weight}")
				Text("Height: ${userData.height}")
				Text("Activity Level: ${userData.activityLevel}")
				Text("Goal Type: ${userData.goalType}")
				Text("Daily Calorie Target: ${userData.dailyCalorieTarget}")
			} else {
				Text("No user data found")
			}

			Spacer(modifier = Modifier.height(16.dp))
		}

		item {
			Text(
				text = "Activity Log Table (${debugData.activityLogs.size} entries)",
				style = MaterialTheme.typography.titleMedium,
				fontWeight = FontWeight.Bold,
				modifier = Modifier.padding(vertical = 8.dp)
			)
			HorizontalDivider()
			Spacer(modifier = Modifier.height(8.dp))

			if (debugData.activityLogs.isEmpty()) {
				Text("No activity logs found")
			}

			Spacer(modifier = Modifier.height(8.dp))
		}

		items(debugData.activityLogs) { activity ->
			Card(
				modifier = Modifier.fillMaxWidth(),
				colors = CardDefaults.cardColors(
					containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
				)
			) {
				Column(
					modifier = Modifier.padding(12.dp)
				) {
					Text(
						text = activity.type.name,
						style = MaterialTheme.typography.titleSmall,
						fontWeight = FontWeight.Bold
					)

					Text("ID: ${activity.id}")
					Text("User ID: ${activity.userId}")
					Text("Type: ${activity.type}")
					Text(
						"Timestamp: ${
							SimpleDateFormat(
								"yyyy-MM-dd HH:mm:ss",
								Locale.getDefault()
							).format(activity.timestamp)
						}"
					)

					activity.name?.let { Text("Name: $it") }
					activity.calories?.let { Text("Calories: $it") }
					activity.notes?.let { Text("Notes: $it") }
					activity.pictureId?.let { Text("Picture ID: $it") }
				}
			}
		}
	}
}