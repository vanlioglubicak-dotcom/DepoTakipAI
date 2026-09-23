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
import com.example.depotakipai.domain.model.ShelfPosition
import com.example.depotakipai.domain.model.WarehouseLocation
import com.example.depotakipai.domain.model.WarehouseRack

private val LocationDarkRed = Color(0xFF8B0000)
private val LocationBlue = Color(0xFF4682B4)
private val LocationGray = Color(0xFF808080)
private val LocationBackground = Color(0xFFF5F5F5)
private val LocationEmpty = Color(0xFFE8F5E9)
private val LocationOccupied = Color(0xFFFFE0E0)

@Composable
fun WarehouseLocationPanel(
    rack: WarehouseRack,
    locations: List<WarehouseLocation>,
    onLocationClick: (WarehouseLocation) -> Unit = {},
    onAddLocationClick: () -> Unit = {}
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(LocationBackground)
            .padding(16.dp)
    ) {

        LocationHeader(
            rack = rack,
            locationCount = locations.count { it.isActive },
            onAddLocationClick = onAddLocationClick
        )

        Spacer(
            modifier = Modifier.height(14.dp)
        )

        if (locations.none { it.isActive }) {

            EmptyLocationState(
                onAddLocationClick = onAddLocationClick
            )

        } else {

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                items(
                    items = locations
                        .filter { it.isActive }
                        .sortedWith(
                            compareBy<WarehouseLocation> {
                                it.shelfNumber
                            }.thenBy {
                                it.locationNumber
                            }.thenBy {
                                it.position.name
                            }
                        ),
                    key = {
                        it.id
                    }
                ) { location ->

                    WarehouseLocationCard(
                        location = location,
                        onClick = {
                            onLocationClick(location)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun LocationHeader(
    rack: WarehouseRack,
    locationCount: Int,
    onAddLocationClick: () -> Unit
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
                        color = LocationDarkRed.copy(
                            alpha = 0.10f
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = rack.rackCode,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = LocationDarkRed
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
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "$locationCount aktif konum",
                    fontSize = 13.sp,
                    color = LocationGray
                )
            }

            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(
                        color = LocationDarkRed.copy(
                            alpha = 0.08f
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .clickable(
                        onClick = onAddLocationClick
                    ),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = "+",
                    fontSize = 23.sp,
                    fontWeight = FontWeight.Bold,
                    color = LocationDarkRed
                )
            }
        }
    }
}

@Composable
private fun WarehouseLocationCard(
    location: WarehouseLocation,
    onClick: () -> Unit
) {

    val isFront = location.position == ShelfPosition.FRONT

    val positionText = if (isFront) {
        "ÖN"
    } else {
        "ARKA"
    }

    val positionColor = if (isFront) {
        LocationBlue
    } else {
        LocationDarkRed
    }

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
                            color = positionColor.copy(
                                alpha = 0.10f
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = positionText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = positionColor
                    )
                }

                Spacer(
                    modifier = Modifier.width(12.dp)
                )

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = location.locationCode,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Kat ${location.shelfNumber} • Konum ${location.locationNumber}",
                        fontSize = 12.sp,
                        color = LocationGray
                    )
                }

                Text(
                    text = positionText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = positionColor
                )
            }

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            LocationVisualSlot(
                position = location.position
            )
        }
    }
}

@Composable
private fun LocationVisualSlot(
    position: ShelfPosition
) {

    val slotColor = when (position) {
        ShelfPosition.FRONT -> LocationEmpty
        ShelfPosition.BACK -> LocationOccupied
    }

    val slotText = when (position) {
        ShelfPosition.FRONT -> "ÖN KONUM"
        ShelfPosition.BACK -> "ARKA KONUM"
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(28.dp)
            .background(
                color = slotColor,
                shape = RoundedCornerShape(6.dp)
            ),
        contentAlignment = Alignment.Center
    ) {

        Text(
            text = slotText,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = Color.DarkGray
        )
    }
}

@Composable
private fun EmptyLocationState(
    onAddLocationClick: () -> Unit
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
                text = "Bu rafta henüz konum yok",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = "Kat ve ön/arka konumlarını ekleyerek rafı oluştur.",
                fontSize = 12.sp,
                color = LocationGray
            )

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            Box(
                modifier = Modifier
                    .background(
                        color = LocationDarkRed,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .clickable(
                        onClick = onAddLocationClick
                    )
                    .padding(
                        horizontal = 20.dp,
                        vertical = 10.dp
                    ),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = "+ KONUM EKLE",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}