package com.ralvin.pencatatankalori.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.platform.LocalConfiguration

data class LogItem(
    val id: Int,
    val type: LogType,
    val calories: Int,
    val name: String,
    val details: String
)

enum class LogType {
    FOOD, WORKOUT
}

fun parseDetails(type: LogType, details: String): Triple<String, String, String> {
    return if (type == LogType.FOOD) {
        // Example: "700 Calories | 60.5g Protein | 50.5g Carbs"
        val parts = details.split("|").map { it.trim() }
        val protein = parts.getOrNull(1)?.substringBefore("g")?.filter { it.isDigit() || it == '.' } ?: ""
        val carbs = parts.getOrNull(2)?.substringBefore("g")?.filter { it.isDigit() || it == '.' } ?: ""
        val portion = "" // Not available in dummy, can be improved if needed
        Triple(protein, carbs, portion)
    } else {
        // Example: "4.5 km"
        Triple("", "", details)
    }
}

@Composable
fun LogsDetailedModal(
    onDismissRequest: () -> Unit,
    date: String,
    logs: List<LogItem>
) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    var showEditModal by remember { mutableStateOf(false) }
    var editLog by remember { mutableStateOf<LogItem?>(null) }

    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 560.dp)
                .heightIn(max = screenHeight * 0.85f)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Text(
                    text = date,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                // TODO: Add summary/calculation if needed

                LazyColumn(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .wrapContentHeight()
                ) {
                    items(logs) { item ->
                        LogListItem(item = item, onEdit = {
                            editLog = item
                            showEditModal = true
                        })
                        Divider(color = Color.LightGray, thickness = 1.dp)
                    }
                }

                Button(
                    onClick = onDismissRequest,
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues()
                ) {
                    Text("Close", color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }

    if (showEditModal && editLog != null) {
        val (protein, carbs, portionOrDuration) = parseDetails(editLog!!.type, editLog!!.details)
        Dialog(onDismissRequest = { showEditModal = false }) {
            AddOrEditLogModal(
                type = editLog!!.type,
                initialName = editLog!!.name,
                initialCalories = editLog!!.calories.toString(),
                initialProtein = if (editLog!!.type == LogType.FOOD) protein else "",
                initialCarbs = if (editLog!!.type == LogType.FOOD) carbs else "",
                initialPortion = if (editLog!!.type == LogType.FOOD) portionOrDuration else "",
                initialDuration = if (editLog!!.type == LogType.WORKOUT) portionOrDuration else "",
                onSubmit = { name, calories, protein, carbs, portion, duration ->
                    // TODO: Save edited data
                    showEditModal = false
                },
                onCancel = { showEditModal = false }
            )
        }
    }
}

@Composable
fun LogListItem(item: LogItem, onEdit: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = if (item.type == LogType.FOOD) "${item.calories} Calories Added" else "${item.calories} Calories Burned",
                fontSize = 12.sp,
                color = Color.Gray
            )
            Text(
                text = item.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = item.details,
                fontSize = 14.sp
            )
        }
        IconButton(onClick = onEdit) {
            Icon(Icons.Filled.Edit, contentDescription = "Edit Log")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LogsDetailedModalPreview() {
    MaterialTheme {
        LogsDetailedModal(onDismissRequest = {/*do nothing*/}, date = "Thursday, 24th April", logs = listOf(
            LogItem(1, LogType.FOOD, 1600, "Ribeye Steak", "600 Calories | 60.5g Protein | 50.5g Carbs"),
            LogItem(2, LogType.WORKOUT, 600, "Jogging", "4.50km"),
            LogItem(3, LogType.FOOD, 1600, "Ribeye Steak", "600 Calories | 60.5g Protein | 50.5g Carbs")
        ))
    }
}
