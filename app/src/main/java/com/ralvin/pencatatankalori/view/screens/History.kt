package com.ralvin.pencatatankalori.view.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.ralvin.pencatatankalori.model.database.entities.ActivityLog
import com.ralvin.pencatatankalori.model.database.entities.ActivityType
import com.ralvin.pencatatankalori.model.formula.GoalType
import com.ralvin.pencatatankalori.view.components.AddOrEditLogModal
import com.ralvin.pencatatankalori.view.components.HistoryScreen.CreateNewActivityDate
import com.ralvin.pencatatankalori.view.components.HistoryScreen.ExpandableFAB
import com.ralvin.pencatatankalori.view.components.HistoryScreen.HistoryDatePicker
import com.ralvin.pencatatankalori.view.components.HistoryScreen.HistoryItemData
import com.ralvin.pencatatankalori.view.components.HistoryScreen.HistoryListItem
import com.ralvin.pencatatankalori.view.components.HistoryScreen.LogItem
import com.ralvin.pencatatankalori.view.components.HistoryScreen.LogType
import com.ralvin.pencatatankalori.view.components.HistoryScreen.LogsDetailedModal
import com.ralvin.pencatatankalori.view.components.Tooltip
import com.ralvin.pencatatankalori.viewmodel.HistoryViewModel
import com.ralvin.pencatatankalori.viewmodel.OverviewViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

