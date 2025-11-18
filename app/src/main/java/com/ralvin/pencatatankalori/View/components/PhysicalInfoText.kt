package com.ralvin.pencatatankalori.view.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.ralvin.pencatatankalori.model.formula.ActivityLevel
import com.ralvin.pencatatankalori.model.formula.GoalType

@Composable
fun PhysicalInfoText(
	weight: Float?,
	activityLevel: ActivityLevel?,
	goalType: GoalType,
	onEditWeight: () -> Unit = {},
	onEditActiveLevel: () -> Unit = {},
	onEditGoal: () -> Unit = {},
	enabled: Boolean = true
) {
	val textColor = if (enabled) {
		MaterialTheme.colorScheme.primary
	} else {
		MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
	}
	val textDecoration = if (enabled) TextDecoration.Underline else TextDecoration.None

	Row(
		horizontalArrangement = Arrangement.spacedBy(4.dp)
	) {
		activityLevel?.let {
			Text(
				text = it.getDisplayName(),
				style = MaterialTheme.typography.bodyMedium,
				color = textColor,
				textDecoration = textDecoration,
				fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
				modifier = if (enabled) Modifier.clickable { onEditActiveLevel() } else Modifier
			)
			Text(
				text = "|",
				style = MaterialTheme.typography.bodyMedium,
				color = textColor,
				fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
			)
		}

		weight?.let {
			Text(
				text = "BB: ${it}kg",
				style = MaterialTheme.typography.bodyMedium,
				color = textColor,
				textDecoration = textDecoration,
				fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
				modifier = if (enabled) Modifier.clickable { onEditWeight() } else Modifier
			)
			Text(
				text = "|",
				style = MaterialTheme.typography.bodyMedium,
				color = textColor,
				fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
			)
		}

		Text(
			text = goalType.getShortDisplayName(),
			style = MaterialTheme.typography.bodyMedium,
			color = textColor,
			textDecoration = textDecoration,
			fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
			modifier = if (enabled) Modifier.clickable { onEditGoal() } else Modifier
		)
	}
}
