package com.example.depotakipai.ui.products

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

@Composable
fun ProductsScreen(
    onBack: () -> Unit = {},
    onAddProduct: () -> Unit = {}
) {
    var searchText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp)
    ) {

        Spacer(modifier = Modifier.height(12.dp))

        // Üst başlık
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Column {
                Text(
                    text = "ÜRÜNLER",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkRed
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = "Depodaki ürünlerinizi yönetin",
                    fontSize = 13.sp,
                    color = Gray
                )
            }

            Button(
                onClick = onAddProduct,
                colors = ButtonDefaults.buttonColors(
                    containerColor = DarkRed
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "+ Ürün",
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Arama
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            label = {
                Text("Ürün ara")
            },
            placeholder = {
                Text("Ürün kodu, renk veya beden")
            },
            shape = RoundedCornerShape(14.dp)
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Ürün özeti
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

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                ProductSummaryItem(
                    title = "ÜRÜN",
                    value = "0",
                    color = DarkRed
                )

                ProductSummaryItem(
                    title = "ADET",
                    value = "0",
                    color = SteelBlue
                )

                ProductSummaryItem(
                    title = "RAF",
                    value = "0",
                    color = Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "ÜRÜN LİSTESİ",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color.DarkGray
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Şimdilik boş ürün alanı
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "📦",
                    fontSize = 42.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Henüz ürün bulunmuyor",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = "İlk ürününüzü eklemek için + Ürün butonuna basın.",
                    fontSize = 12.sp,
                    color = Gray
                )
            }
        }
    }
}

@Composable
private fun ProductSummaryItem(
    title: String,
    value: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = value,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )

        Spacer(modifier = Modifier.height(3.dp))

        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Gray
        )
    }
}