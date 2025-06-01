package com.ralvin.pencatatankalori.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.ralvin.pencatatankalori.ui.components.EditUserDataDialog
import com.ralvin.pencatatankalori.ui.components.EditUserDataType
import com.ralvin.pencatatankalori.ui.components.OnboardingDialog
import com.ralvin.pencatatankalori.ui.components.UserDataDebugDialog

@Preview(showBackground = true)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSettings() {
    var showOnboardingDialog by remember { mutableStateOf(false) }
    var showUserDataDebugDialog by remember { mutableStateOf(false) }

    var showEditDialog by remember { mutableStateOf(false) }
    var currentEditType by remember { mutableStateOf(EditUserDataType.WEIGHT) }
    var currentEditValue by remember { mutableStateOf("") }

    // dummy data for profile settings, TODO: replace with actual data
    val profileData = remember {
        mutableStateMapOf(
            EditUserDataType.ACTIVE_LEVEL to "Sedentary",
            EditUserDataType.GOAL to "Gain Weight",
            EditUserDataType.WEIGHT to "50",
            EditUserDataType.HEIGHT to "170",
            EditUserDataType.AGE to "25",
            EditUserDataType.GENDER to "Male"
        )
    }

    fun openEditDialog(type: EditUserDataType) {
        currentEditType = type
        currentEditValue = profileData[type] ?: ""
        showEditDialog = true
    }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        TopAppBar(
            title = {
                Text(
                    text = "Profile Settings",
                    fontWeight = FontWeight.Medium,
                    fontSize = 22.sp
                )
            }
        )
        Spacer(modifier = Modifier.height(8.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
        ) {
            ProfileSettingItem(icon = Icons.Filled.FitnessCenter, label = "Active Level", value = profileData[EditUserDataType.ACTIVE_LEVEL]!!, onClick = { openEditDialog(EditUserDataType.ACTIVE_LEVEL) })
            Divider()
            ProfileSettingItem(icon = Icons.Filled.Flag, label = "Goal", value = profileData[EditUserDataType.GOAL]!!, onClick = { openEditDialog(EditUserDataType.GOAL) })
            Divider()
            ProfileSettingItem(icon = Icons.Filled.MonitorWeight, label = "Weight", value = "${profileData[EditUserDataType.WEIGHT]!!}kg", onClick = { openEditDialog(EditUserDataType.WEIGHT) })
            Divider()
            ProfileSettingItem(icon = Icons.Filled.Height, label = "Height", value = "${profileData[EditUserDataType.HEIGHT]!!}cm", onClick = { openEditDialog(EditUserDataType.HEIGHT) })
            Divider()
            ProfileSettingItem(icon = Icons.Filled.Cake, label = "Age", value = "${profileData[EditUserDataType.AGE]!!} Years Old", onClick = { openEditDialog(EditUserDataType.AGE) })
            Divider()
            ProfileSettingItem(icon = Icons.Filled.People, label = "Gender", value = profileData[EditUserDataType.GENDER]!!, onClick = { openEditDialog(EditUserDataType.GENDER) })
        }
        Spacer(modifier = Modifier.height(16.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
        ) {
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
            Divider()
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
        if (showOnboardingDialog) {
            OnboardingDialog(onDismiss = { showOnboardingDialog = false })
        }

        if (showUserDataDebugDialog) {
            UserDataDebugDialog(onDismiss = { showUserDataDebugDialog = false })
        }

        if (showEditDialog) {
            EditUserDataDialog(
                editType = currentEditType,
                currentValue = currentEditValue,
                onDismiss = { showEditDialog = false },
                onSave = {
                    profileData[currentEditType] = it
                    showEditDialog = false
                }
            )
        }
    }
}

@Composable
fun ProfileSettingItem(icon: ImageVector, label: String, value: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(label, fontWeight = FontWeight.Medium)
            Text(value, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
        Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(18.dp))
    }
}