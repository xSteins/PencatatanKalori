package com.ralvin.pencatatankalori.view.components.OverviewScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun CalorieInfoRow(
	label: String,
	value: Int,
	progressBarColor: Color,
	target: Int? = null,
	showTargetInValue: Boolean = true,
	onClick: (() -> Unit)? = null
) {
	Column(
		modifier = Modifier
			.fillMaxWidth()
			.clickable(enabled = onClick != null) { onClick?.invoke() }
	) {
		Row(
			verticalAlignment = Alignment.CenterVertically,
			modifier = Modifier.fillMaxWidth()
		) {
			Text(
				text = if (showTargetInValue && target != null && target > 0) "$value / $target" else "$value",
				style = MaterialTheme.typography.headlineMedium,
				fontWeight = FontWeight.Bold
			)
			Spacer(modifier = Modifier.width(8.dp))
			Text(
				text = label,
				style = MaterialTheme.typography.bodyLarge
			)
			if (onClick != null) {
				Spacer(modifier = Modifier.width(4.dp))
				Icon(
					imageVector = Icons.Default.Info,
					contentDescription = "Info",
					modifier = Modifier.size(20.dp),
					tint = MaterialTheme.colorScheme.primary
				)
			}
		}
		LinearProgressIndicator(
			progress = {
				when {
					target != null && target > 0 -> {
						val clampedValue = value.coerceAtLeast(0)
						minOf(1.0f, clampedValue.toFloat() / target.toFloat())
					}

					value > 0 -> minOf(
						1.0f,
						value.toFloat() / 2000f // set 2000 karena weird behavior kalau pakai dynamic value
					)
					else -> 0.0f
				}
			},
			modifier = Modifier
				.fillMaxWidth()
				.height(8.dp),
			color = progressBarColor,
			trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f)
		)
	}
}