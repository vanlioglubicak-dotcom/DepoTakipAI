package com.example.depotakipai.ui.warehouse

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.depotakipai.domain.model.WarehouseRack
import com.example.depotakipai.domain.model.WarehouseRow

private val RowScreenDarkRed = Color(0xFF8B0000)
private val RowScreenGray = Color(0xFF808080)
private val RowScreenBackground = Color(0xFFF5F5F5)

@Composable
fun WarehouseRowScreen(
    row: WarehouseRow,
    racks: List<WarehouseRack>,
    isLoading: Boolean = false,
    onBack: () -> Unit = {},
    onRackClick: (WarehouseRack) -> Unit = {},
    onAddRackClick: () -> Unit = {}
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RowScreenBackground)
    ) {

        WarehouseRowHeader(
            row = row,
            rackCount = racks.count { it.isActive },
            onBack = onBack,
            onAddRackClick = onAddRackClick
        )

        when {

            isLoading -> {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {

                    CircularProgressIndicator(
                        color = RowScreenDarkRed
                    )
                }
            }

            racks.none { it.isActive } -> {

                EmptyRowRackState(
                    onAddRackClick = onAddRackClick
                )
            }

            else -> {

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(
                        top = 14.dp,
                        bottom = 24.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {

                    items(
                        items = racks
                            .filter { it.isActive }
                            .sortedBy { it.displayOrder },
                        key = { it.id }
                    ) { rack ->

                        WarehouseRackCard(
                            rack = rack,
                            onClick = {
                                onRackClick(rack)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WarehouseRowHeader(
    row: WarehouseRow,
    rackCount: Int,
    onBack: () -> Unit,
    onAddRackClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(
                horizontal = 16.dp,
                vertical = 14.dp
            )
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            TextButton(
                onClick = onBack
            ) {

                Text(
                    text = "‹ Geri",
                    fontSize = 14.sp,
                    color = RowScreenDarkRed
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = row.name,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "$rackCount aktif raf",
                    fontSize = 12.sp,
                    color = RowScreenGray
                )
            }

            TextButton(
                onClick = onAddRackClick
            ) {

                Text(
                    text = "+ RAF",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = RowScreenDarkRed
                )
            }
        }

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        Text(
            text = "Bu sıradaki rafları ve katları yönet",
            fontSize = 12.sp,
            color = RowScreenGray
        )
    }
}

@Composable
private fun EmptyRowRackState(
    onAddRackClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Bu sırada henüz raf bulunmuyor",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "Depo yapısını oluşturmak için ilk rafı ekle.",
                fontSize = 12.sp,
                color = RowScreenGray
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Button(
                onClick = onAddRackClick
            ) {

                Text(
                    text = "+ RAF EKLE"
                )
            }
        }
    }
}

/*
 * WarehouseRackView.kt içindeki fonksiyon private olduğu için
 * burada bağımsız bir kart kullanıyoruz.
 *
 * Böylece dosyalar birbirlerinin private bileşenlerine
 * bağımlı olmuyor.
 */
@Composable
private fun WarehouseRackCard(
    rack: WarehouseRack,
    onClick: () -> Unit
) {

    androidx.compose.material3.Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                Modifier
                    .background(Color.White)
            ),
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = androidx.compose.material3.CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .background(
                            color = Color(0xFF4682B4).copy(
                                alpha = 0.10f
                            )
                        )
                        .padding(
                            horizontal = 12.dp,
                            vertical = 10.dp
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = rack.rackCode,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4682B4)
                    )
                }

                Spacer(
                    modifier = Modifier.height(1.dp)
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 12.dp)
                ) {

                    Text(
                        text = rack.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Raf ${rack.rackCode}",
                        fontSize = 12.sp,
                        color = RowScreenGray
                    )
                }

                Text(
                    text = "${rack.shelfCount} kat",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = RowScreenDarkRed
                )
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text = "Katları ve konumları görüntüle",
                fontSize = 12.sp,
                color = RowScreenDarkRed,
                fontWeight = FontWeight.Medium
            )
        }
    }
}