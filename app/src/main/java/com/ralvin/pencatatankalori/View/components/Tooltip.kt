package com.ralvin.pencatatankalori.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun Tooltip(
	message: String,
	onDismiss: () -> Unit
) {
	Dialog(
		onDismissRequest = onDismiss,
		properties = DialogProperties(
			dismissOnBackPress = true,
			dismissOnClickOutside = true
		)
	) {
		Box(
			modifier = Modifier
				.background(
					color = MaterialTheme.colorScheme.surfaceVariant,
					shape = RoundedCornerShape(8.dp)
				)
				.padding(16.dp)
		) {
			Text(
				text = message,
				fontSize = 14.sp,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
				lineHeight = 20.sp
			)
		}
	}
}
