package com.example.depotakipai.ui.products

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.depotakipai.domain.model.Product

private val DarkRed = Color(0xFF8B0000)
private val SteelBlue = Color(0xFF4682B4)
private val Gray = Color(0xFF606060)
private val LightGray = Color(0xFFF0F0F0)

@Composable
fun ProductListItem(
    product: Product,
    onClick: () -> Unit = {}
) {

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(86.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(
                    horizontal = 10.dp,
                    vertical = 8.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Ürün fotoğraf alanı
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .background(
                        color = LightGray,
                        shape = RoundedCornerShape(10.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = "📦",
                    fontSize = 27.sp
                )
            }

            Spacer(
                modifier = Modifier.size(10.dp)
            )

            // Ürün bilgileri
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {

                Text(
                    text = product.productCode,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkRed,
                    maxLines = 1
                )

                Spacer(
                    modifier = Modifier.height(3.dp)
                )

                Text(
                    text = "${product.color} • ${product.size}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.DarkGray,
                    maxLines = 1
                )

                Spacer(
                    modifier = Modifier.height(3.dp)
                )

                val locationText =
                    buildString {

                        product.rowNumber?.let {
                            append("Sıra $it")
                        }

                        product.shelfNumber?.let {

                            if (isNotEmpty()) {
                                append(" • ")
                            }

                            append("Raf $it")
                        }

                        product.position?.let {

                            if (isNotEmpty()) {
                                append(" • ")
                            }

                            append(
                                when (it.name) {
                                    "FRONT" -> "ÖN"
                                    "BACK" -> "ARKA"
                                    else -> it.name
                                }
                            )
                        }
                    }

                Text(
                    text = if (locationText.isBlank()) {
                        "Konum belirtilmemiş"
                    } else {
                        locationText
                    },
                    fontSize = 11.sp,
                    color = Gray,
                    maxLines = 1
                )
            }

            Spacer(
                modifier = Modifier.size(8.dp)
            )

            // Adet
            Column(
                horizontalAlignment = Alignment.End
            ) {

                Text(
                    text = "${product.quantity}",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = SteelBlue
                )

                Text(
                    text = "adet",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = Gray
                )
            }
        }
    }
}