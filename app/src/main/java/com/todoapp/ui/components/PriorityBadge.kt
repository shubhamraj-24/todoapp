package com.todoapp.ui.components

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
import com.todoapp.domain.model.Priority

@Composable
fun PriorityBadge(
    priority: Priority,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor, label) = when (priority) {
        Priority.LOW -> Triple(Color(0xFFE8F5E9), Color(0xFF2E7D32), "Low")
        Priority.MEDIUM -> Triple(Color(0xFFFFF8E1), Color(0xFFEF6C00), "Medium")
        Priority.HIGH -> Triple(Color(0xFFFFEBEE), Color(0xFFC62828), "High")
    }

    Box(
        modifier = modifier
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = textColor
        )
    }
}