fun ActivityLog.toLogItem(): LogItem {
	return LogItem(
		id = this.id.hashCode(),
		type = when (this.type) {
			ActivityType.CONSUMPTION -> LogType.FOOD
			ActivityType.WORKOUT -> LogType.WORKOUT
		},
		calories = this.calories,
		name = this.name,
		details = when (this.type) {
			ActivityType.CONSUMPTION -> {
				val caloriesStr = "${this.calories} kalori"
				val notesStr = this.notes?.let { "\n$it" } ?: ""
				"$caloriesStr$notesStr"
			}

			ActivityType.WORKOUT -> {
				val caloriesStr = "${this.calories} kalori"
				val notesStr = this.notes?.let { "\n$it" } ?: ""
				"$caloriesStr$notesStr"
			}
		},
		pictureId = this.pictureId,
		activityId = this.id,
		notes = this.notes,
		activityLog = this
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun History(
	viewModel: HistoryViewModel = hiltViewModel(),
	overviewViewModel: OverviewViewModel = hiltViewModel()
) {
	val allActivities by viewModel.allActivities.collectAsStateWithLifecycle()
	val dateRange by viewModel.dateRange.collectAsStateWithLifecycle()
	val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
	val dailyDataList by viewModel.dailyDataList.collectAsStateWithLifecycle()

	val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault())
	val rangeFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
	val dateRangeText = remember(dateRange) {
		val (startDate, endDate) = dateRange
		"${rangeFormat.format(startDate)} - ${rangeFormat.format(endDate)}"
	}

	val isDefaultRange = remember(dateRange) {
		val (startDate, endDate) = dateRange
		val calendar = Calendar.getInstance()
		val defaultEndDate = calendar.time
		calendar.add(Calendar.DAY_OF_YEAR, -6)
		val defaultStartDate = calendar.time

		val daysDiff =
			(endDate.time - defaultEndDate.time).let { if (it < 0) -it else it } / (24 * 60 * 60 * 1000)
		val startDaysDiff =
			(startDate.time - defaultStartDate.time).let { if (it < 0) -it else it } / (24 * 60 * 60 * 1000)
		daysDiff <= 1 && startDaysDiff <= 1
	}

	val dayEntries = remember(allActivities, dateRange, userProfile, dailyDataList) {
		val allDaysData = if (isDefaultRange) {
			viewModel.getLastNDaysData(7)
		} else {
			viewModel.getDayDataForSelectedRange()
		}

		allDaysData.mapNotNull { dayData ->
			val dateString = dateFormat.format(dayData.date)
			val activitiesForDay = allActivities.filter { activity ->
				val activityCalendar = Calendar.getInstance()
				activityCalendar.time = activity.timestamp

				val dayCalendar = Calendar.getInstance()
				dayCalendar.time = dayData.date

				activityCalendar.get(Calendar.YEAR) == dayCalendar.get(Calendar.YEAR) &&
						activityCalendar.get(Calendar.DAY_OF_YEAR) == dayCalendar.get(Calendar.DAY_OF_YEAR)
			}

			if (dayData.dailyDataId != null || activitiesForDay.isNotEmpty()) {
				Triple(dayData, dateString, activitiesForDay.map { it.toLogItem() })
			} else {
				null
			}
		}
	}

	var showModal by remember { mutableStateOf(false) }
	var selectedDayIdx by remember { mutableIntStateOf(0) }
	var showDatePicker by remember { mutableStateOf(false) }
	var showAddModal by remember { mutableStateOf(false) }
	var addModalType by remember { mutableStateOf(LogType.FOOD) }
	var showCreateModal by remember { mutableStateOf(false) }
	var createModalType by remember { mutableStateOf(LogType.FOOD) }
	var showTooltip by remember { mutableStateOf(false) }
	val hasUserProfile = userProfile != null
	Scaffold(
		topBar = {
			TopAppBar(
				title = {
					Column {
						Text(
							text = "Riwayat Pencatatan",
							maxLines = 1,
							overflow = TextOverflow.Ellipsis,
							fontWeight = FontWeight.Medium
						)
						Text(
							text = dateRangeText,
							style = MaterialTheme.typography.bodySmall,
							color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
						)
					}
				},
				actions = {
					Box {
						IconButton(
							onClick = {
								if (hasUserProfile) {
									showDatePicker = true
								} else {
									showTooltip = true
								}
							}
						) {
							Icon(Icons.Filled.CalendarToday, contentDescription = "Select date")
						}
					}
				},
				windowInsets = WindowInsets(0)
			)
		},
		floatingActionButton = {
			ExpandableFAB(
				onFoodClick = {
					createModalType = LogType.FOOD
					showCreateModal = true
				},
				onWorkoutClick = {
					createModalType = LogType.WORKOUT
					showCreateModal = true
				},
				enabled = hasUserProfile,
				tooltipMessage = if (!hasUserProfile) {
					"Mohon lakukan proses onboarding dengan pindah ke menu \"Profile\" -> Onboarding Screen"
				} else null
			)
		}
	) { innerPadding ->
		Box(modifier = Modifier.padding(innerPadding)) {
			if (showDatePicker) {
				HistoryDatePicker(
					onDismiss = { showDatePicker = false },
					onDateRangeSelected = { startDate, endDate ->
						viewModel.selectDateRange(startDate, endDate)
						showDatePicker = false
					}
				)
			}

			if (showTooltip) {
				Tooltip(
					message = "Mohon lakukan proses onboarding dengan pindah ke menu \"Profile\" -> Onboarding Screen",
					onDismiss = { showTooltip = false }
				)
			}

			if (dayEntries.isEmpty()) {
						Box(
							modifier = Modifier
								.fillMaxSize()
								.padding(horizontal = 48.dp),
							contentAlignment = Alignment.Center
						) {
							Column(
								horizontalAlignment = Alignment.Start,
								verticalArrangement = Arrangement.Center
							) {
								Text(
									text = "Belum ada riwayat pencatatan,\ntambah catatan kalori anda. ",
									style = MaterialTheme.typography.titleMedium,
									color = MaterialTheme.colorScheme.onSurfaceVariant
								)
								Spacer(modifier = Modifier.height(8.dp))
								Text(
									text = "Anda bisa menambahkan data untuk tanggal tertentu melalui menu \"Tambah Data Manual\".\n" +
											"\nSecara otomatis data yang ditampilkan adalah hari ini hingga 6 hari yang lalu.",
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
							items(dayEntries.size) { idx ->
								val (dayData, date, _) = dayEntries[idx]
								val goalType = dayData.goalType
								val difference = dayData.caloriesConsumed - dayData.tdee
								val absDifference = if (difference < 0) -difference else difference

								val goalMet = when (goalType) {
									GoalType.LOSE_WEIGHT -> dayData.caloriesConsumed <= dayData.tdee
									GoalType.GAIN_WEIGHT -> dayData.caloriesConsumed >= dayData.tdee
								}

								// New UI formatting
								val consumedText = when (goalType) {
									GoalType.LOSE_WEIGHT -> {
										if (difference > 0) "Surplus $absDifference kalori"
										else "Defisit $absDifference kalori"
									}

									GoalType.GAIN_WEIGHT -> {
										if (difference > 0) "Surplus $absDifference kalori"
										else "Defisit $absDifference kalori"
									}
								}

								val activityLevelText = dayData.activityLevel?.getDisplayName() ?: "-"
								val weightText = dayData.weight?.let { "${it}kg" } ?: "-"
								val goalTypeText = goalType.getShortDisplayName()

								val personalInfoText = buildString {
									if (dayData.activityLevel != null) {
										append(activityLevelText)
										append(" | ")
									}
									append("BB: $weightText")
									append(" | ")
									append(goalTypeText)
								}

								val calorieTargetText = "Target Kalori: ${dayData.tdee} kalori"

								HistoryListItem(
									item = HistoryItemData(
										date = date,
										consumedText = consumedText,
										targetText = "${dayData.mealCount} Konsumsi, ${dayData.workoutCount} Kegiatan",
										personalInfoText = personalInfoText,
										calorieTargetText = calorieTargetText,
										isGoalMet = goalMet
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
						val selectedEntry = dayEntries.getOrNull(selectedDayIdx)
						if (selectedEntry != null) {
							val (dayData, date, logs) = selectedEntry
							LogsDetailedModal(
								onDismissRequest = { showModal = false },
								date = date,
								logs = logs,
								dayData = dayData,
								historyViewModel = viewModel,
								overviewViewModel = overviewViewModel,
								onAddFood = {
									val dailyDataId =
										dayEntries.getOrNull(selectedDayIdx)?.first?.dailyDataId
									if (dailyDataId != null) {
										addModalType = LogType.FOOD
										showAddModal = true
									}
								},
								onAddWorkout = {
									val dailyDataId =
										dayEntries.getOrNull(selectedDayIdx)?.first?.dailyDataId
									if (dailyDataId != null) {
										addModalType = LogType.WORKOUT
										showAddModal = true
									}
								}
							)
						} else {
							showModal = false
						}
					}

					if (showAddModal) {
						Dialog(onDismissRequest = { showAddModal = false }) {
							AddOrEditLogModal(
								type = addModalType,
								onSubmit = { name, calories, notes, imagePath ->
									val activityType = when (addModalType) {
										LogType.FOOD -> ActivityType.CONSUMPTION
										LogType.WORKOUT -> ActivityType.WORKOUT
									}
									val selectedEntry = dayEntries.getOrNull(selectedDayIdx)
									val selectedDayData = selectedEntry?.first
									val dailyDataId = selectedDayData?.dailyDataId

									if (selectedDayData != null && dailyDataId != null) {
										val calorieValue = calories.toIntOrNull() ?: 0
										val logActivity: (String?) -> Unit = { pictureId ->
											viewModel.logActivityForDate(
												date = selectedDayData.date,
												name = name,
												calories = calorieValue,
												type = activityType,
												pictureId = pictureId,
												notes = notes
											)
										}

										if (imagePath != null) {
											overviewViewModel.savePicture(
												imagePath,
												onSuccess = { pictureId ->
													logActivity(pictureId)
												},
												onError = { _ ->
													logActivity(null)
												}
											)
										} else {
											logActivity(null)
										}
									}

									showAddModal = false
								},
								onCancel = { showAddModal = false }
							)
						}
					}

					if (showCreateModal) {
						Dialog(onDismissRequest = { showCreateModal = false }) {
							CreateNewActivityDate(
								type = createModalType,
								onSubmit = { name, calories, notes, imagePath, selectedDate ->
									val activityType = when (createModalType) {
										LogType.FOOD -> ActivityType.CONSUMPTION
										LogType.WORKOUT -> ActivityType.WORKOUT
									}
									val calorieValue = calories.toIntOrNull() ?: 0

									val logActivity: (String?) -> Unit = { pictureId ->
										viewModel.logActivityForDate(
											date = selectedDate,
											name = name,
											calories = calorieValue,
											type = activityType,
											pictureId = pictureId,
											notes = notes
										)
									}

									if (imagePath != null) {
										overviewViewModel.savePicture(
											imagePath,
											onSuccess = { pictureId ->
												logActivity(pictureId)
											},
											onError = { _ ->
												logActivity(null)
											}
										)
									} else {
										logActivity(null)
									}

									showCreateModal = false
								},
								onCancel = { showCreateModal = false }
							)
						}
					}
		}
	}
}
