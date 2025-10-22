package com.ralvin.pencatatankalori.View.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Height
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ralvin.pencatatankalori.Model.formula.ActivityLevel
import com.ralvin.pencatatankalori.Model.formula.CalorieStrategy
import com.ralvin.pencatatankalori.Model.formula.GoalType
import com.ralvin.pencatatankalori.Model.formula.MifflinModel
import com.ralvin.pencatatankalori.View.components.CalorieSettingsDialog
import com.ralvin.pencatatankalori.View.components.EditUserDataDialog
import com.ralvin.pencatatankalori.View.components.EditUserDataType
import com.ralvin.pencatatankalori.View.components.OnboardingDialog
import com.ralvin.pencatatankalori.View.components.UserDataDebugDialog
import com.ralvin.pencatatankalori.View.theme.PencatatanKaloriTheme
import com.ralvin.pencatatankalori.Viewmodel.OnboardingViewModel
import com.ralvin.pencatatankalori.Viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSettings(
	profileViewModel: ProfileViewModel = hiltViewModel(),
	onboardingViewModel: OnboardingViewModel = hiltViewModel()
) {
	var showOnboardingDialog by remember { mutableStateOf(false) }
	var showUserDataDebugDialog by remember { mutableStateOf(false) }
	var showCalorieSettingsDialog by remember { mutableStateOf(false) }

	var showEditDataDialog by remember { mutableStateOf(false) }
	var showEditGoalDialog by remember { mutableStateOf(false) }
	var showEditActivityDialog by remember { mutableStateOf(false) }
	var showEditDialog by remember { mutableStateOf(false) }
	var currentEditType by remember { mutableStateOf(EditUserDataType.WEIGHT) }
	var currentEditValue by remember { mutableStateOf("") }

	val userProfile by profileViewModel.userProfile.collectAsStateWithLifecycle()
	val profileUiState by profileViewModel.uiState.collectAsStateWithLifecycle()
	val onboardingUiState by onboardingViewModel.uiState.collectAsStateWithLifecycle()
	val isDummyDataEnabled by profileViewModel.isDummyDataEnabled.collectAsStateWithLifecycle()
	val todayDailyData by profileViewModel.todayDailyData.collectAsStateWithLifecycle()

	LaunchedEffect(Unit) {
		val userExists = profileViewModel.checkUserExists()
		if (!userExists) {
			showOnboardingDialog = true
		}
	}

	LaunchedEffect(onboardingUiState, userProfile) {
		val currentOnboardingState = onboardingUiState
		val currentUserProfile = userProfile
		if (currentOnboardingState is com.ralvin.pencatatankalori.Viewmodel.OnboardingUiState.Success && currentUserProfile != null) {
			showOnboardingDialog = false
			onboardingViewModel.resetUiState()
		}
	}

	fun openEditDialog(type: EditUserDataType) {
		val profile = userProfile
		profile?.let {
			currentEditType = type
			when (type) {
				EditUserDataType.WEIGHT, EditUserDataType.HEIGHT, EditUserDataType.AGE -> {
					showEditDataDialog = true
				}

				EditUserDataType.GOAL -> {
					showEditGoalDialog = true
				}

				EditUserDataType.ACTIVE_LEVEL -> {
					showEditActivityDialog = true
				}

				EditUserDataType.GENDER -> {
					currentEditValue = it.gender
					showEditDialog = true
				}
			}
		}
	}

	fun handleProfileUpdate(type: EditUserDataType, newValue: String) {
		val profile = userProfile
		profile?.let {
			when (type) {
				EditUserDataType.GENDER -> {
					profileViewModel.updateUserProfile(
						name = it.name,
						age = it.age,
						gender = newValue,
						weight = it.weight,
						height = it.height,
						activityLevel = it.activityLevel,
						goalType = it.goalType,
						dailyCalorieTarget = it.dailyCalorieTarget
					)
				}

				else -> {}
			}
		}
		showEditDialog = false
	}

	fun handleValueUpdate(value: String) {
		val profile = userProfile
		profile?.let {
			when (currentEditType) {
				EditUserDataType.WEIGHT -> {
					value.toFloatOrNull()?.let { floatValue ->
						profileViewModel.updateWeight(floatValue)
					}
				}

				EditUserDataType.HEIGHT -> {
					value.toFloatOrNull()?.let { floatValue ->
						profileViewModel.updateUserProfile(
							name = it.name,
							age = it.age,
							gender = it.gender,
							weight = it.weight,
							height = floatValue,
							activityLevel = it.activityLevel,
							goalType = it.goalType,
							dailyCalorieTarget = it.dailyCalorieTarget
						)
					}
				}

				EditUserDataType.AGE -> {
					value.toIntOrNull()?.let { intValue ->
						profileViewModel.updateUserProfile(
							name = it.name,
							age = intValue,
							gender = it.gender,
							weight = it.weight,
							height = it.height,
							activityLevel = it.activityLevel,
							goalType = it.goalType,
							dailyCalorieTarget = it.dailyCalorieTarget
						)
					}
				}

				else -> {}
			}
		}
		showEditDataDialog = false
	}

	fun handleGoalUpdate(goalType: GoalType) {
		profileViewModel.updateGoalType(goalType)
		showEditGoalDialog = false
	}

	fun handleActivityLevelUpdate(activityLevel: ActivityLevel) {
		profileViewModel.updateActivityLevel(activityLevel)
		showEditActivityDialog = false
	}

	Scaffold(
		topBar = {
			TopAppBar(
				title = {
					Text(
						text = "Pengaturan Profil",
						fontWeight = FontWeight.Medium,
						fontSize = 22.sp
					)
				},
				windowInsets = WindowInsets(0)
			)
		}
	) { innerPadding ->
		Box(modifier = Modifier.padding(innerPadding)) {
			val currentProfileUiState = profileUiState
			when {
				currentProfileUiState is com.ralvin.pencatatankalori.Viewmodel.ProfileUiState.Loading -> {
					Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
						CircularProgressIndicator()
					}
				}

				else -> {
					LazyColumn(
						modifier = Modifier
							.fillMaxSize()
							.background(MaterialTheme.colorScheme.background),
						contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
						verticalArrangement = Arrangement.spacedBy(16.dp)
					) {
						item {
							val currentUserProfile = userProfile
							val hasUserData = currentUserProfile != null

							Card(
								modifier = Modifier.fillMaxWidth(),
								colors = CardDefaults.cardColors(
									containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
										alpha = 0.2f
									)
								)
							) {
								ProfileSettingItem(
									icon = Icons.Filled.FitnessCenter,
									label = "Tingkat Keaktifan",
									value = currentUserProfile?.activityLevel?.getDisplayName()
										?: "Sedentary",
									onClick = if (hasUserData) {
										{ openEditDialog(EditUserDataType.ACTIVE_LEVEL) }
									} else {
										{}
									},
									isEditable = hasUserData
								)
								HorizontalDivider()
								ProfileSettingItem(
									icon = Icons.Filled.Flag,
									label = "Tujuan",
									value = currentUserProfile?.goalType?.getDisplayName()
										?: "Gain Weight",
									onClick = if (hasUserData) {
										{ openEditDialog(EditUserDataType.GOAL) }
									} else {
										{}
									},
									isEditable = hasUserData
								)
								HorizontalDivider()
								ProfileSettingItem(
									icon = Icons.Filled.MonitorWeight,
									label = "Berat Badan",
									value = currentUserProfile?.let { "${it.weight}kg" } ?: "50kg",
									onClick = if (hasUserData) {
										{ openEditDialog(EditUserDataType.WEIGHT) }
									} else {
										{}
									},
									isEditable = hasUserData
								)
								HorizontalDivider()
								ProfileSettingItem(
									icon = Icons.Filled.Height,
									label = "Tinggi Badan",
									value = currentUserProfile?.let { "${it.height}cm" } ?: "170cm",
									onClick = if (hasUserData) {
										{ openEditDialog(EditUserDataType.HEIGHT) }
									} else {
										{}
									},
									isEditable = hasUserData
								)
								HorizontalDivider()
								ProfileSettingItem(
									icon = Icons.Filled.Cake,
									label = "Umur",
									value = currentUserProfile?.let { "${it.age} Tahun" }
										?: "25 Tahun",
									onClick = if (hasUserData) {
										{ openEditDialog(EditUserDataType.AGE) }
									} else {
										{}
									},
									isEditable = hasUserData
								)
								HorizontalDivider()
								ProfileSettingItem(
									icon = Icons.Filled.People,
									label = "Jenis Kelamin",
									value = currentUserProfile?.let { if (it.gender == "Male") "Pria" else "Wanita" }
										?: "Pria",
									onClick = if (hasUserData) {
										{ openEditDialog(EditUserDataType.GENDER) }
									} else {
										{}
									},
									isEditable = hasUserData
								)
								HorizontalDivider()
								ProfileSettingItem(
									icon = Icons.Filled.Tune,
									label = "Pengaturan Kalori",
									value = if (MifflinModel.isAdvancedEnabled()) {
										"Advanced: ${MifflinModel.getCalorieStrategy().displayName} (${MifflinModel.getGranularityValue()} cal)"
									} else {
										"Basic: ${MifflinModel.getGranularityValue()} cal adjustment"
									},
									onClick = if (hasUserData) {
										{ showCalorieSettingsDialog = true }
									} else {
										{}
									},
									isEditable = hasUserData
								)
							}
						}

						item {
							Column {
								Text(
									text = "Debugging",
									style = MaterialTheme.typography.titleMedium,
									fontWeight = FontWeight.Bold,
									modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
								)
								Card(
									modifier = Modifier.fillMaxWidth(),
									colors = CardDefaults.cardColors(
										containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
											alpha = 0.2f
										)
									)
								) {
									Row(
										modifier = Modifier
											.fillMaxWidth()
											.clickable { showOnboardingDialog = true }
											.padding(horizontal = 16.dp, vertical = 16.dp),
										verticalAlignment = Alignment.CenterVertically
									) {
										Column(modifier = Modifier.weight(1f)) {
											Text(
												"Onboarding Screen",
												fontWeight = FontWeight.Medium
											)
											Text(
												"Re-run initial setup",
												style = MaterialTheme.typography.bodySmall,
												color = Color.Gray
											)
										}
										Icon(
											Icons.AutoMirrored.Filled.ArrowForwardIos,
											contentDescription = null,
											tint = Color.Gray,
											modifier = Modifier.size(18.dp)
										)
									}
									HorizontalDivider()
									Row(
										modifier = Modifier
											.fillMaxWidth()
											.padding(horizontal = 16.dp, vertical = 16.dp),
										verticalAlignment = Alignment.CenterVertically
									) {
										Column(modifier = Modifier.weight(1f)) {
											Text("Dummy Data Mode", fontWeight = FontWeight.Medium)
											Text(
												"Toggle demo data with sample activities",
												style = MaterialTheme.typography.bodySmall,
												color = Color.Gray
											)
										}
										Switch(
											checked = isDummyDataEnabled,
											onCheckedChange = { profileViewModel.toggleDummyData() }
										)
									}
									HorizontalDivider()
									Row(
										modifier = Modifier
											.fillMaxWidth()
											.clickable { showUserDataDebugDialog = true }
											.padding(horizontal = 16.dp, vertical = 16.dp),
										verticalAlignment = Alignment.CenterVertically
									) {
										Column(modifier = Modifier.weight(1f)) {
											Text("User Data Debug", fontWeight = FontWeight.Medium)
											Text(
												"View/Edit raw user data",
												style = MaterialTheme.typography.bodySmall,
												color = Color.Gray
											)
										}
										Icon(
											Icons.AutoMirrored.Filled.ArrowForwardIos,
											contentDescription = null,
											tint = Color.Gray,
											modifier = Modifier.size(18.dp)
										)
									}
								}
							}
						}
					}
				}
			}
		}

		if (showOnboardingDialog) {
			OnboardingDialog(
				onDismiss = { showOnboardingDialog = false },
				onboardingViewModel = onboardingViewModel
			)
		}

		if (showUserDataDebugDialog) {
			UserDataDebugDialog(onDismiss = { showUserDataDebugDialog = false })
		}

		if (showEditDialog) {
			EditUserDataDialog(
				editType = currentEditType,
				currentValue = currentEditValue,
				onDismiss = { showEditDialog = false },
				onSave = { newValue ->
					handleProfileUpdate(currentEditType, newValue)
				}
			)
		}

		if (showEditDataDialog) {
			userProfile?.let { profile ->
				when (currentEditType) {
					EditUserDataType.WEIGHT -> {
						EditUserDataDialog(
							editType = EditUserDataType.WEIGHT,
							currentValue = profile.weight.toString(),
							onDismiss = { showEditDataDialog = false },
							onSave = { handleValueUpdate(it) }
						)
					}

					EditUserDataType.HEIGHT -> {
						EditUserDataDialog(
							editType = EditUserDataType.HEIGHT,
							currentValue = profile.height.toString(),
							onDismiss = { showEditDataDialog = false },
							onSave = { handleValueUpdate(it) }
						)
					}

					EditUserDataType.AGE -> {
						EditUserDataDialog(
							editType = EditUserDataType.AGE,
							currentValue = profile.age.toString(),
							onDismiss = { showEditDataDialog = false },
							onSave = { handleValueUpdate(it) }
						)
					}

					else -> {}
				}
			}
		}

		if (showEditGoalDialog) {
			userProfile?.let { profile ->
				EditGoalDialog(
					currentGoal = profile.goalType,
					onDismiss = { showEditGoalDialog = false },
					onSave = { handleGoalUpdate(it) }
				)
			}
		}

		if (showEditActivityDialog) {
			userProfile?.let { profile ->
				EditActivityLevelDialog(
					currentActivityLevel = profile.activityLevel,
					onDismiss = { showEditActivityDialog = false },
					onSave = { handleActivityLevelUpdate(it) }
				)
			}
		}

		if (showCalorieSettingsDialog) {
			userProfile?.let { profile ->
				CalorieSettingsDialog(
					onDismiss = { showCalorieSettingsDialog = false },
					onSave = { granularityValue, strategy, advancedEnabled ->
						profileViewModel.updateCalorieSettings(
							granularityValue,
							strategy,
							advancedEnabled
						)
						showCalorieSettingsDialog = false
					},
					goalType = profile.goalType,
					userWeight = profile.weight.toDouble(),
					userHeight = profile.height.toDouble(),
					userAge = profile.age,
					isMale = profile.gender == "Male",
					activityLevel = profile.activityLevel,
					initialGranularityValue = todayDailyData?.granularityValue ?: 250,
					initialCalorieStrategy = todayDailyData?.calorieStrategy
						?: CalorieStrategy.MODERATE,
					initialAdvancedEnabled = todayDailyData?.advancedEnabled == true
				)
			}
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditGoalDialog(
	currentGoal: GoalType,
	onDismiss: () -> Unit,
	onSave: (GoalType) -> Unit
) {
	var selectedGoal by remember { mutableStateOf(currentGoal) }

	Dialog(onDismissRequest = onDismiss) {
		Card(
			modifier = Modifier
				.fillMaxWidth()
				.padding(16.dp),
			shape = MaterialTheme.shapes.large
		) {
			Column(
				modifier = Modifier.padding(24.dp)
			) {
				Text(
					text = "Change Goal",
					style = MaterialTheme.typography.headlineSmall,
					fontWeight = FontWeight.Medium,
					modifier = Modifier.padding(bottom = 24.dp)
				)

				GoalType.values().forEach { goal ->
					Row(
						modifier = Modifier
							.fillMaxWidth()
							.clickable { selectedGoal = goal }
							.padding(vertical = 8.dp),
						verticalAlignment = Alignment.CenterVertically
					) {
						RadioButton(
							selected = selectedGoal == goal,
							onClick = { selectedGoal = goal }
						)
						Spacer(modifier = Modifier.width(12.dp))
						Text(
							text = goal.getDisplayName(),
							style = MaterialTheme.typography.bodyLarge
						)
					}
				}

				Spacer(modifier = Modifier.height(24.dp))

				Row(
					modifier = Modifier.fillMaxWidth(),
					horizontalArrangement = Arrangement.End
				) {
					TextButton(onClick = onDismiss) {
						Text("Cancel")
					}
					Spacer(modifier = Modifier.width(8.dp))
					Button(onClick = { onSave(selectedGoal) }) {
						Text("Save")
					}
				}
			}
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditActivityLevelDialog(
	currentActivityLevel: ActivityLevel,
	onDismiss: () -> Unit,
	onSave: (ActivityLevel) -> Unit
) {
	var selectedLevel by remember { mutableStateOf(currentActivityLevel) }

	Dialog(onDismissRequest = onDismiss) {
		Card(
			modifier = Modifier
				.fillMaxWidth()
				.padding(16.dp),
			shape = MaterialTheme.shapes.large
		) {
			Column(
				modifier = Modifier.padding(24.dp)
			) {
				Text(
					text = "Change Activity Level",
					style = MaterialTheme.typography.headlineSmall,
					fontWeight = FontWeight.Medium,
					modifier = Modifier.padding(bottom = 24.dp)
				)

				ActivityLevel.values().forEach { level ->
					Row(
						modifier = Modifier
							.fillMaxWidth()
							.clickable { selectedLevel = level }
							.padding(vertical = 4.dp),
						verticalAlignment = Alignment.CenterVertically
					) {
						RadioButton(
							selected = selectedLevel == level,
							onClick = { selectedLevel = level },
							modifier = Modifier.size(20.dp)
						)
						Spacer(modifier = Modifier.width(12.dp))
						Text(
							text = level.getDisplayName(),
							style = MaterialTheme.typography.bodyMedium
						)
					}
				}

				Spacer(modifier = Modifier.height(24.dp))

				Row(
					modifier = Modifier.fillMaxWidth(),
					horizontalArrangement = Arrangement.End
				) {
					TextButton(onClick = onDismiss) {
						Text("Cancel")
					}
					Spacer(modifier = Modifier.width(8.dp))
					Button(onClick = { onSave(selectedLevel) }) {
						Text("Save")
					}
				}
			}
		}
	}
}

@Composable
fun ProfileSettingItem(
	icon: ImageVector? = null,
	label: String,
	value: String,
	onClick: () -> Unit,
	isEditable: Boolean = true
) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.let { if (isEditable) it.clickable { onClick() } else it }
			.padding(horizontal = 16.dp, vertical = 18.dp),
		verticalAlignment = Alignment.CenterVertically
	) {
		if (icon != null) {
			Icon(
				icon,
				contentDescription = null,
				tint = if (isEditable) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(
					alpha = 0.6f
				),
				modifier = Modifier.size(24.dp)
			)
			Spacer(modifier = Modifier.width(16.dp))
		}
		Column(modifier = Modifier.weight(1f)) {
			Text(
				label,
				fontWeight = FontWeight.Medium,
				color = if (isEditable) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(
					alpha = 0.6f
				)
			)
			Text(
				value,
				style = MaterialTheme.typography.bodySmall,
				color = if (isEditable) Color.Gray else Color.Gray.copy(alpha = 0.6f)
			)
		}
		if (isEditable) {
			Icon(
				Icons.AutoMirrored.Filled.ArrowForwardIos,
				contentDescription = null,
				tint = Color.Gray,
				modifier = Modifier.size(18.dp)
			)
		}
	}
}

//@Preview(showBackground = true)
//@Composable
//fun ProfileSettingItemPreview() {
//	MaterialTheme {
//		ProfileSettingItem(
//			icon = Icons.Filled.MonitorWeight,
//			label = "Weight",
//			value = "70kg",
//			onClick = {}
//		)
//	}
//}
//
//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun ProfileSettingsPreview() {
//	PencatatanKaloriTheme {
//		ProfileSettings()
//	}
//}