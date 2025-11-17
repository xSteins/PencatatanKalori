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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.ralvin.pencatatankalori.model.database.entities.ActivityLog
import com.ralvin.pencatatankalori.model.database.entities.ActivityType
import com.ralvin.pencatatankalori.model.formula.GoalType
import com.ralvin.pencatatankalori.view.components.AddOrEditLogModal
import com.ralvin.pencatatankalori.view.components.CreateNewActivityDate
import com.ralvin.pencatatankalori.view.components.ExpandableFAB
import com.ralvin.pencatatankalori.view.components.HistoryDatePicker
import com.ralvin.pencatatankalori.view.components.LogItem
import com.ralvin.pencatatankalori.view.components.LogType
import com.ralvin.pencatatankalori.view.components.LogsDetailedModal
import com.ralvin.pencatatankalori.viewmodel.HistoryUiState
import com.ralvin.pencatatankalori.viewmodel.HistoryViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

data class HistoryItemData(
	val date: String,
	val consumedText: String,
	val targetText: String,
	val goalText: String,
	val mealWorkoutText: String,
	val physicalInfoText: String,
	val isGoalMet: Boolean,
	val id: UUID = UUID.randomUUID()
)

fun ActivityLog.toLogItem(): LogItem {
	return LogItem(
		id = this.id.hashCode(),
		type = when (this.type) {
			ActivityType.CONSUMPTION -> LogType.FOOD
			ActivityType.WORKOUT -> LogType.WORKOUT
		},
		calories = this.calories ?: 0,
		name = this.name ?: when (this.type) {
			ActivityType.CONSUMPTION -> "Unknown Food"
			ActivityType.WORKOUT -> "Unknown Workout"
		},
		details = when (this.type) {
			ActivityType.CONSUMPTION -> {
				val caloriesStr = "${this.calories ?: 0} Calories"
				val notesStr = this.notes?.let { " | $it" } ?: ""
				"$caloriesStr$notesStr"
			}

			ActivityType.WORKOUT -> {
				val caloriesStr = "${this.calories ?: 0} Calories"
				val notesStr = this.notes?.let { " | $it" } ?: ""
				"$caloriesStr$notesStr"
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
	val dateRange by viewModel.dateRange.collectAsState()
	val userProfile by viewModel.userProfile.collectAsState()
	val dailyDataList by viewModel.dailyDataList.collectAsState()

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
	var selectedDayIdx by remember { mutableStateOf(0) }
	var showDatePicker by remember { mutableStateOf(false) }
	var showAddModal by remember { mutableStateOf(false) }
	var addModalType by remember { mutableStateOf(LogType.FOOD) }
	var showCreateModal by remember { mutableStateOf(false) }
	var createModalType by remember { mutableStateOf(LogType.FOOD) }

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
					IconButton(onClick = { showDatePicker = true }) {
						Icon(Icons.Filled.CalendarToday, contentDescription = "Select date")
					}
				},
				windowInsets = WindowInsets(0)
			)
		},
		floatingActionButton = {
			val hasUserProfile = userProfile != null
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

					if (dayEntries.isEmpty()) {
						Box(
							modifier = Modifier.fillMaxSize(),
							contentAlignment = Alignment.Center
						) {
							Column(
								horizontalAlignment = Alignment.CenterHorizontally,
								verticalArrangement = Arrangement.Center
							) {
								Text(
									text = "Belum ada riwayat pencatatan.\nTambahkan catatan kalori anda. ",
									style = MaterialTheme.typography.titleMedium,
									color = MaterialTheme.colorScheme.onSurfaceVariant
								)
								Spacer(modifier = Modifier.height(8.dp))
								Text(
									text = "Anda bisa menambahkan data secara manual untuk tanggal tertentu melalui menu 'Tambah Data Manual' \n" +
											"\n Secara otomatis data yang ditampilkan adalah hari ini hingga 6 hari yang lalu.",
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
									else -> dayData.caloriesConsumed == dayData.tdee
								}

								// New UI formatting
								val consumedText = when (goalType) {
									GoalType.LOSE_WEIGHT -> {
										if (difference > 0) "Surplus ${absDifference} kalori"
										else "Defisit ${absDifference} kalori"
									}

									GoalType.GAIN_WEIGHT -> {
										if (difference > 0) "Surplus ${absDifference} kalori"
										else "Defisit ${absDifference} kalori"
									}

									else -> "Kalori Dikonsumsi: ${dayData.caloriesConsumed}"
								}

								val heightText = dayData.height?.let { "$it" } ?: "-"
								val weightText = dayData.weight?.let { "$it" } ?: "-"
								val goalText = buildString {
									append(goalType.getShortDisplayName())
									append(" | TB: ${heightText}cm | BB: ${weightText}kg")
								}

								val physicalInfoText = "Target Kalori: ${dayData.tdee} kalori"

								HistoryListItem(
									item = HistoryItemData(
										date = date,
										consumedText = consumedText,
										targetText = "${dayData.mealCount} Konsumsi, ${dayData.workoutCount} Kegiatan",
										goalText = goalText,
										mealWorkoutText = goalText,
										physicalInfoText = physicalInfoText,
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
											viewModel.logActivityForDailyData(
												dailyDataId = dailyDataId,
												date = selectedDayData.date,
												name = name,
												calories = calorieValue,
												type = activityType,
												pictureId = pictureId,
												notes = notes
											)
										}

										if (imagePath != null) {
											viewModel.savePicture(
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
										viewModel.savePicture(
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
			modifier = Modifier
				.fillMaxWidth()
				.clickable { onClick() },
			colors = CardDefaults.cardColors(
				containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
					alpha = 0.7f
				)
			)
		) {
			Row(
				modifier = Modifier
					.fillMaxWidth()
					.padding(16.dp),
				verticalAlignment = Alignment.CenterVertically
			) {
				Column(modifier = Modifier.weight(1f)) {
					Text(
						text = item.consumedText,
						style = MaterialTheme.typography.titleMedium,
						fontWeight = FontWeight.Bold
					)
					Spacer(modifier = Modifier.height(4.dp))
					Text(
						text = item.goalText,
						style = MaterialTheme.typography.bodySmall,
						color = MaterialTheme.colorScheme.primary
					)
					Text(
						text = item.targetText,
						style = MaterialTheme.typography.bodySmall,
						color = MaterialTheme.colorScheme.onSurfaceVariant
					)
					Text(
						text = item.physicalInfoText,
						style = MaterialTheme.typography.bodySmall,
						color = MaterialTheme.colorScheme.onSurfaceVariant
					)
				}
				Spacer(modifier = Modifier.width(16.dp))
				val statusIcon =
					if (item.isGoalMet) Icons.Filled.CheckCircle else Icons.Filled.Cancel
				val statusTint = if (item.isGoalMet) {
					MaterialTheme.colorScheme.primary
				} else {
					MaterialTheme.colorScheme.error
				}
				Icon(
					imageVector = statusIcon,
					contentDescription = if (item.isGoalMet) "Target Achieved" else "Target Not Achieved",
					modifier = Modifier.size(40.dp),
					tint = statusTint
				)
			}
		}
	}
}
//
//@Preview(showBackground = true)
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun HistoryPreview() {
//	PencatatanKaloriTheme {
//		val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault())
//		val rangeFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
//		val calendar = Calendar.getInstance()
//		val days = (0..3).map { offset ->
//			calendar.timeInMillis = System.currentTimeMillis() - offset * 24 * 60 * 60 * 1000L
//			dateFormat.format(calendar.time)
//		}
//		val logsPerDay = days.mapIndexed { idx, date ->
//			date to listOf(
//				LogItem(
//					idx * 10 + 1,
//					LogType.FOOD,
//					600 + idx * 100,
//					"Ribeye Steak",
//					"${600 + idx * 100} Calories | 60.5g Protein | 50.5g Carbs",
//					activityId = "sample-id-${idx * 10 + 1}"
//				),
//				LogItem(
//					idx * 10 + 2,
//					LogType.WORKOUT,
//					400 + idx * 50,
//					"Jogging",
//					"${4.5 + idx} km",
//					activityId = "sample-id-${idx * 10 + 2}"
//				),
//				LogItem(
//					idx * 10 + 3,
//					LogType.FOOD,
//					500 + idx * 80,
//					"Chicken Salad",
//					"${500 + idx * 80} Calories | 30g Protein | 20g Carbs",
//					activityId = "sample-id-${idx * 10 + 3}"
//				)
//			)
//		}
//
//		var showModal by remember { mutableStateOf(false) }
//		var selectedDayIdx by remember { mutableStateOf(0) }
//		val dateRangeText =
//			"${rangeFormat.format(System.currentTimeMillis() - 3 * 24 * 60 * 60 * 1000L)} - ${
//				rangeFormat.format(System.currentTimeMillis())
//			}"
//
//		Scaffold(
//			topBar = {
//				TopAppBar(
//					title = {
//						Column {
//							Text(
//								text = "Riwayat Pencatatan",
//								maxLines = 1,
//								overflow = TextOverflow.Ellipsis,
//								fontWeight = FontWeight.Medium
//							)
//							Text(
//								text = dateRangeText,
//								style = MaterialTheme.typography.bodySmall,
//								color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
//							)
//						}
//					},
//					actions = {
//						IconButton(onClick = { }) {
//							Icon(Icons.Filled.CalendarToday, contentDescription = "Select date")
//						}
//					},
//					windowInsets = WindowInsets(0)
//				)
//			}
//		) { innerPadding ->
//			Box(modifier = Modifier.padding(innerPadding)) {
//				LazyColumn(
//					modifier = Modifier
//						.fillMaxSize()
//						.padding(horizontal = 16.dp),
//					verticalArrangement = Arrangement.spacedBy(12.dp),
//					contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp)
//				) {
//					items(logsPerDay.size) { idx ->
//						val date = logsPerDay[idx].first
//						val logs = logsPerDay[idx].second
//						val consumed = logs.filter { it.type == LogType.FOOD }.sumOf { it.calories }
//						val target = 2000
//						val difference = consumed - target
//						val absDiff = if (difference < 0) -difference else difference
//						HistoryListItem(
//							item = HistoryItemData(
//								date = date,
//								consumedText = if (difference > 0) "Surplus $absDiff Kalori" else "Defisit $absDiff Kalori",
//								targetText = "${logs.count { it.type == LogType.FOOD }}x Konsumsi, ${logs.count { it.type == LogType.WORKOUT }}x Aktifitas Aktif",
//								goalText = "Cutting",
//								mealWorkoutText = "Cutting",
//								physicalInfoText = "Kebutuhan Kalori Harian: 2000 kalori\n170.0cm, 60.0kg",
//								isGoalMet = idx % 2 == 0
//							),
//							onClick = {
//								selectedDayIdx = idx
//								showModal = true
//							}
//						)
//					}
//				}
//
//				if (showModal) {
//					val (date, logs) = logsPerDay[selectedDayIdx]
//					LogsDetailedModal(
//						onDismissRequest = { showModal = false },
//						date = date,
//						logs = logs
//					)
//				}
//			}
//		}
//	}
//}
