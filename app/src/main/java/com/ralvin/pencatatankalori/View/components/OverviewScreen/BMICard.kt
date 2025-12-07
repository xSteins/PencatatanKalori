package com.ralvin.pencatatankalori.view.components.OverviewScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ralvin.pencatatankalori.view.components.EditUserDataDialog
import com.ralvin.pencatatankalori.view.components.EditUserDataType
import com.ralvin.pencatatankalori.view.components.Tooltip


@Composable
fun BmiCard(
	bmiValue: Float,
	bmiStatus: String,
	statusColor: Color,
	onWeightUpdate: (Float) -> Unit = {},
	currentWeight: Float? = null,
	enabled: Boolean = true,
	tooltipMessage: String? = null
) {
	var showWeightDialog by remember { mutableStateOf(false) }
	var showTooltip by remember { mutableStateOf(false) }

	Card(
		modifier = Modifier
			.fillMaxWidth()
			.clickable {
				if (enabled) {
					showWeightDialog = true
				} else if (tooltipMessage != null) {
					showTooltip = true
				}
			}
	) {
		Row(
			modifier = Modifier
				.padding(16.dp)
				.fillMaxWidth(),
			verticalAlignment = Alignment.CenterVertically
		) {
			Box(
				contentAlignment = Alignment.Center,
				modifier = Modifier.size(100.dp)
			) {
				CircularProgressIndicator(
					progress = { bmiValue / 40f },
					modifier = Modifier.fillMaxSize(),
					color = statusColor,
					strokeWidth = 8.dp,
					trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
				)
				Text(
					text = "%.1f".format(bmiValue),
					style = MaterialTheme.typography.headlineSmall,
					fontWeight = FontWeight.Bold
				)
			}

			Spacer(modifier = Modifier.width(16.dp))

			Column(modifier = Modifier.weight(1f)) {
				Text(
					text = "Indeks Massa Tubuh",
					style = MaterialTheme.typography.titleMedium,
					fontWeight = FontWeight.Bold
				)
				Spacer(modifier = Modifier.height(4.dp))
				Text(
					text = bmiStatus,
					style = MaterialTheme.typography.bodyMedium,
					color = statusColor,
					fontWeight = FontWeight.Medium
				)
				Spacer(modifier = Modifier.height(1.dp))
				Text(
					text = "Klik untuk update berat badan",
					style = MaterialTheme.typography.labelLarge,
					color = MaterialTheme.colorScheme.primary,
				)
			}
		}
	}

	if (showWeightDialog) {
		EditUserDataDialog(
			editType = EditUserDataType.WEIGHT,
			currentValue = currentWeight?.toString() ?: "",
			onDismiss = { showWeightDialog = false },
			onSave = { newValue ->
				newValue.toFloatOrNull()?.let { weight ->
					onWeightUpdate(weight)
				}
				showWeightDialog = false
			}
		)
	}

	if (showTooltip && tooltipMessage != null) {
		Tooltip(
			message = tooltipMessage,
			onDismiss = { showTooltip = false }
		)
	}
}