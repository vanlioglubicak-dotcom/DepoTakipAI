package com.example.depotakipai.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
import com.example.depotakipai.domain.model.ShelfPosition
import com.example.depotakipai.domain.model.WarehouseLocation

private val LocationNavigationDarkRed = Color(0xFF8B0000)
private val LocationNavigationBlue = Color(0xFF4682B4)
private val LocationNavigationGray = Color(0xFF808080)
private val LocationNavigationBackground = Color(0xFFF5F5F5)

@Composable
fun WarehouseLocationNavigation(
    location: WarehouseLocation,
    onBack: () -> Unit = {},
    onProductClick: () -> Unit = {},
    onEditClick: () -> Unit = {}
) {

    val positionText =
        when (location.position) {
            ShelfPosition.FRONT -> "ÖN"
            ShelfPosition.BACK -> "ARKA"
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LocationNavigationBackground)
            .padding(16.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Button(
                onClick = onBack
            ) {
                Text(
                    text = "‹ Geri"
                )
            }

            Spacer(
                modifier = Modifier.weight(1f)
            )

            Text(
                text = "KONUM",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = LocationNavigationDarkRed
            )
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Text(
            text = location.locationCode,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        Text(
            text = "Depo konum bilgileri",
            fontSize = 13.sp,
            color = LocationNavigationGray
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                LocationInfoRow(
                    title = "Konum Kodu",
                    value = location.locationCode
                )

                LocationInfoRow(
                    title = "Kat",
                    value = location.shelfNumber.toString()
                )

                LocationInfoRow(
                    title = "Konum",
                    value = location.locationNumber.toString()
                )

                LocationInfoRow(
                    title = "Pozisyon",
                    value = positionText
                )

                LocationInfoRow(
                    title = "Durum",
                    value = if (location.isActive) {
                        "AKTİF"
                    } else {
                        "PASİF"
                    }
                )
            }
        }

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onProductClick
        ) {

            Text(
                text = "BU KONUMDAKİ ÜRÜNLER"
            )
        }

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onEditClick
        ) {

            Text(
                text = "KONUMU DÜZENLE"
            )
        }
    }
}

@Composable
private fun LocationInfoRow(
    title: String,
    value: String
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = title,
            modifier = Modifier.weight(1f),
            fontSize = 13.sp,
            color = LocationNavigationGray
        )

        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = LocationNavigationBlue
        )
    }
}