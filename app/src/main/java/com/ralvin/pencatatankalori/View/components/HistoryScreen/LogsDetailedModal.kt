package com.ralvin.pencatatankalori.view.components.HistoryScreen

import android.R
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.ralvin.pencatatankalori.model.formula.ActivityLevel
import com.ralvin.pencatatankalori.model.formula.GoalType
import com.ralvin.pencatatankalori.view.components.AddOrEditLogModal
import com.ralvin.pencatatankalori.view.components.EditUserDataDialog
import com.ralvin.pencatatankalori.view.components.EditUserDataType
import com.ralvin.pencatatankalori.view.components.Tooltip
import com.ralvin.pencatatankalori.viewmodel.DayData
import com.ralvin.pencatatankalori.viewmodel.HistoryViewModel
import com.ralvin.pencatatankalori.viewmodel.OverviewViewModel
import com.ralvin.pencatatankalori.viewmodel.ProfileViewModel
import java.io.File

data class LogItem(
	val id: Int,
	val type: LogType,
	val calories: Int,
	val name: String,
	val details: String,
	val pictureId: String? = null,
	val activityId: String? = null,
	val notes: String? = null
)

enum class LogType {
	FOOD, WORKOUT
}


@Composable
fun AddActivityButtons(
	onAddFood: () -> Unit,
	onAddWorkout: () -> Unit,
	modifier: Modifier = Modifier,
	enabled: Boolean = true,
	tooltipMessage: String? = null
) {
	var showTooltip by remember { mutableStateOf(false) }

	Row(
		modifier = modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.spacedBy(16.dp)
	) {
		Box(
			modifier = Modifier
				.weight(1f)
				.height(56.dp)
		) {
			Button(
				onClick = if (enabled) onAddFood else {
					{}
				},
				modifier = Modifier.fillMaxSize(),
				shape = MaterialTheme.shapes.medium,
				colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
				enabled = enabled
			) {
				Icon(Icons.Filled.Add, contentDescription = "Add Food")
				Spacer(modifier = Modifier.width(4.dp))
				Text("Konsumsi")
			}
			if (!enabled && tooltipMessage != null) {
				Box(
					modifier = Modifier
						.fillMaxSize()
						.clickable { showTooltip = true }
				)
			}
		}

		Box(
			modifier = Modifier
				.weight(1f)
				.height(56.dp)
		) {
			Button(
				onClick = if (enabled) onAddWorkout else {
					{}
				},
				modifier = Modifier.fillMaxSize(),
				shape = MaterialTheme.shapes.medium,
				colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
				enabled = enabled
			) {
				Icon(Icons.Filled.Add, contentDescription = "Add Workout")
				Spacer(modifier = Modifier.width(4.dp))
				Text("Aktivitas")
			}
			if (!enabled && tooltipMessage != null) {
				Box(
					modifier = Modifier
						.fillMaxSize()
						.clickable { showTooltip = true }
				)
			}
		}
	}

	if (showTooltip && tooltipMessage != null) {
		Tooltip(
			message = tooltipMessage,
			onDismiss = { showTooltip = false }
		)
	}
}

