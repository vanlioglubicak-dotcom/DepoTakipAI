package com.example.depotakipai.ui.splash

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.depotakipai.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    var showContent by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        delay(150)
        showContent = true

        delay(2600)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF5C0000),
                        Color(0xFF8B0000),
                        Color(0xFF650000)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {

        AnimatedVisibility(
            visible = showContent,
            enter = fadeIn() + slideInVertically(
                initialOffsetY = { 80 }
            )
        ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Image(
                    painter = painterResource(
                        id = R.mipmap.ic_launcher_foreground
                    ),
                    contentDescription = "Depo Takip AI",
                    modifier = Modifier
                        .size(175.dp)
                        .shadow(
                            elevation = 18.dp,
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(32.dp)
                        )
                )

                Spacer(
                    modifier = Modifier.height(28.dp)
                )

                Text(
                    text = "DEPO TAKİP AI",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text = "Akıllı Depo ve Ürün Yönetimi",
                    color = Color.White.copy(alpha = 0.82f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    letterSpacing = 0.5.sp
                )

                Spacer(
                    modifier = Modifier.height(32.dp)
                )

                LoadingDots()
            }
        }
    }
}

@Composable
private fun LoadingDots() {

    RowDots()
}

@Composable
private fun RowDots() {

    androidx.compose.foundation.layout.Row(
        horizontalArrangement = Arrangement.spacedBy(7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Dot()

        Dot()

        Dot()
    }
}

@Composable
private fun Dot() {

    Box(
        modifier = Modifier
            .size(7.dp)
            .background(
                color = Color.White.copy(alpha = 0.85f),
                shape = androidx.compose.foundation.shape.CircleShape
            )
    )
}