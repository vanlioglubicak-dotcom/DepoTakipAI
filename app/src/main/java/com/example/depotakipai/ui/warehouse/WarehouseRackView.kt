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
import androidx.compose.foundation.lazy.itemsIndexed
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

    val activeRacks = racks
        .filter { it.isActive }
        .sortedBy { it.displayOrder }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(RackBackground)
            .padding(16.dp)
    ) {

        WarehouseRackHeader(
            row = row,
            rackCount = activeRacks.size,
            onAddRackClick = onAddRackClick
        )

        Spacer(
            modifier = Modifier.height(14.dp)
        )

        if (activeRacks.isEmpty()) {

            EmptyRackState(
                onAddRackClick = onAddRackClick
            )

        } else {

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                itemsIndexed(
                    items = activeRacks,
                    key = { _, rack -> rack.id }
                ) { index, rack ->

                    WarehouseRackCard(
                        rack = rack,
                        rackLetter = rackLetter(index),
                        onClick = {
                            onRackClick(rack)
                        }
                    )
                }
            }
        }
    }
}

private fun rackLetter(index: Int): String {

    var number = index + 1
    val result = StringBuilder()

    while (number > 0) {
        val remainder = (number - 1) % 26
        result.insert(0, ('A'.code + remainder).toChar())
        number = (number - 1) / 26
    }

    return result.toString()
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
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Text(
                    text = "$rackCount RAF",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
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
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
private fun WarehouseRackCard(
    rack: WarehouseRack,
    rackLetter: String,
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
            modifier = Modifier.padding(12.dp)
        ) {

            /*
             * RAF BAŞLIK
             */
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
                        text = rackLetter,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

                Spacer(
                    modifier = Modifier.width(12.dp)
                )

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = "RAF $rackLetter",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Text(
                        text = rack.name,
                        fontSize = 12.sp,
                        color = Color.Black
                    )
                }

                Text(
                    text = "${rack.shelfCount} KAT",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            /*
             * ÖN / ARKA BÖLÜMLERİ
             */
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                RackSideCard(
                    title = "ARKA",
                    modifier = Modifier.weight(1f),
                    onClick = onClick
                )

                RackSideCard(
                    title = "ÖN",
                    modifier = Modifier.weight(1f),
                    onClick = onClick
                )
            }

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            /*
             * RAF İŞLEM BUTONLARI
             */
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {

                RackActionButton(
                    text = "RAF DÜZENLE",
                    modifier = Modifier.weight(1f),
                    onClick = onClick
                )

                RackActionButton(
                    text = "RAF ÇIKAR",
                    modifier = Modifier.weight(1f),
                    onClick = onClick
                )
            }

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {

                RackActionButton(
                    text = "MODEL EKLE",
                    modifier = Modifier.weight(1f),
                    onClick = onClick
                )

                RackActionButton(
                    text = "MODEL ÇIKAR",
                    modifier = Modifier.weight(1f),
                    onClick = onClick
                )
            }

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            /*
             * KAT ÖNİZLEME
             */
            RackShelfPreview(
                shelfCount = rack.shelfCount
            )
        }
    }
}

@Composable
private fun RackSideCard(
    title: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {

    Box(
        modifier = modifier
            .height(58.dp)
            .background(
                color = RackBlue.copy(alpha = 0.08f),
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Text(
                text = "BÖLÜM",
                fontSize = 9.sp,
                color = Color.Black
            )
        }
    }
}

@Composable
private fun RackActionButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {

    Box(
        modifier = modifier
            .height(34.dp)
            .background(
                color = RackBorder,
                shape = RoundedCornerShape(7.dp)
            )
            .clickable(
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {

        Text(
            text = text,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
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
                    color = Color.Black
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
                        color = Color.Black
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
                    color = Color.Black
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
                text = "Bu sırada henüz RAF yok",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = "İlk rafı ekleyerek depo yapısını oluşturmaya başlayabilirsin.",
                fontSize = 12.sp,
                color = Color.Black
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