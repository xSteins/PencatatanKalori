package com.ralvin.pencatatankalori.view.components.Onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InitialOnboardingBottomSheet(
	onDismiss: () -> Unit,
	onSkip: () -> Unit,
	onFillData: () -> Unit
) {
	val sheetState = rememberModalBottomSheetState(
		skipPartiallyExpanded = true
	)

	ModalBottomSheet(
		onDismissRequest = onDismiss,
		sheetState = sheetState
	) {
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 24.dp)
				.padding(bottom = 32.dp)
		) {
			Text(
				text = "Mulai Pencatatan Kalori",
				style = MaterialTheme.typography.headlineSmall,
				fontWeight = FontWeight.Bold,
				modifier = Modifier.padding(bottom = 16.dp)
			)

			Text(
				text = "Untuk pencatatan kalori, anda harus mengisi data pribadi seperti berat, tinggi badan, usia, dan jenis kelamin anda",
				style = MaterialTheme.typography.bodyMedium,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
				modifier = Modifier.padding(bottom = 8.dp)
			)

			Text(
				text = "Anda bisa melakukan tahapan ini secara manual melalui menu \"Profile -> Onboarding Screen\"",
				style = MaterialTheme.typography.bodySmall,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
				modifier = Modifier.padding(bottom = 24.dp)
			)

			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.spacedBy(12.dp)
			) {
				TextButton(
					onClick = {
						onSkip()
						onDismiss()
					},
					modifier = Modifier.weight(1f)
				) {
					Text("Skip")
				}

				Button(
					onClick = {
						onFillData()
						onDismiss()
					},
					modifier = Modifier.weight(1f)
				) {
					Text("Isi Data")
				}
			}
		}
	}
}
