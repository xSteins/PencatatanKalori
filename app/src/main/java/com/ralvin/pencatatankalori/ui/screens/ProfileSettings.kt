package com.ralvin.pencatatankalori.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ralvin.pencatatankalori.ui.components.EditUserDataDialog
import com.ralvin.pencatatankalori.ui.components.EditUserDataType
import com.ralvin.pencatatankalori.ui.components.OnboardingDialog
import com.ralvin.pencatatankalori.ui.components.UserDataDebugDialog
import com.ralvin.pencatatankalori.ui.components.CalorieAdjustmentDialog
import com.ralvin.pencatatankalori.viewmodel.ProfileViewModel
import com.ralvin.pencatatankalori.viewmodel.OnboardingViewModel
import com.ralvin.pencatatankalori.health.model.ActivityLevel
import com.ralvin.pencatatankalori.health.model.GoalType
import com.ralvin.pencatatankalori.health.model.MifflinModel
import com.ralvin.pencatatankalori.ui.theme.PencatatanKaloriTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSettings(
    profileViewModel: ProfileViewModel = hiltViewModel(),
    onboardingViewModel: OnboardingViewModel = hiltViewModel()
) {
    var showOnboardingDialog by remember { mutableStateOf(false) }
    var showUserDataDebugDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showCalorieAdjustmentDialog by remember { mutableStateOf(false) }
    var currentEditType by remember { mutableStateOf(EditUserDataType.WEIGHT) }
    var currentEditValue by remember { mutableStateOf("") }

    // Collect user profile from ProfileViewModel
    val userProfile by profileViewModel.userProfile.collectAsStateWithLifecycle()
    val profileUiState by profileViewModel.uiState.collectAsStateWithLifecycle()
    val onboardingUiState by onboardingViewModel.uiState.collectAsStateWithLifecycle()
    val isDummyDataEnabled by profileViewModel.isDummyDataEnabled.collectAsStateWithLifecycle()

    // Check if user exists and show onboarding dialog automatically
    LaunchedEffect(Unit) {
        // Check if user actually exists in database (not affected by dummy data)
        val userExists = profileViewModel.checkUserExists()
        if (!userExists) {
            showOnboardingDialog = true
        }
    }

    // Handle onboarding success
    LaunchedEffect(onboardingUiState) {
        val currentOnboardingState = onboardingUiState
        if (currentOnboardingState is com.ralvin.pencatatankalori.viewmodel.OnboardingUiState.Success) {
            showOnboardingDialog = false
            onboardingViewModel.resetUiState()
        }
    }

    fun openEditDialog(type: EditUserDataType) {
        val profile = userProfile
        profile?.let {
            currentEditType = type
            currentEditValue = when (type) {
                EditUserDataType.WEIGHT -> it.weight.toString()
                EditUserDataType.HEIGHT -> it.height.toString()
                EditUserDataType.AGE -> it.age.toString()
                EditUserDataType.GENDER -> it.gender
                EditUserDataType.ACTIVE_LEVEL -> it.activityLevel.getDisplayName()
                EditUserDataType.GOAL -> it.goalType.getDisplayName()
            }
            showEditDialog = true
        }
    }

    fun handleProfileUpdate(type: EditUserDataType, newValue: String) {
        val profile = userProfile
        profile?.let {
            when (type) {
                EditUserDataType.WEIGHT -> {
                    newValue.toFloatOrNull()?.let { profileViewModel.updateWeight(it) }
                }
                EditUserDataType.HEIGHT -> {
                    newValue.toFloatOrNull()?.let { height ->
                        profileViewModel.updateUserProfile(
                            name = it.name,
                            age = it.age,
                            gender = it.gender,
                            weight = it.weight,
                            height = height,
                            activityLevel = it.activityLevel,
                            goalType = it.goalType,
                            dailyCalorieTarget = it.dailyCalorieTarget
                        )
                    }
                }
                EditUserDataType.AGE -> {
                    newValue.toIntOrNull()?.let { age ->
                        profileViewModel.updateUserProfile(
                            name = it.name,
                            age = age,
                            gender = it.gender,
                            weight = it.weight,
                            height = it.height,
                            activityLevel = it.activityLevel,
                            goalType = it.goalType,
                            dailyCalorieTarget = it.dailyCalorieTarget
                        )
                    }
                }
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
                EditUserDataType.ACTIVE_LEVEL -> {
                    ActivityLevel.values().find { activityLevel -> activityLevel.getDisplayName() == newValue }?.let { activityLevel ->
                        profileViewModel.updateActivityLevel(activityLevel)
                    }
                }
                EditUserDataType.GOAL -> {
                    GoalType.values().find { goalType -> goalType.getDisplayName() == newValue }?.let { goalType ->
                        profileViewModel.updateGoalType(goalType)
                    }
                }
            }
        }
        showEditDialog = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Profile Settings",
                        fontWeight = FontWeight.Medium,
                        fontSize = 22.sp
                    )
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            // Show loading state or profile data
            val currentProfileUiState = profileUiState
            when {
                currentProfileUiState is com.ralvin.pencatatankalori.viewmodel.ProfileUiState.Loading -> {
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
                        // Profile Settings Section
                        item {
                            // Always show profile fields, but make them non-editable if no data
                            val currentUserProfile = userProfile
                            val hasUserData = currentUserProfile != null
                            
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
                            ) {
                                ProfileSettingItem(
                                    icon = Icons.Filled.FitnessCenter,
                                    label = "Active Level",
                                    value = currentUserProfile?.activityLevel?.getDisplayName() ?: "Sedentary",
                                    onClick = if (hasUserData) { { openEditDialog(EditUserDataType.ACTIVE_LEVEL) } } else { {} },
                                    isEditable = hasUserData
                                )
                                HorizontalDivider()
                                ProfileSettingItem(
                                    icon = Icons.Filled.Flag,
                                    label = "Goal",
                                    value = currentUserProfile?.goalType?.getDisplayName() ?: "Gain Weight",
                                    onClick = if (hasUserData) { { openEditDialog(EditUserDataType.GOAL) } } else { {} },
                                    isEditable = hasUserData
                                )
                                HorizontalDivider()
                                ProfileSettingItem(
                                    icon = Icons.Filled.MonitorWeight,
                                    label = "Weight",
                                    value = currentUserProfile?.let { "${it.weight}kg" } ?: "50kg",
                                    onClick = if (hasUserData) { { openEditDialog(EditUserDataType.WEIGHT) } } else { {} },
                                    isEditable = hasUserData
                                )
                                HorizontalDivider()
                                ProfileSettingItem(
                                    icon = Icons.Filled.Height,
                                    label = "Height",
                                    value = currentUserProfile?.let { "${it.height}cm" } ?: "170cm",
                                    onClick = if (hasUserData) { { openEditDialog(EditUserDataType.HEIGHT) } } else { {} },
                                    isEditable = hasUserData
                                )
                                HorizontalDivider()
                                ProfileSettingItem(
                                    icon = Icons.Filled.Cake,
                                    label = "Age",
                                    value = currentUserProfile?.let { "${it.age} Years Old" } ?: "25 Years Old",
                                    onClick = if (hasUserData) { { openEditDialog(EditUserDataType.AGE) } } else { {} },
                                    isEditable = hasUserData
                                )
                                HorizontalDivider()
                                ProfileSettingItem(
                                    icon = Icons.Filled.People,
                                    label = "Gender",
                                    value = currentUserProfile?.gender ?: "Male",
                                    onClick = if (hasUserData) { { openEditDialog(EditUserDataType.GENDER) } } else { {} },
                                    isEditable = hasUserData
                                )
                                HorizontalDivider()
                                ProfileSettingItem(
                                    icon = Icons.Filled.Tune,
                                    label = "Calorie Adjustment",
                                    value = "${MifflinModel.getGranularityValue()} cal",
                                    onClick = { showCalorieAdjustmentDialog = true },
                                    isEditable = true  // Always allow calorie adjustment
                                )
                            }
                        }
                        
                        // Debugging Section
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
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("Dummy Data Mode", fontWeight = FontWeight.Medium)
                                            Text("Toggle demo data with sample activities", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
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
                                            Text("View/Edit raw user data", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                        }
                                        Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(18.dp))
                                    }
                                    HorizontalDivider()
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { showOnboardingDialog = true }
                                            .padding(horizontal = 16.dp, vertical = 16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("Onboarding Screen", fontWeight = FontWeight.Medium)
                                            Text("Re-run initial setup", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                        }
                                        Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        
        // Dialogs
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

        if (showCalorieAdjustmentDialog) {
            CalorieAdjustmentDialog(
                onDismiss = { showCalorieAdjustmentDialog = false },
                onSave = { newValue ->
                    MifflinModel.adjustTargetCalorie(newValue)
                }
            )
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
                tint = if (isEditable) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), 
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                label, 
                fontWeight = FontWeight.Medium,
                color = if (isEditable) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Text(
                value, 
                style = MaterialTheme.typography.bodySmall, 
                color = if (isEditable) Color.Gray else Color.Gray.copy(alpha = 0.6f)
            )
        }
        if (isEditable) {
            Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(18.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileSettingItemPreview() {
    MaterialTheme {
        ProfileSettingItem(
            icon = Icons.Filled.MonitorWeight,
            label = "Weight",
            value = "70kg",
            onClick = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProfileSettingsPreview() {
    PencatatanKaloriTheme {
        ProfileSettings()
    }
}