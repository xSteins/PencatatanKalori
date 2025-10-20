package com.ralvin.pencatatankalori.View.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ExpandableFAB(
	onFoodClick: () -> Unit,
	onWorkoutClick: () -> Unit,
	modifier: Modifier = Modifier
) {
	var isExpanded by remember { mutableStateOf(false) }

	val rotation by animateFloatAsState(
		targetValue = if (isExpanded) 45f else 0f,
		animationSpec = tween(300),
		label = "rotation"
	)

	Column(
		modifier = modifier,
		horizontalAlignment = Alignment.End,
		verticalArrangement = Arrangement.spacedBy(8.dp)
	) {
		if (isExpanded) {
			ExpandedFABItem(
				icon = Icons.Filled.FitnessCenter,
				label = "Aktivitas",
				onClick = {
					onWorkoutClick()
					isExpanded = false
				},
				backgroundColor = MaterialTheme.colorScheme.secondary
			)

			ExpandedFABItem(
				icon = Icons.Filled.Restaurant,
				label = "Konsumsi",
				onClick = {
					onFoodClick()
					isExpanded = false
				},
				backgroundColor = MaterialTheme.colorScheme.primary
			)
		}

		ExtendedFloatingActionButton(
			onClick = { isExpanded = !isExpanded },
			containerColor = MaterialTheme.colorScheme.primary,
			contentColor = MaterialTheme.colorScheme.onPrimary,
			expanded = !isExpanded,
			icon = {
				Icon(
					Icons.Filled.Add,
					contentDescription = "Tambah Data Manual",
					modifier = Modifier.rotate(rotation),
					tint = MaterialTheme.colorScheme.onPrimary
				)
			},
			text = {
				Text(
					text = "Tambah Data Manual",
					fontWeight = FontWeight.Medium,
					color = MaterialTheme.colorScheme.onPrimary
				)
			}
		)
	}
}

@Composable
private fun ExpandedFABItem(
	icon: androidx.compose.ui.graphics.vector.ImageVector,
	label: String,
	onClick: () -> Unit,
	backgroundColor: Color,
	modifier: Modifier = Modifier
) {
	Row(
		modifier = modifier,
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(8.dp)
	) {
		Card(
			modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
			shape = CircleShape,
			colors = CardDefaults.cardColors(
				containerColor = MaterialTheme.colorScheme.surface,
				contentColor = MaterialTheme.colorScheme.onSurface
			),
			elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
		) {
			Text(
				text = label,
				style = MaterialTheme.typography.bodyMedium,
				fontWeight = FontWeight.Medium,
				color = MaterialTheme.colorScheme.onSurface,
				modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
			)
		}

		FloatingActionButton(
			onClick = onClick,
			modifier = Modifier.size(48.dp),
			containerColor = backgroundColor
		) {
			Icon(
				icon,
				contentDescription = label,
				tint = MaterialTheme.colorScheme.onPrimary
			)
		}
	}
}