@Composable
fun LogsDetailedModal(
	onDismissRequest: () -> Unit,
	date: String,
	logs: List<LogItem>,
	onAddFood: () -> Unit = {},
	onAddWorkout: () -> Unit = {},
	dayData: DayData? = null,
	overviewViewModel: OverviewViewModel = hiltViewModel(),
	historyViewModel: HistoryViewModel = hiltViewModel(),
	profileViewModel: ProfileViewModel = hiltViewModel()
) {
	val configuration = LocalConfiguration.current
	val screenHeight = configuration.screenHeightDp.dp

	var showEditModal by remember { mutableStateOf(false) }
	var editLog by remember { mutableStateOf<LogItem?>(null) }

	var showEditUserDataDialog by remember { mutableStateOf(false) }
	var currentEditType by remember { mutableStateOf(EditUserDataType.WEIGHT) }
	var currentEditValue by remember { mutableStateOf("") }

	Dialog(
		onDismissRequest = onDismissRequest,
		properties = DialogProperties(usePlatformDefaultWidth = false)
	) {
		Card(
			shape = RoundedCornerShape(16.dp),
			colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 16.dp)
				.heightIn(max = screenHeight * 0.85f)
		) {
			Column(
				modifier = Modifier
					.padding(16.dp)
			) {
				Text(
					text = date,
					fontSize = 24.sp,
					fontWeight = FontWeight.Bold,
					modifier = Modifier.padding(bottom = 8.dp)
				)

				// Daily Summary Information
				dayData?.let { data ->
					Card(
						modifier = Modifier
							.fillMaxWidth(),
							colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
						)
					) {
						Column(
							modifier = Modifier.padding(16.dp),
							verticalArrangement = Arrangement.spacedBy(4.dp)
						) {
							Text(
								text = "${data.tdee} kalori (Kebutuhan Konsumsi Kalori Harian)",
								style = MaterialTheme.typography.bodyLarge
							)
							Text(
								text = "${data.mealCount} Konsumsi, ${data.caloriesConsumed} kalori | ${data.workoutCount} Aktivitas, ${data.caloriesBurned} kalori",
								style = MaterialTheme.typography.bodyMedium
							)

							Row(
								horizontalArrangement = Arrangement.spacedBy(8.dp),
								verticalAlignment = Alignment.CenterVertically
							) {
								PhysicalInfoText(
									weight = data.weight,
									activityLevel = data.activityLevel,
									goalType = data.goalType,
									enabled = data.isToday,
									onEditWeight = {
										if (data.isToday) {
											currentEditType = EditUserDataType.WEIGHT
											currentEditValue = data.weight?.toString() ?: ""
											showEditUserDataDialog = true
										}
									},
									onEditActiveLevel = {
										if (data.isToday) {
											currentEditType = EditUserDataType.ACTIVE_LEVEL
											currentEditValue =
												data.activityLevel?.getDisplayName() ?: ""
											showEditUserDataDialog = true
										}
									},
									onEditGoal = {
										if (data.isToday) {
											currentEditType = EditUserDataType.GOAL
											currentEditValue = data.goalType.getDisplayName()
											showEditUserDataDialog = true
										}
									}
								)
							}
							Text(
								text = "Tekan salah satu teks diatas untuk update data.\nHanya data diri hari ini yang dapat diupdate",
								style = MaterialTheme.typography.bodySmall,
								color = MaterialTheme.colorScheme.onSurfaceVariant,
								fontWeight = FontWeight.Light,
								modifier = Modifier.padding(top = 4.dp)
							)

							if (logs.size > 4) {
								Text(
									text = "Tarik keatas untuk lihat aktivitas lainnya",
									style = MaterialTheme.typography.bodySmall,
									color = MaterialTheme.colorScheme.onSurfaceVariant,
									fontWeight = FontWeight.Light,
									modifier = Modifier.padding(top = 4.dp)
								)
							}
						}
					}
				}

				val canAddActivities = dayData?.dailyDataId != null

				if (logs.isEmpty()) {
					Column(
						modifier = Modifier
							.fillMaxWidth()
							.padding(vertical = 32.dp),
						horizontalAlignment = Alignment.CenterHorizontally
					) {
						Text(
							text = "Belum ada aktivitas.",
							style = MaterialTheme.typography.bodyLarge,
							textAlign = TextAlign.Center,
							color = MaterialTheme.colorScheme.onSurfaceVariant,
							modifier = Modifier.padding(bottom = 24.dp)
						)
					}
				} else {
					LazyColumn(
						modifier = Modifier
							.weight(1f)
							.fillMaxWidth()
					) {
						items(logs) { item ->
							LogListItem(item = item, onEdit = {
								editLog = item
								showEditModal = true
							}, viewModel = overviewViewModel)
							HorizontalDivider(color = Color.LightGray, thickness = 1.dp)
						}
					}
				}

				Spacer(modifier = Modifier.height(16.dp))
				AddActivityButtons(
					onAddFood = onAddFood,
					onAddWorkout = onAddWorkout,
					modifier = Modifier.fillMaxWidth(),
					enabled = canAddActivities
				)

				Button(
					onClick = onDismissRequest,
					modifier = Modifier
						.align(Alignment.End)
						.padding(top = 16.dp),
					colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
					contentPadding = PaddingValues()
				) {
					Text("Close", color = MaterialTheme.colorScheme.primary)
				}
			}
		}
	}

	if (showEditModal && editLog != null) {
		var initialImagePath by remember(editLog!!.pictureId) { mutableStateOf<String?>(null) }

		LaunchedEffect(editLog!!.pictureId) {
			initialImagePath = null
			editLog!!.pictureId?.let { pictureId ->
				overviewViewModel.getPicture(pictureId) { path ->
					initialImagePath = path
				}
			}
		}

		Dialog(onDismissRequest = { showEditModal = false }) {
			AddOrEditLogModal(
				type = editLog!!.type,
				initialName = editLog!!.name,
				initialCalories = editLog!!.calories.toString(),
				initialNotes = editLog!!.notes ?: "",
				initialImagePath = initialImagePath,
				isEditMode = true,
				onSubmit = { name, calories, notes, imagePath ->
					val activityId = editLog!!.activityId
					if (activityId != null) {
						if (imagePath != null && imagePath != initialImagePath) {
							historyViewModel.savePicture(
								imagePath,
								onSuccess = { pictureId ->
									historyViewModel.updateActivity(
										activityId = activityId,
										name = name,
										calories = calories.toIntOrNull() ?: 0,
										notes = notes,
										pictureId = pictureId
									)
								},
								onError = { _ ->
									historyViewModel.updateActivity(
										activityId = activityId,
										name = name,
										calories = calories.toIntOrNull() ?: 0,
										notes = notes,
										pictureId = editLog!!.pictureId
									)
								}
							)
						} else {
							historyViewModel.updateActivity(
								activityId = activityId,
								name = name,
								calories = calories.toIntOrNull() ?: 0,
								notes = notes,
								pictureId = editLog!!.pictureId
							)
						}
					}
					showEditModal = false
				},
				onCancel = { showEditModal = false },
				onDelete = {
					val activityId = editLog!!.activityId
					if (activityId != null) {
						historyViewModel.deleteActivity(activityId)
					}
					showEditModal = false
				}
			)
		}
	}

	if (showEditUserDataDialog) {
		Dialog(onDismissRequest = { showEditUserDataDialog = false }) {
			EditUserDataDialog(
				editType = currentEditType,
				currentValue = currentEditValue,
				onDismiss = { showEditUserDataDialog = false },
				onSave = { newValue ->
					when (currentEditType) {
						EditUserDataType.WEIGHT -> {
							newValue.toFloatOrNull()?.let { newWeight ->
								profileViewModel.updateWeight(newWeight)
							}
						}

						EditUserDataType.ACTIVE_LEVEL -> {
							val activityLevel =
								ActivityLevel.entries.find { it.getDisplayName() == newValue }
							activityLevel?.let { profileViewModel.updateActivityLevel(it) }
						}

						EditUserDataType.GOAL -> {
							val goalType = GoalType.entries.find { it.getDisplayName() == newValue }
							goalType?.let { profileViewModel.updateGoalType(it) }
						}

						else -> {}
					}
					showEditUserDataDialog = false
				}
			)
		}
	}
}

