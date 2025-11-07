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
import com.ralvin.pencatatankalori.model.formula.GoalType

/**
 * Independent composable that displays physical information (weight, height, goal type)
 * with clickable items that trigger callbacks for editing.
 *
 * @param weight Current weight in kg (nullable)
 * @param height Current height in cm (nullable)
 * @param goalType Current goal type
 * @param onEditWeight Callback when weight is clicked
 * @param onEditHeight Callback when height is clicked
 * @param onEditGoal Callback when goal is clicked
 * @param modifier Modifier for styling
 */
@Composable
fun PhysicalInfoText(
	weight: Float?,
	height: Float?,
	goalType: GoalType,
	onEditWeight: () -> Unit = {},
	onEditHeight: () -> Unit = {},
	onEditGoal: () -> Unit = {},
	modifier: Modifier = Modifier
) {
	Row(
		modifier = modifier,
		horizontalArrangement = Arrangement.spacedBy(4.dp)
	) {
		height?.let {
			Text(
				text = "TB: ${it}cm",
				style = MaterialTheme.typography.bodyMedium,
				color = MaterialTheme.colorScheme.primary,
				textDecoration = TextDecoration.Underline,
				fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
				modifier = Modifier.clickable { onEditHeight() }
			)
			Text(
				text = "|",
				style = MaterialTheme.typography.bodyMedium,
				color = MaterialTheme.colorScheme.primary,
				fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
			)
		}

		weight?.let {
			Text(
				text = "BB: ${it}kg",
				style = MaterialTheme.typography.bodyMedium,
				color = MaterialTheme.colorScheme.primary,
				textDecoration = TextDecoration.Underline,
				fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
				modifier = Modifier.clickable { onEditWeight() }
			)
			Text(
				text = "|",
				style = MaterialTheme.typography.bodyMedium,
				color = MaterialTheme.colorScheme.primary,
				fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
			)
		}

		Text(
			text = goalType.getShortDisplayName(),
			style = MaterialTheme.typography.bodyMedium,
			color = MaterialTheme.colorScheme.primary,
			textDecoration = TextDecoration.Underline,
			fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
			modifier = Modifier.clickable { onEditGoal() }
		)
	}
}
