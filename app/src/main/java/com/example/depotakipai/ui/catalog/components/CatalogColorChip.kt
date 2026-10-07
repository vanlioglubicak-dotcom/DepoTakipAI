package com.example.depotakipai.ui.catalog.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CatalogColorChip(
    colorName: String,
    color: Color,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clickable { onClick() },
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .size(if (selected) 30.dp else 26.dp)
                .background(
                    color = color,
                    shape = CircleShape
                )
                .border(
                    width = if (selected) 3.dp else 1.dp,
                    color = if (selected) {
                        Color(0xFF263238)
                    } else {
                        Color(0xFFB0BEC5)
                    },
                    shape = CircleShape
                )
        )

        Text(
            text = colorName,
            fontSize = 12.sp,
            fontWeight = if (selected) {
                FontWeight.Bold
            } else {
                FontWeight.Normal
            },
            color = Color(0xFF37474F)
        )
    }
}