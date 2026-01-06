package com.ralvin.pencatatankalori.view.components.ProfileScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalorieSettingsDialog(
	onDismiss: () -> Unit,
	onSave: (granularityValue: Int) -> Unit,
	initialGranularityValue: Int = 0
) {
	var granularityValue by remember { mutableIntStateOf(initialGranularityValue) }

	Dialog(
		onDismissRequest = onDismiss,
		properties = DialogProperties(usePlatformDefaultWidth = false)
	) {
		Surface(
			modifier = Modifier
				.fillMaxWidth()
				.padding(16.dp),
			shape = MaterialTheme.shapes.large,
			tonalElevation = 8.dp
		) {
			Column(
				modifier = Modifier
					.padding(16.dp)
					.verticalScroll(rememberScrollState()),
				horizontalAlignment = Alignment.Start
			) {
				Text(
					text = "Pengaturan Kalori Lanjutan",
					style = MaterialTheme.typography.headlineSmall,
					fontWeight = FontWeight.Bold,
				)

				Spacer(modifier = Modifier.height(12.dp))

				Text(
					text = "Pengaturan lanjutan, sesuaikan target kalori harian Anda dengan menggeser slider di bawah ini:",
					style = MaterialTheme.typography.bodyMedium,
					color = MaterialTheme.colorScheme.onSurfaceVariant
				)

				Spacer(modifier = Modifier.height(6.dp))

				Row(
					verticalAlignment = Alignment.CenterVertically,
					modifier = Modifier.fillMaxWidth()
				) {
					Text("-500", style = MaterialTheme.typography.bodySmall)
					Slider(
						value = granularityValue.toFloat(),
						onValueChange = { granularityValue = it.toInt() },
						valueRange = -500f..500f,
						steps = 99,
						modifier = Modifier
							.weight(1f)
							.padding(horizontal = 12.dp)
					)
					Text("500", style = MaterialTheme.typography.bodySmall)
				}

				Text(
					text = "Nilai saat ini: $granularityValue kalori",
					style = MaterialTheme.typography.bodyMedium,
					fontWeight = FontWeight.Medium,
					modifier = Modifier.padding(bottom = 12.dp)
				)

				Card(
					modifier = Modifier.fillMaxWidth(),
					colors = CardDefaults.cardColors(
						containerColor = MaterialTheme.colorScheme.secondaryContainer
					)
				) {
					Text(
						text = "Fitur ini membantu Anda menyesuaikan target kalori harian secara manual.\n\nTarget kalori dihitung berdasarkan data pribadi Anda (berat, tinggi, usia, jenis kelamin) dan tingkat aktivitas. Namun, rumus perhitungan mungkin memiliki perbedaan sekitar 20% dari kebutuhan asli Anda.\n\nGunakan slider di atas untuk menambah atau mengurangi target kalori sesuai kebutuhan:\n• Nilai positif (+): menambah kalori harian\n• Nilai negatif (-): mengurangi kalori harian\n\nDisarankan untuk menyesuaikan Tingkat Aktivitas terlebih dahulu di pengaturan profil sebelum mengubah nilai ini.",
						style = MaterialTheme.typography.bodySmall,
						modifier = Modifier.padding(10.dp)
					)
				}

				Spacer(modifier = Modifier.height(16.dp))

				Row(
					modifier = Modifier.fillMaxWidth(),
					horizontalArrangement = Arrangement.End
				) {
					TextButton(onClick = onDismiss) {
						Text("Cancel")
					}
					Spacer(modifier = Modifier.width(8.dp))
					Button(
						onClick = {
							onSave(granularityValue)
						}
					) {
						Text("Save")
					}
				}
			}
		}
	}
}
