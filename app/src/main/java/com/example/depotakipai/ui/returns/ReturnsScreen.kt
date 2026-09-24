package com.example.depotakipai.ui.returns

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val ReturnBackground = Color(0xFFF5F5F5)
private val ReturnAccent = Color(0xFF8B0000)
private val ReturnSecondary = Color(0xFF4682B4)

@Composable
fun ReturnsScreen(
    onBack: () -> Unit = {},
    onIncomingReturnClick: () -> Unit = {},
    onOutgoingReturnClick: () -> Unit = {}
) {
    val dateText = SimpleDateFormat(
        "dd MMMM yyyy",
        Locale("tr", "TR")
    ).format(Date())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ReturnBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(
                start = 20.dp,
                end = 20.dp,
                top = 14.dp,
                bottom = 12.dp
            )
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "İadeler",
                    color = ReturnAccent,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = "Gelen ve giden iadeleri buradan yönetin.",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Text(
                text = dateText,
                color = Color.Gray,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(
            modifier = Modifier.height(18.dp)
        )

        ReturnTypeCard(
            title = "Gelen İade",
            description = "Mağazadan, Trendyol'dan veya müşteriden gelen iadeleri yönetin.",
            iconText = "↩",
            iconBackground = Color(0xFFF3E5E5),
            iconColor = ReturnAccent,
            buttonText = "GELEN İADE",
            buttonColor = ReturnAccent,
            onClick = onIncomingReturnClick
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        ReturnTypeCard(
            title = "Giden İade",
            description = "Ürünleri iade edilecek hedefe göre yönetin.",
            iconText = "↪",
            iconBackground = Color(0xFFE8F0F7),
            iconColor = ReturnSecondary,
            buttonText = "GİDEN İADE",
            buttonColor = ReturnSecondary,
            onClick = onOutgoingReturnClick
        )

        Spacer(
            modifier = Modifier.height(18.dp)
        )

        OutlinedButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .height(38.dp)
        ) {
            Text(
                text = "GERİ",
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Composable
private fun ReturnTypeCard(
    title: String,
    description: String,
    iconText: String,
    iconBackground: Color,
    iconColor: Color,
    buttonText: String,
    buttonColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = iconBackground,
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = iconText,
                        color = iconColor,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(
                    modifier = Modifier.width(10.dp)
                )

                Text(
                    text = title,
                    color = Color.DarkGray,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Text(
                text = description,
                color = Color.Gray,
                style = MaterialTheme.typography.bodySmall
            )

            OutlinedButton(
                onClick = onClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
            ) {
                Text(
                    text = buttonText,
                    color = buttonColor,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}