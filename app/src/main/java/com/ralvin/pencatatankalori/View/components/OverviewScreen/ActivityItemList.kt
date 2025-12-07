package com.ralvin.pencatatankalori.view.components.OverviewScreen

import android.R
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.ralvin.pencatatankalori.model.database.entities.ActivityLog
import com.ralvin.pencatatankalori.model.database.entities.ActivityType
import com.ralvin.pencatatankalori.viewmodel.OverviewViewModel
import java.io.File


private val WorkoutActivityColor = Color(0xFF7986CB)
private val FoodActivityColor = Color(0xFF81C784)
private val ActivityContentColor = Color.White

@Composable
fun ActivityItemList(activity: ActivityLog, onClick: (ActivityLog) -> Unit) {
	val isFood = activity.type == ActivityType.CONSUMPTION
	val icon = if (isFood) Icons.Default.Restaurant else Icons.Default.FitnessCenter
	val calories = activity.calories
	val calorieText = "$calories Kalori"
	val description = activity.name

	val viewModel: OverviewViewModel = hiltViewModel()
	var imagePath by remember(activity.pictureId) { mutableStateOf<String?>(null) }

	LaunchedEffect(activity.pictureId) {
		imagePath = null // fix: previous image will be shown here
		activity.pictureId?.let { pictureId ->
			viewModel.getPicture(pictureId) { path ->
				imagePath = path
			}
		}
	}

	Card(
		modifier = Modifier
			.fillMaxHeight()
			.aspectRatio(3f / 4f)
			.clickable { onClick(activity) },
		colors = CardDefaults.cardColors(
			containerColor = if (isFood) FoodActivityColor else WorkoutActivityColor,
			contentColor = ActivityContentColor
		)
	) {
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(8.dp),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.spacedBy(8.dp)
		) {
			Box(
				contentAlignment = Alignment.Center,
				modifier = Modifier
					.weight(1f)
					.fillMaxWidth()
			) {
				if (imagePath != null) {
					val imageModel = if (imagePath!!.startsWith("android.resource://")) {
						val assetPath = imagePath!!.substringAfter("assets/")
						"file:///android_asset/$assetPath"
					} else {
						File(imagePath!!)
					}

					AsyncImage(
						model = ImageRequest.Builder(LocalContext.current)
							.data(imageModel)
							.crossfade(true)
							.build(),
						contentDescription = description,
						modifier = Modifier
							.fillMaxSize()
							.clip(RoundedCornerShape(8.dp)),
						contentScale = ContentScale.Crop,
						fallback = painterResource(R.drawable.ic_menu_gallery)
					)
				} else {
					Icon(
						imageVector = icon,
						contentDescription = description,
						modifier = Modifier.size(48.dp)
					)
				}
			}
			Text(
				text = "$description, $calorieText",
				style = MaterialTheme.typography.labelMedium,
				fontWeight = FontWeight.Bold
			)
		}
	}
}