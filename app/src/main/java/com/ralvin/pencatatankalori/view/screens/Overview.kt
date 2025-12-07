package com.ralvin.pencatatankalori.view.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ralvin.pencatatankalori.model.database.entities.ActivityLog
import com.ralvin.pencatatankalori.model.database.entities.ActivityType
import com.ralvin.pencatatankalori.model.formula.GoalType
import com.ralvin.pencatatankalori.view.components.OverviewScreen.ActivityItemList
import com.ralvin.pencatatankalori.view.components.HistoryScreen.AddActivityButtons
import com.ralvin.pencatatankalori.view.components.AddOrEditLogModal
import com.ralvin.pencatatankalori.view.components.OverviewScreen.BmiCard
import com.ralvin.pencatatankalori.view.components.OverviewScreen.CalorieInfoRow
import com.ralvin.pencatatankalori.view.components.HistoryScreen.LogType
import com.ralvin.pencatatankalori.view.components.Tooltip
import com.ralvin.pencatatankalori.viewmodel.OverviewViewModel
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

	val currentDate = SimpleDateFormat("EEEE, dd MMMM", Locale.getDefault()).format(Date())

	overviewData?.caloriesConsumed ?: 0
	overviewData?.caloriesBurned ?: 0
	val hasUserProfile = overviewData?.user != null
	val dailyCalorieTarget = if (hasUserProfile) {
		overviewData?.dailyData?.tdee ?: overviewData?.user?.dailyCalorieTarget ?: 0
	} else 0
	val remainingCalories = if (hasUserProfile) (overviewData?.remainingCalories ?: 0) else 0
	val netCalories = if (hasUserProfile) (overviewData?.netCalories ?: 0) else 0
	val activities =
		if (hasUserProfile) (overviewData?.todayActivities ?: emptyList()) else emptyList()
	val goalType = overviewData?.user?.goalType

	val user = overviewData?.user
	val bmiValue = user?.let { userData ->
		val heightInMeters = userData.height / 100
		if (heightInMeters > 0) userData.weight / (heightInMeters * heightInMeters) else 0f
	} ?: 0f

	val bmiStatus = user?.let { userData ->
		val statusText = when {
			bmiValue == 0f -> "Belum ada data"
			bmiValue < 18.5 -> "Kurus"
			bmiValue < 25 -> "Normal"
			bmiValue < 30 -> "Obesitas"
			else -> "Obesitas"
		}
		statusText.format(userData.weight, userData.height)
	} ?: "Belum ada data"

	"18.5 - 24.9"
	val bmiStatusColor = when {
		bmiValue == 0f -> Color.Gray
		bmiValue < 18.5 -> Color(0xFF2196F3)
		bmiValue < 25 -> Color(0xFF4CAF50)
		bmiValue < 30 -> Color(0xFFFF9800)
		else -> Color(0xFFF44336)
	}

	Column(
		modifier = Modifier.fillMaxSize()
	) {
		Column(
			modifier = Modifier.padding(all = 16.dp)
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
					var showNetCaloriesTooltip by remember { mutableStateOf(false) }
					var showSecondaryCaloriesTooltip by remember { mutableStateOf(false) }

					val netCaloriesColor = when (goalType) {
						GoalType.LOSE_WEIGHT -> {
							if (netCalories > dailyCalorieTarget) Color(0xFFE57373) else Color(
								0xFF81C784
							)
						}

						GoalType.GAIN_WEIGHT -> {
							if (netCalories >= dailyCalorieTarget) Color(0xFF81C784) else Color(
								0xFF9FA8DA
							)
						}

						else -> Color(0xFF9FA8DA)
					}

					val netCaloriesTooltipText = if (dailyCalorieTarget > 0) {
						"Kalori bersih yang dikonsumsi setelah dikurangi aktivitas olahraga.\n\nContoh: \n$dailyCalorieTarget (Target Harian) - 500 (Kalori Olahraga) + 1000 (Kalori Konsumsi) = ${dailyCalorieTarget + 500} (Kalori Bersih)."
					} else {
						"Kalori bersih yang dikonsumsi setelah dikurangi aktivitas olahraga.\n\nContoh: \n2000 (Target Harian) - 500 (Kalori Olahraga) + 1000 (Kalori Konsumsi) = 2500 (Kalori Bersih)."
					}

					CalorieInfoRow(
						label = "Kalori Bersih",
						value = netCalories,
						progressBarColor = netCaloriesColor,
						target = dailyCalorieTarget,
						showTargetInValue = false,
						onClick = { showNetCaloriesTooltip = true }
					)

					Spacer(modifier = Modifier.height(8.dp))

					val secondaryCalorieLabel: String
					val secondaryCalorieValue: Int
					val secondaryCalorieColor: Color
					val secondaryCaloriesTooltipText: String

					when (goalType) {
						GoalType.LOSE_WEIGHT -> {
							if (remainingCalories < 0) {
								secondaryCalorieLabel = "Kelebihan Kalori"
								secondaryCalorieValue = -remainingCalories
								secondaryCalorieColor = Color(0xFFFFAB91)
								secondaryCaloriesTooltipText = "Kalori yang melebihi target harian anda yaitu ${-remainingCalories} kalori."
							} else {
								secondaryCalorieLabel = "Sisa Kalori"
								secondaryCalorieValue = remainingCalories
								secondaryCalorieColor = Color(0xFF81C784)
								secondaryCaloriesTooltipText = "Sisa kalori yang dapat dikonsumsi untuk mencapai target harian anda."
							}
						}

						GoalType.GAIN_WEIGHT -> {
							if (remainingCalories < 0) {
								val excessCalories = -remainingCalories
								secondaryCalorieLabel = "Surplus Kalori"
								secondaryCalorieValue = excessCalories
								secondaryCalorieColor = Color(0xFFFFAB91)
								secondaryCaloriesTooltipText = "Kalori surplus yang telah anda konsumsi melebihi target yaitu $excessCalories kalori."
							} else {
								secondaryCalorieLabel = "Sisa Kalori"
								secondaryCalorieValue = remainingCalories
								secondaryCalorieColor = Color(0xFF9FA8DA)
								secondaryCaloriesTooltipText = "Sisa kalori yang perlu anda konsumsi untuk hari ini."
							}
						}

						else -> {
							secondaryCalorieLabel = "Sisa Kalori"
							secondaryCalorieValue = remainingCalories.coerceAtLeast(0)
							secondaryCalorieColor = Color(0xFF9FA8DA)
							secondaryCaloriesTooltipText = "Sisa kalori yang perlu anda konsumsi untuk hari ini."
						}
					}

					CalorieInfoRow(
						label = secondaryCalorieLabel,
						value = secondaryCalorieValue,
						progressBarColor = secondaryCalorieColor,
						target = dailyCalorieTarget,
						onClick = { showSecondaryCaloriesTooltip = true }
					)

					if (showNetCaloriesTooltip) {
						Tooltip(
							message = netCaloriesTooltipText,
							onDismiss = { showNetCaloriesTooltip = false }
						)
					}

					if (showSecondaryCaloriesTooltip) {
						Tooltip(
							message = secondaryCaloriesTooltipText,
							onDismiss = { showSecondaryCaloriesTooltip = false }
						)
					}
				}
			}

			Spacer(modifier = Modifier.height(16.dp))

			BmiCard(
				bmiValue = bmiValue,
				bmiStatus = bmiStatus,
				statusColor = bmiStatusColor,
				currentWeight = user?.weight,
				onWeightUpdate = { newWeight ->
					viewModel.updateUserWeight(newWeight)
				},
				enabled = hasUserProfile,
				tooltipMessage = if (!hasUserProfile) {
					"Mohon lakukan proses onboarding dengan pindah ke menu \"Profile\" -> Onboarding Screen"
				} else null
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
				enabled = hasUserProfile,
				tooltipMessage = if (!hasUserProfile) {
					"Mohon lakukan proses onboarding dengan pindah ke menu \"Profile\" -> Onboarding Screen"
				} else null
			)
			Spacer(modifier = Modifier.height(8.dp))

			Text(
				text = "Aktivitas anda:",
				style = MaterialTheme.typography.titleMedium,
				fontWeight = FontWeight.Bold
			)
			HorizontalDivider(modifier = Modifier.padding(vertical = 2.dp))
		}

		if (activities.isEmpty()) {
			Box(
				modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
				contentAlignment = Alignment.Center
			) {
				Column(
					horizontalAlignment = Alignment.Start,
					verticalArrangement = Arrangement.Center
				){
					Text(
						text = "Mulai catat kalori anda.",
						style = MaterialTheme.typography.headlineMedium
					)
					Spacer(modifier = Modifier.height(8.dp))
					Text(
						text = "Untuk memulai, gunakan menu \"Onboarding\" pada halaman Profile.",
						style = MaterialTheme.typography.labelLarge,
						color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
					)
				}
			}
		} else {
			LazyRow(
				horizontalArrangement = Arrangement.spacedBy(16.dp),
				contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
				modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
			) {
				items(activities) { activity ->
					ActivityItemList(
						activity = activity,
						onClick = { clickedActivity ->
							modalType =
								if (clickedActivity.type == ActivityType.CONSUMPTION) LogType.FOOD else LogType.WORKOUT
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
				initialNotes = editData?.notes ?: "",
				initialImagePath = initialImagePath,
				isEditMode = editData != null,
				onSubmit = { name, calories, notes, imagePath ->
					val currentEditData = editData
					if (currentEditData != null) {
						if (imagePath != null && imagePath != initialImagePath) {
							viewModel.savePicture(
								imagePath,
								onSuccess = { pictureId ->
									val updatedActivity = currentEditData.copy(
										name = name,
										calories = calories.toIntOrNull() ?: 0,
										notes = notes,
										pictureId = pictureId
									)
									viewModel.updateActivity(updatedActivity)
								},
								onError = { error ->
									val updatedActivity = currentEditData.copy(
										name = name,
										calories = calories.toIntOrNull() ?: 0,
										notes = notes
									)
									viewModel.updateActivity(updatedActivity)
								}
							)
						} else {
							val updatedActivity = currentEditData.copy(
								name = name,
								calories = calories.toIntOrNull() ?: 0,
								notes = notes
							)
							viewModel.updateActivity(updatedActivity)
						}
					} else {
						val activityType = when (modalType) {
							LogType.FOOD -> ActivityType.CONSUMPTION
							LogType.WORKOUT -> ActivityType.WORKOUT
						}

						if (imagePath != null) {
							viewModel.savePicture(
								imagePath,
								onSuccess = { pictureId ->
									viewModel.logActivity(
										name = name,
										calories = calories.toIntOrNull() ?: 0,
										type = activityType,
										pictureId = pictureId,
										notes = notes
									)
								},
								onError = { error ->
									viewModel.logActivity(
										name = name,
										calories = calories.toIntOrNull() ?: 0,
										type = activityType,
										notes = notes
									)
								}
							)
						} else {
							viewModel.logActivity(
								name = name,
								calories = calories.toIntOrNull() ?: 0,
								type = activityType,
								notes = notes
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


//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun OverviewScreenPreview() {
//	PencatatanKaloriTheme {
//		OverviewScreen()
//	}
//}
//
//@Preview(showBackground = true)
//@Composable
//fun BmiCardPreview() {
//	PencatatanKaloriTheme {
//		BmiCard(
//			bmiValue = 22.5f,
//			bmiStatus = "Normal (55kg, 170cm)",
//			statusColor = Color(0xFF4CAF50),
//			currentWeight = 55f
//		)
//	}
//}
