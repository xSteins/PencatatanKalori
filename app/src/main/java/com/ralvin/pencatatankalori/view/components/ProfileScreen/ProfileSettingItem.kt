package com.ralvin.pencatatankalori.view.components.ProfileScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.ralvin.pencatatankalori.view.components.Tooltip

@Composable
fun ProfileSettingItem(
	icon: ImageVector? = null,
	label: String,
	value: String,
	onClick: () -> Unit,
	isEditable: Boolean = true,
	tooltipMessage: String? = null
) {
	var showTooltip by remember { mutableStateOf(false) }

	Row(
		modifier = Modifier
			.fillMaxWidth()
			.let {
				if (isEditable) {
					it.clickable { onClick() }
				} else if (tooltipMessage != null) {
					it.clickable { showTooltip = true }
				} else {
					it
				}
			}
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

	if (showTooltip && tooltipMessage != null) {
		Tooltip(
			message = tooltipMessage,
			onDismiss = { showTooltip = false }
		)
	}
}
