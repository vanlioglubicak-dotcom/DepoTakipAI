package com.example.depotakipai.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
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

private val DarkRed = Color(0xFF8B0000)
private val SteelBlue = Color(0xFF4682B4)
private val Gray = Color(0xFF808080)

@Composable
fun QuickActionMenu(
    visible: Boolean,
    onNewProductClick: () -> Unit,
    onIncomingReturnClick: () -> Unit,
    onOutgoingReturnClick: () -> Unit
) {
    if (!visible) {
        return
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {

        // YENİ ÜRÜN
        QuickActionItem(
            modifier = Modifier.offset(y = (-128).dp),
            text = "YENİ ÜRÜN",
            color = DarkRed,
            onClick = onNewProductClick
        )

        // GİDEN İADE
        QuickActionItem(
            modifier = Modifier.offset(
                x = (-112).dp,
                y = (-72).dp
            ),
            text = "GİDEN İADE",
            color = SteelBlue,
            onClick = onOutgoingReturnClick
        )

        // GELEN İADE
        QuickActionItem(
            modifier = Modifier.offset(
                x = 112.dp,
                y = (-72).dp
            ),
            text = "GELEN İADE",
            color = DarkRed,
            onClick = onIncomingReturnClick
        )
    }
}

@Composable
private fun QuickActionItem(
    modifier: Modifier,
    text: String,
    color: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .size(92.dp)
            .clickable(onClick = onClick)
            .background(
                color = Color.White,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}