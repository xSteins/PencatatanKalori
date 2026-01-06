package com.ralvin.pencatatankalori.view.components.HistoryScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.util.UUID

data class HistoryItemData(
	val date: String,
	val consumedText: String,
	val targetText: String,
	val personalInfoText: String,
	val calorieTargetText: String,
	val isGoalMet: Boolean,
	val id: UUID = UUID.randomUUID()
)

@Composable
fun HistoryListItem(item: HistoryItemData, onClick: () -> Unit) {
	Column {
		Text(
			text = item.date,
			style = MaterialTheme.typography.labelLarge,
			modifier = Modifier.padding(bottom = 4.dp, start = 4.dp)
		)
		Card(
			modifier = Modifier
				.fillMaxWidth()
				.clickable { onClick() },
			colors = CardDefaults.cardColors(
				containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
					alpha = 0.7f
				)
			)
		) {
			Row(
				modifier = Modifier
					.fillMaxWidth()
					.padding(16.dp),
				verticalAlignment = Alignment.CenterVertically
			) {
				Column(modifier = Modifier.weight(1f)) {
					Text(
						text = item.consumedText,
						style = MaterialTheme.typography.titleMedium,
						fontWeight = FontWeight.Bold
					)
					Spacer(modifier = Modifier.height(4.dp))
					Text(
						text = item.personalInfoText,
						style = MaterialTheme.typography.bodySmall,
						color = MaterialTheme.colorScheme.primary
					)
					Text(
						text = item.targetText,
						style = MaterialTheme.typography.bodySmall,
						color = MaterialTheme.colorScheme.onSurfaceVariant
					)
					Text(
						text = item.calorieTargetText,
						style = MaterialTheme.typography.bodySmall,
						color = MaterialTheme.colorScheme.onSurfaceVariant
					)
				}
				Spacer(modifier = Modifier.width(16.dp))
				val statusIcon =
					if (item.isGoalMet) Icons.Filled.CheckCircle else Icons.Filled.Cancel
				val statusTint = if (item.isGoalMet) {
					MaterialTheme.colorScheme.primary
				} else {
					MaterialTheme.colorScheme.error
				}
				Icon(
					imageVector = statusIcon,
					contentDescription = if (item.isGoalMet) "Target Achieved" else "Target Not Achieved",
					modifier = Modifier.size(40.dp),
					tint = statusTint
				)
			}
		}
	}
}
