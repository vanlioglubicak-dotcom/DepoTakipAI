package com.example.depotakipai.ui.products

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

private val DarkRed = Color(0xFF8B0000)

@Composable
fun ProductPhotoViewer(
    photoUri: String,
    onClose: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .statusBarsPadding()
    ) {

        AsyncImage(
            model = Uri.parse(photoUri),
            contentDescription = "Ürün fotoğrafı",
            modifier = Modifier
                .fillMaxSize()
                .clickable {
                    onClose()
                },
            contentScale = ContentScale.Fit
        )

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .background(
                    color = DarkRed,
                    shape = RoundedCornerShape(12.dp)
                )
                .clickable {
                    onClose()
                }
                .padding(
                    horizontal = 16.dp,
                    vertical = 10.dp
                )
        ) {
            Text(
                text = "✕ Kapat",
                color = Color.White,
                fontSize = 14.sp
            )
        }
    }
}