@Composable
fun LogListItem(item: LogItem, onEdit: () -> Unit, viewModel: OverviewViewModel = hiltViewModel()) {
	var imagePath by remember(item.pictureId) { mutableStateOf<String?>(null) }

	LaunchedEffect(item.pictureId) {
		imagePath = null // Reset first
		item.pictureId?.let { pictureId ->
			viewModel.getPicture(pictureId) { path ->
				imagePath = path
			}
		}
	}

	Row(
		modifier = Modifier
			.fillMaxWidth()
			.padding(vertical = 12.dp),
		verticalAlignment = Alignment.CenterVertically
	) {
		val currentImagePath = imagePath
		if (currentImagePath != null) {
			val imageModel = if (currentImagePath.startsWith("android.resource://")) {
				val assetPath = currentImagePath.substringAfter("assets/")
				"file:///android_asset/$assetPath"
			} else {
				File(currentImagePath)
			}

			AsyncImage(
				model = ImageRequest.Builder(LocalContext.current)
					.data(imageModel)
					.crossfade(true)
					.build(),
				contentDescription = item.name,
				modifier = Modifier
					.size(60.dp)
					.clip(androidx.compose.foundation.shape.RoundedCornerShape(8.dp)),
				contentScale = ContentScale.Crop,
				fallback = painterResource(R.drawable.ic_menu_gallery)
			)
			Spacer(modifier = Modifier.width(12.dp))
		}

		Column(modifier = Modifier.weight(1f)) {
			Text(
				text = if (item.type == LogType.FOOD) "Konsumsi ${item.calories} kalori" else "${item.calories} kalori dibakar",
				fontSize = 12.sp,
				color = Color.Gray
			)
			Text(
				text = item.name,
				fontSize = 18.sp,
				fontWeight = FontWeight.SemiBold
			)
			Text(
				text = item.details,
				fontSize = 14.sp
			)
		}
		IconButton(onClick = onEdit) {
			Icon(Icons.Filled.Edit, contentDescription = "Edit Log")
		}
	}
}

@Preview(showBackground = true)
@Composable
fun LogsDetailedModalPreview() {
	MaterialTheme {
		LogsDetailedModal(
			onDismissRequest = {/*do nothing*/ }, date = "Thursday, 24th April", logs = listOf(
				LogItem(
					1,
					LogType.FOOD,
					1600,
					"Ribeye Steak",
					"600 Calories | 60.5g Protein | 50.5g Carbs",
					activityId = "sample-id-1"
				),
				LogItem(2, LogType.WORKOUT, 600, "Jogging", "4.50km", activityId = "sample-id-2"),
				LogItem(
					3,
					LogType.FOOD,
					1600,
					"Ribeye Steak",
					"600 Calories | 60.5g Protein | 50.5g Carbs",
					activityId = "sample-id-3"
				)
			)
		)
	}
}

@Preview(showBackground = true)
@Composable
fun LogsDetailedModalEmptyPreview() {
	MaterialTheme {
		LogsDetailedModal(
			onDismissRequest = {/*do nothing*/ },
			date = "Sunday, 28 September 2025",
			logs = emptyList(),
			onAddFood = {/*do nothing*/ },
			onAddWorkout = {/*do nothing*/ }
		)
	}
}
