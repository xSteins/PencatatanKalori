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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ralvin.pencatatankalori.model.formula.ActivityLevel
import com.ralvin.pencatatankalori.model.formula.GoalType
import com.ralvin.pencatatankalori.model.formula.MifflinModel
import com.ralvin.pencatatankalori.view.components.EditUserDataDialog
import com.ralvin.pencatatankalori.view.components.EditUserDataType
import com.ralvin.pencatatankalori.view.components.HistoryScreen.UserDataDebugDialog
import com.ralvin.pencatatankalori.view.components.Onboarding.InitialOnboardingBottomSheet
import com.ralvin.pencatatankalori.view.components.Onboarding.OnboardingDialog
import com.ralvin.pencatatankalori.view.components.ProfileScreen.CalorieSettingsDialog
import com.ralvin.pencatatankalori.view.components.ProfileScreen.ProfileSettingItem
import com.ralvin.pencatatankalori.view.components.Tooltip
import com.ralvin.pencatatankalori.viewmodel.OnboardingViewModel
import com.ralvin.pencatatankalori.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSettings(
	profileViewModel: ProfileViewModel = hiltViewModel(),
	onboardingViewModel: OnboardingViewModel = hiltViewModel()
) {
	var showOnboardingDialog by remember { mutableStateOf(false) }
	var showInitialBottomSheet by remember { mutableStateOf(false) }
	var showUserDataDebugDialog by remember { mutableStateOf(false) }
	var showCalorieSettingsDialog by remember { mutableStateOf(false) }

	var showEditDataDialog by remember { mutableStateOf(false) }
	var showEditDialog by remember { mutableStateOf(false) }
	var currentEditType by remember { mutableStateOf(EditUserDataType.WEIGHT) }
	var currentEditValue by remember { mutableStateOf("") }

	val userProfile by profileViewModel.userProfile.collectAsStateWithLifecycle()
	val isOnboardingCompleted by onboardingViewModel.isCompleted.collectAsStateWithLifecycle()
	val isDummyDataEnabled by profileViewModel.isDummyDataEnabled.collectAsStateWithLifecycle()
	val todayDailyData by profileViewModel.todayDailyData.collectAsStateWithLifecycle()

	LaunchedEffect(isOnboardingCompleted, userProfile) {
		if (isOnboardingCompleted && userProfile != null) {
			showOnboardingDialog = false
			onboardingViewModel.resetState()
			profileViewModel.markOnboardingComplete()
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
						profileViewModel.updateUserProfile(
							age = it.age,
							gender = it.gender,
							weight = floatValue,
							height = it.height,
							activityLevel = it.activityLevel,
							goalType = it.goalType,
							dailyCalorieTarget = it.dailyCalorieTarget
						)
					}
				}

				EditUserDataType.HEIGHT -> {
					value.toFloatOrNull()?.let { floatValue ->
						profileViewModel.updateUserProfile(
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
		val profile = userProfile
		val goalType = GoalType.entries.find { it.getDisplayName() == value }
		profile?.let { userProfile ->
			goalType?.let {
				profileViewModel.updateUserProfile(
					age = userProfile.age,
					gender = userProfile.gender,
					weight = userProfile.weight,
					height = userProfile.height,
					activityLevel = userProfile.activityLevel,
					goalType = it,
					dailyCalorieTarget = userProfile.dailyCalorieTarget
				)
			}
		}
		showEditDataDialog = false
	}

	fun handleActivityLevelUpdate(value: String) {
		val profile = userProfile
		val activityLevel = ActivityLevel.entries.find { it.getDisplayName() == value }
		profile?.let { userProfile ->
			activityLevel?.let {
				profileViewModel.updateUserProfile(
					age = userProfile.age,
					gender = userProfile.gender,
					weight = userProfile.weight,
					height = userProfile.height,
					activityLevel = it,
					goalType = userProfile.goalType,
					dailyCalorieTarget = userProfile.dailyCalorieTarget
				)
			}
		}
		showEditDataDialog = false
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
							label = "Tingkat Aktivitas",
							value = currentUserProfile?.activityLevel?.getDisplayName()
								?: "Belum ada data",
							onClick = if (hasUserData) {
								{ openEditDialog(EditUserDataType.ACTIVE_LEVEL) }
							} else {
								{}
							},
							isEditable = hasUserData,
							tooltipMessage = if (!hasUserData) {
								"Mohon lakukan proses onboarding pada halaman \"Profile\" -> Onboarding Screen"
							} else null
						)
						HorizontalDivider()
						ProfileSettingItem(
							icon = Icons.Filled.Flag,
							label = "Tujuan",
							value = currentUserProfile?.goalType?.getDisplayName()
								?: "Belum ada data",
							onClick = if (hasUserData) {
								{ openEditDialog(EditUserDataType.GOAL) }
							} else {
								{}
							},
							isEditable = hasUserData,
							tooltipMessage = if (!hasUserData) {
								"Mohon lakukan proses onboarding pada halaman \"Profile\" -> Onboarding Screen"
							} else null
						)
						HorizontalDivider()
						ProfileSettingItem(
							icon = Icons.Filled.MonitorWeight,
							label = "Berat Badan",
							value = currentUserProfile?.let { "${it.weight}kg" } ?: "Belum ada data",
							onClick = if (hasUserData) {
								{ openEditDialog(EditUserDataType.WEIGHT) }
							} else {
								{}
							},
							isEditable = hasUserData,
							tooltipMessage = if (!hasUserData) {
								"Mohon lakukan proses onboarding pada halaman \"Profile\" -> Onboarding Screen"
							} else null
						)
						HorizontalDivider()
						ProfileSettingItem(
							icon = Icons.Filled.Height,
							label = "Tinggi Badan",
							value = currentUserProfile?.let { "${it.height}cm" } ?: "Belum ada data",
							onClick = if (hasUserData) {
								{ openEditDialog(EditUserDataType.HEIGHT) }
							} else {
								{}
							},
							isEditable = hasUserData,
							tooltipMessage = if (!hasUserData) {
								"Mohon lakukan proses onboarding pada halaman \"Profile\" -> Onboarding Screen"
							} else null
						)
						HorizontalDivider()
						ProfileSettingItem(
							icon = Icons.Filled.Cake,
							label = "Umur",
							value = currentUserProfile?.let { "${it.age} Tahun" }
								?: "Belum ada data",
							onClick = if (hasUserData) {
								{ openEditDialog(EditUserDataType.AGE) }
							} else {
								{}
							},
							isEditable = hasUserData,
							tooltipMessage = if (!hasUserData) {
								"Mohon lakukan proses onboarding pada halaman \"Profile\" -> Onboarding Screen"
							} else null
						)
						HorizontalDivider()
						ProfileSettingItem(
							icon = Icons.Filled.People,
							label = "Jenis Kelamin",
							value = currentUserProfile?.let { if (it.gender == "Male") "Pria" else "Wanita" }
								?: "Belum ada data",
							onClick = if (hasUserData) {
								{ openEditDialog(EditUserDataType.GENDER) }
							} else {
								{}
							},
							isEditable = hasUserData,
							tooltipMessage = if (!hasUserData) {
								"Mohon lakukan proses onboarding pada halaman \"Profile\" -> Onboarding Screen"
							} else null
						)
						HorizontalDivider()
						ProfileSettingItem(
							icon = Icons.Filled.Tune,
							label = "Pengaturan Kalori",
							value = "Granularitas / Nilai Kompensasi: ${MifflinModel.getGranularityValue()}",
							onClick = if (hasUserData) {
								{ showCalorieSettingsDialog = true }
							} else {
								{}
							},
							isEditable = hasUserData,
							tooltipMessage = if (!hasUserData) {
								"Mohon lakukan proses onboarding pada halaman \"Profile\" -> Onboarding Screen"
							} else null
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
									.clickable { showInitialBottomSheet = true }
									.padding(horizontal = 16.dp, vertical = 16.dp),
								verticalAlignment = Alignment.CenterVertically
							) {
								Column(modifier = Modifier.weight(1f)) {
									Text(
										"Onboarding Screen",
										fontWeight = FontWeight.Medium
									)
									Text(
										"Fitur untuk inisialisasi aplikasi",
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

		if (showOnboardingDialog) {
			OnboardingDialog(
				onDismiss = { showOnboardingDialog = false },
				onboardingViewModel = onboardingViewModel
			)
		}

		if (showInitialBottomSheet) {
			InitialOnboardingBottomSheet(
				onDismiss = { showInitialBottomSheet = false },
				onSkip = { showInitialBottomSheet = false },
				onFillData = {
					showInitialBottomSheet = false
					showOnboardingDialog = true
				}
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
					onSave = { granularityValue ->
						profileViewModel.updateCalorieSettings(granularityValue)
						showCalorieSettingsDialog = false
					},
					initialGranularityValue = todayDailyData?.granularityValue ?: 0
				)
			}
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
