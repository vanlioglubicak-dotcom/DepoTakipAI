package com.example.depotakipai.ui.lists

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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

private val DarkRed = Color(0xFF8B0000)
private val SteelBlue = Color(0xFF4682B4)
private val Gray = Color(0xFF808080)
private val LightBackground = Color(0xFFF5F5F5)
private val TextDark = Color(0xFF222222)

enum class ListCategory {
    ANA_LISTE,
    YENI_GELEN,
    GIDEN,
    GIDEN_IADE,
    GELEN_IADE
}

@Composable
fun ListsScreen(
    onBack: () -> Unit,
    onCategoryClick: (ListCategory) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBackground)
    ) {

        // =========================================================
        // BAŞLIK
        // =========================================================

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkRed)
                .padding(
                    start = 20.dp,
                    end = 20.dp,
                    top = 18.dp,
                    bottom = 18.dp
                )
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "‹",
                    color = Color.White,
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Light,
                    modifier = Modifier
                        .size(42.dp)
                        .clickable {
                            onBack()
                        }
                )

                Column(
                    modifier = Modifier.padding(start = 8.dp)
                ) {

                    Text(
                        text = "LİSTELER",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(2.dp)
                    )

                    Text(
                        text = "Depo hareketleri",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 13.sp
                    )
                }
            }
        }

        // =========================================================
        // LİSTELER
        // =========================================================

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = 16.dp,
                    vertical = 18.dp
                ),

            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // -----------------------------------------------------
            // YENİ ÜRÜNLER
            // -----------------------------------------------------

            ListCategoryCard(
                title = "Yeni Ürünler",
                description = "Yeni gelen ürünler",
                icon = "↓",
                color = SteelBlue,
                onClick = {
                    onCategoryClick(
                        ListCategory.YENI_GELEN
                    )
                }
            )

            // -----------------------------------------------------
            // GİDEN
            // -----------------------------------------------------

            ListCategoryCard(
                title = "Giden",
                description = "Depodan çıkan ürünler",
                icon = "↑",
                color = Gray,
                onClick = {
                    onCategoryClick(
                        ListCategory.GIDEN
                    )
                }
            )

            // -----------------------------------------------------
            // GELEN İADE
            // -----------------------------------------------------

            ListCategoryCard(
                title = "Gelen İade",
                description = "Depoya gelen iadeler",
                icon = "↙",
                color = SteelBlue,
                onClick = {
                    onCategoryClick(
                        ListCategory.GELEN_IADE
                    )
                }
            )

            // -----------------------------------------------------
            // GİDEN İADE
            // -----------------------------------------------------

            ListCategoryCard(
                title = "Giden İade",
                description = "Depodan gönderilen iadeler",
                icon = "↗",
                color = DarkRed,
                onClick = {
                    onCategoryClick(
                        ListCategory.GIDEN_IADE
                    )
                }
            )

            // -----------------------------------------------------
            // OKUNANLAR
            //
            // Mevcut ANA_LISTE enum değeri şimdilik korunuyor.
            // Daha sonra kamera onay kayıtlarını buraya bağlayacağız.
            // -----------------------------------------------------

            ListCategoryCard(
                title = "Okunanlar",
                description = "Kamera ile onaylanan ürün listeleri",
                icon = "▤",
                color = DarkRed,
                onClick = {
                    onCategoryClick(
                        ListCategory.ANA_LISTE
                    )
                }
            )
        }
    }
}

// =====================================================================
// LİSTE KARTI
// =====================================================================

@Composable
private fun ListCategoryCard(
    title: String,
    description: String,
    icon: String,
    color: Color,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(88.dp)
            .clickable {
                onClick()
            },

        shape = RoundedCornerShape(16.dp),

        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = 18.dp,
                    vertical = 12.dp
                ),

            verticalAlignment = Alignment.CenterVertically
        ) {

            // =====================================================
            // İKON
            // =====================================================

            Box(
                modifier = Modifier
                    .size(54.dp)
                    .background(
                        color = color.copy(
                            alpha = 0.12f
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ),

                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = icon,
                    color = color,
                    fontSize = 27.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // =====================================================
            // YAZI
            // =====================================================

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {

                Text(
                    text = title,
                    color = TextDark,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(3.dp)
                )

                Text(
                    text = description,
                    color = Color(0xFF777777),
                    fontSize = 12.sp
                )
            }

            // =====================================================
            // SAĞ OK
            // =====================================================

            Text(
                text = "›",
                color = color,
                fontSize = 30.sp,
                fontWeight = FontWeight.Light
            )
        }
    }
}