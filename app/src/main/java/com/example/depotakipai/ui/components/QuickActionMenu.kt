package com.example.depotakipai.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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

@Composable
fun QuickActionMenu(
    modifier: Modifier = Modifier,
    visible: Boolean,
    onNewProductClick: () -> Unit,
    onIncomingReturnClick: () -> Unit,
    onOutgoingReturnClick: () -> Unit
) {
    if (!visible) {
        return
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // YENİ ÜRÜN
            QuickActionButton(
                text = "YENİ ÜRÜN",
                color = DarkRed,
                onClick = onNewProductClick
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            // GİDEN İADE - GELEN İADE
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                QuickActionButton(
                    text = "GİDEN İADE",
                    color = SteelBlue,
                    onClick = onOutgoingReturnClick
                )

                Spacer(
                    modifier = Modifier.width(74.dp)
                )

                QuickActionButton(
                    text = "GELEN İADE",
                    color = DarkRed,
                    onClick = onIncomingReturnClick
                )
            }

            // Menü grubunu alt menünün üzerine taşır.
            Spacer(
                modifier = Modifier.height(190.dp)
            )
        }
    }
}

@Composable
private fun QuickActionButton(
    text: String,
    color: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(88.dp)
            .height(44.dp)
            .clickable(onClick = onClick)
            .background(
                color = Color.White,
                shape = RoundedCornerShape(14.dp)
            ),
        contentAlignment = Alignment.Center
    ) {

        Text(
            text = text,
            color = color,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}