package com.example.depotakipai.ui.warehouse

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.depotakipai.domain.model.WarehouseRack
import com.example.depotakipai.domain.model.WarehouseRow

private val RackDarkRed = Color(0xFF8B0000)
private val RackBlue = Color(0xFF4682B4)
private val RackGray = Color(0xFF808080)
private val RackBackground = Color(0xFFF5F5F5)
private val RackBorder = Color(0xFFE0E0E0)

@Composable
fun WarehouseRackView(
    row: WarehouseRow,
    racks: List<WarehouseRack>,
    onRackClick: (WarehouseRack) -> Unit = {},
    onAddRackClick: () -> Unit = {}
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(RackBackground)
            .padding(16.dp)
    ) {

        WarehouseRackHeader(
            row = row,
            rackCount = racks.count { it.isActive },
            onAddRackClick = onAddRackClick
        )

        Spacer(
            modifier = Modifier.height(14.dp)
        )

        if (racks.none { it.isActive }) {

            EmptyRackState(
                onAddRackClick = onAddRackClick
            )

        } else {

            LazyColumn(
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

@Composable
private fun WarehouseRackHeader(
    row: WarehouseRow,
    rackCount: Int,
    onAddRackClick: () -> Unit
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

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(
                        color = RackDarkRed.copy(alpha = 0.10f),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = row.rowNumber.toString(),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = RackDarkRed
                )
            }

            Spacer(
                modifier = Modifier.width(12.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = row.name,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "$rackCount aktif raf",
                    fontSize = 13.sp,
                    color = RackGray
                )
            }

            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(
                        color = RackDarkRed.copy(alpha = 0.08f),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .clickable(
                        onClick = onAddRackClick
                    ),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = "+",
                    fontSize = 23.sp,
                    fontWeight = FontWeight.Bold,
                    color = RackDarkRed
                )
            }
        }
    }
}

@Composable
private fun WarehouseRackCard(
    rack: WarehouseRack,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = onClick
            ),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(14.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(
                            color = RackBlue.copy(alpha = 0.10f),
                            shape = RoundedCornerShape(10.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = rack.rackCode,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = RackBlue
                    )
                }

                Spacer(
                    modifier = Modifier.width(12.dp)
                )

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = rack.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Raf ${rack.rackCode}",
                        fontSize = 12.sp,
                        color = RackGray
                    )
                }

                Text(
                    text = "${rack.shelfCount} kat",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = RackDarkRed
                )
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            RackShelfPreview(
                shelfCount = rack.shelfCount
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Text(
                text = "Katları ve konumları görüntüle",
                fontSize = 12.sp,
                color = RackDarkRed,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun RackShelfPreview(
    shelfCount: Int
) {

    val visibleShelfCount = shelfCount.coerceAtMost(8)

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {

        if (visibleShelfCount == 0) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(28.dp)
                    .background(
                        color = RackBorder,
                        shape = RoundedCornerShape(6.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = "Kat tanımlanmamış",
                    fontSize = 11.sp,
                    color = RackGray
                )
            }

        } else {

            repeat(visibleShelfCount) { index ->

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = "${index + 1}",
                        modifier = Modifier.width(24.dp),
                        fontSize = 10.sp,
                        color = RackGray
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(22.dp)
                            .background(
                                color = RackBlue.copy(alpha = 0.10f),
                                shape = RoundedCornerShape(4.dp)
                            )
                    )
                }
            }

            if (shelfCount > 8) {

                Text(
                    text = "+${shelfCount - 8} kat daha",
                    fontSize = 10.sp,
                    color = RackGray
                )
            }
        }
    }
}

@Composable
private fun EmptyRackState(
    onAddRackClick: () -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Bu sırada henüz raf yok",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = "İlk rafı ekleyerek depo yapısını oluşturmaya başlayabilirsin.",
                fontSize = 12.sp,
                color = RackGray
            )

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            Box(
                modifier = Modifier
                    .background(
                        color = RackDarkRed,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .clickable(
                        onClick = onAddRackClick
                    )
                    .padding(
                        horizontal = 20.dp,
                        vertical = 10.dp
                    ),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = "+ RAF EKLE",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}