package com.ralvin.pencatatankalori.view.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateNewActivityDate(
	type: LogType,
	onSubmit: (
		name: String,
		calories: String,
		notes: String,
		imagePath: String?,
		selectedDate: Date
	) -> Unit,
	onCancel: () -> Unit
) {
	var name by remember { mutableStateOf("") }
	var calories by remember { mutableStateOf("") }
	var notes by remember { mutableStateOf("") }
	var selectedImagePath by remember { mutableStateOf<String?>(null) }
	var selectedDate by remember { mutableStateOf(Date()) }
	var showDatePicker by remember { mutableStateOf(false) }

	var nameError by remember { mutableStateOf<String?>(null) }
	var caloriesError by remember { mutableStateOf<String?>(null) }

	val context = LocalContext.current
	val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

	fun validateName(input: String): String? {
		return when {
			input.isBlank() -> "Nama tidak boleh kosong"
			input.length < 2 -> "Nama minimal 2 karakter"
			else -> null
		}
	}

	fun validateCalories(input: String): String? {
		return when {
			input.isBlank() -> "Kalori tidak boleh kosong"
			input.toIntOrNull() == null -> "Masukkan angka yang valid"
			input.toIntOrNull()!! <= 0 -> "Kalori harus lebih dari 0"
			else -> null
		}
	}

	fun isFormValid(): Boolean {
		nameError = validateName(name)
		caloriesError = validateCalories(calories)
		return nameError == null && caloriesError == null
	}

	val imagePickerLauncher = rememberLauncherForActivityResult(
		contract = ActivityResultContracts.GetContent()
	) { uri: Uri? ->
		uri?.let {
			val inputStream = context.contentResolver.openInputStream(uri)
			val fileName = "${UUID.randomUUID()}.jpg"
			val storageDir = File(context.filesDir, "StoredImage")
			if (!storageDir.exists()) {
				storageDir.mkdirs()
			}
			val file = File(storageDir, fileName)
			val outputStream = FileOutputStream(file)
			inputStream?.copyTo(outputStream)
			inputStream?.close()
			outputStream.close()
			selectedImagePath = file.absolutePath
		}
	}

	Card(
		modifier = Modifier
			.fillMaxWidth()
			.widthIn(max = 560.dp),
		shape = RoundedCornerShape(16.dp),
		elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
		colors = CardDefaults.cardColors(
			containerColor = MaterialTheme.colorScheme.surface
		)
	) {
		Column(
			modifier = Modifier
				.padding(16.dp)
				.verticalScroll(rememberScrollState())
		) {
			Box(
				modifier = Modifier
					.fillMaxWidth()
					.height(120.dp)
					.clip(RoundedCornerShape(12.dp))
					.background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
					.clickable { imagePickerLauncher.launch("image/*") },
				contentAlignment = Alignment.Center
			) {
				if (selectedImagePath != null) {
					val imageModel = if (selectedImagePath!!.startsWith("android.resource://")) {
						val assetPath = selectedImagePath!!.substringAfter("assets/")
						"file:///android_asset/$assetPath"
					} else {
						File(selectedImagePath!!)
					}

					AsyncImage(
						model = ImageRequest.Builder(context)
							.data(imageModel)
							.crossfade(true)
							.build(),
						contentDescription = "Selected image",
						modifier = Modifier
							.fillMaxSize()
							.clip(RoundedCornerShape(12.dp)),
						contentScale = ContentScale.Crop
					)
					Box(
						modifier = Modifier
							.align(Alignment.BottomEnd)
							.padding(4.dp)
							.size(24.dp)
							.clip(RoundedCornerShape(12.dp))
							.background(MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)),
						contentAlignment = Alignment.Center
					) {
						Icon(
							Icons.Filled.CameraAlt,
							contentDescription = "Change photo",
							modifier = Modifier.size(16.dp),
							tint = MaterialTheme.colorScheme.onPrimary
						)
					}
				} else {
					Column(
						horizontalAlignment = Alignment.CenterHorizontally,
						verticalArrangement = Arrangement.Center
					) {
						Icon(
							Icons.Filled.CameraAlt,
							contentDescription = null,
							modifier = Modifier.size(32.dp),
							tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
						)
						Spacer(modifier = Modifier.height(4.dp))
						Text(
							text = "Tambah Foto",
							style = MaterialTheme.typography.bodySmall,
							color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
						)
						Icon(
							if (type == LogType.FOOD) Icons.Filled.Restaurant else Icons.Filled.FitnessCenter,
							contentDescription = null,
							modifier = Modifier.size(20.dp),
							tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
						)
					}
				}
			}
			Spacer(modifier = Modifier.height(16.dp))

			Text(
				text = when (type) {
					LogType.FOOD -> "Tambah Data Konsumsi"
					LogType.WORKOUT -> "Buat Data Aktifitas"
				},
				style = MaterialTheme.typography.headlineSmall,
				fontWeight = FontWeight.Bold,
				color = MaterialTheme.colorScheme.onSurface,
				modifier = Modifier.padding(bottom = 16.dp)
			)

			OutlinedTextField(
				value = name,
				onValueChange = {
					name = it
					nameError = null
				},
				label = { Text("Nama") },
				placeholder = { Text(if (type == LogType.FOOD) "Nama Konsumsi" else "Nama Aktivitas") },
				modifier = Modifier
					.fillMaxWidth()
					.padding(bottom = 16.dp),
				singleLine = true,
				isError = nameError != null,
				supportingText = nameError?.let {
					{
						Text(
							it,
							color = MaterialTheme.colorScheme.error
						)
					}
				}
			)
			OutlinedTextField(
				value = calories,
				onValueChange = {
					calories = it
					caloriesError = null
				},
				label = { Text("Jumlah Kalori") },
				placeholder = { Text("600") },
				keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
				modifier = Modifier
					.fillMaxWidth()
					.padding(bottom = 16.dp),
				singleLine = true,
				isError = caloriesError != null,
				supportingText = caloriesError?.let {
					{
						Text(
							it,
							color = MaterialTheme.colorScheme.error
						)
					}
				}
			)
			OutlinedTextField(
				value = notes,
				onValueChange = { notes = it },
				label = { Text("Catatan") },
				placeholder = { Text("Tambahkan Catatan") },
				modifier = Modifier
					.fillMaxWidth()
					.height(120.dp)
					.padding(bottom = 16.dp),
				singleLine = false,
				maxLines = 5
			)

			OutlinedTextField(
				value = dateFormat.format(selectedDate),
				onValueChange = { },
				label = { Text("Tanggal") },
				readOnly = true,
				modifier = Modifier
					.fillMaxWidth()
					.clickable { showDatePicker = true }
					.padding(bottom = 16.dp),
				trailingIcon = {
					IconButton(onClick = { showDatePicker = true }) {
						Icon(Icons.Default.DateRange, contentDescription = "Select Date")
					}
				}
			)

			Spacer(modifier = Modifier.height(16.dp))
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.spacedBy(12.dp)
			) {
				OutlinedButton(
					onClick = onCancel,
					modifier = Modifier.weight(1f),
					shape = RoundedCornerShape(50)
				) {
					Icon(Icons.Filled.Close, contentDescription = "Cancel")
					Spacer(modifier = Modifier.width(4.dp))
					Text("Cancel")
				}
				Button(
					onClick = {
						if (isFormValid()) {
							onSubmit(
								name,
								calories,
								notes,
								selectedImagePath,
								selectedDate
							)
						}
					},
					modifier = Modifier.weight(1f),
					shape = RoundedCornerShape(50)
				) {
					Icon(Icons.Filled.Check, contentDescription = "Submit")
					Spacer(modifier = Modifier.width(4.dp))
					Text("Save")
				}
			}
		}
	}

	if (showDatePicker) {
		val today = Calendar.getInstance()
		today.set(Calendar.HOUR_OF_DAY, 23)
		today.set(Calendar.MINUTE, 59)
		today.set(Calendar.SECOND, 59)
		today.set(Calendar.MILLISECOND, 999)

		val datePickerState = rememberDatePickerState(
			initialSelectedDateMillis = selectedDate.time,
			selectableDates = object : SelectableDates {
				override fun isSelectableDate(utcTimeMillis: Long): Boolean {
					return utcTimeMillis <= today.timeInMillis
				}
			}
		)
		DatePickerDialog(
			onDismissRequest = { showDatePicker = false },
			confirmButton = {
				TextButton(
					onClick = {
						datePickerState.selectedDateMillis?.let { millis ->
							selectedDate = Date(millis)
						}
						showDatePicker = false
					}
				) {
					Text("OK")
				}
			},
			dismissButton = {
				TextButton(
					onClick = { showDatePicker = false }
				) {
					Text("Cancel")
				}
			}
		) {
			DatePicker(state = datePickerState)
		}
	}
}