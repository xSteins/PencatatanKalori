package com.ralvin.pencatatankalori.view.screens

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ralvin.pencatatankalori.Model.formula.ActivityLevel
import com.ralvin.pencatatankalori.Model.formula.CalorieStrategy
import com.ralvin.pencatatankalori.Model.formula.GoalType
import com.ralvin.pencatatankalori.Model.formula.MifflinModel
import com.ralvin.pencatatankalori.R
import com.ralvin.pencatatankalori.view.components.CalorieSettingsDialog
import com.ralvin.pencatatankalori.view.components.EditUserDataDialog
import com.ralvin.pencatatankalori.view.components.EditUserDataType
import com.ralvin.pencatatankalori.view.components.OnboardingDialog
import com.ralvin.pencatatankalori.view.components.UserDataDebugDialog
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
				EditUserDataType.WEIGHT, EditUserDataType.HEIGHT, EditUserDataType.AGE, EditUserDataType.GOAL, EditUserDataType.ACTIVE_LEVEL -> {
					showEditDataDialog = true
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

	fun handleGoalUpdate(value: String) {
		val goalType = GoalType.values().find { it.getDisplayName() == value }
		goalType?.let {
			profileViewModel.updateGoalType(it)
		}
		showEditDataDialog = false
	}

	fun handleActivityLevelUpdate(value: String) {
		val activityLevel = ActivityLevel.values().find { it.getDisplayName() == value }
		activityLevel?.let {
			profileViewModel.updateActivityLevel(it)
		}
		showEditDataDialog = false
	}

	Scaffold(
		topBar = {
			TopAppBar(
				title = {
					Text(
						text = stringResource(R.string.pengaturan_profil),
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
									label = stringResource(R.string.tingkat_keaktifan),
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
									label = stringResource(R.string.tujuan),
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
									label = stringResource(R.string.berat_badan),
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
									label = stringResource(R.string.tinggi_badan),
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
									label = stringResource(R.string.umur),
									value = currentUserProfile?.let { "${it.age} ${stringResource(R.string.tahun)}" }
										?: "25 ${stringResource(R.string.tahun)}",
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
									label = stringResource(R.string.jenis_kelamin),
									value = currentUserProfile?.let { if (it.gender == "Male") stringResource(R.string.pria) else stringResource(R.string.wanita) }
										?: stringResource(R.string.pria),
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
									label = stringResource(R.string.pengaturan_kalori),
									value = if (MifflinModel.isAdvancedEnabled()) {
										stringResource(R.string.advanced_calorie_format, MifflinModel.getCalorieStrategy().displayName, MifflinModel.getGranularityValue())
									} else {
										stringResource(R.string.basic_calorie_format, MifflinModel.getGranularityValue())
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
									text = stringResource(R.string.debugging),
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
												stringResource(R.string.onboarding_screen),
												fontWeight = FontWeight.Medium
											)
											Text(
												stringResource(R.string.rerun_initial_setup),
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
											Text(stringResource(R.string.dummy_data_mode), fontWeight = FontWeight.Medium)
											Text(
												stringResource(R.string.toggle_demo_data),
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
											Text(stringResource(R.string.user_data_debug), fontWeight = FontWeight.Medium)
											Text(
												stringResource(R.string.view_edit_raw_data),
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

					EditUserDataType.GOAL -> {
						EditUserDataDialog(
							editType = EditUserDataType.GOAL,
							currentValue = profile.goalType.getDisplayName(),
							onDismiss = { showEditDataDialog = false },
							onSave = { handleGoalUpdate(it) }
						)
					}

					EditUserDataType.ACTIVE_LEVEL -> {
						EditUserDataDialog(
							editType = EditUserDataType.ACTIVE_LEVEL,
							currentValue = profile.activityLevel.getDisplayName(),
							onDismiss = { showEditDataDialog = false },
							onSave = { handleActivityLevelUpdate(it) }
						)
					}

					else -> {}
				}
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
