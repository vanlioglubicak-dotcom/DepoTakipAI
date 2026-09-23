package com.example.depotakipai.ui.warehouse

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import com.example.depotakipai.domain.model.WarehouseRow

private val WarehouseDarkRed = Color(0xFF8B0000)
private val WarehouseBlue = Color(0xFF4682B4)
private val WarehouseGray = Color(0xFF808080)
private val WarehouseBackground = Color(0xFFF5F5F5)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WarehouseScreen(
    rows: List<WarehouseRow> = WarehouseRow.createDefaultRows(),

    onBack: () -> Unit = {},

    onListsClick: () -> Unit = {},

    onProductsClick: () -> Unit = {},

    onLocationClick: () -> Unit = {},

    onStockClick: () -> Unit = {},

    onRowClick: (WarehouseRow) -> Unit = {},

    onAddRowClick: () -> Unit = {},

    onSearchClick: (String) -> Unit = {}
) {
    var searchText by remember {
        mutableStateOf("")
    }

    Scaffold(
        containerColor = WarehouseBackground,

        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = onBack
                    ) {
                        Text(
                            text = "‹",
                            fontSize = 34.sp,
                            fontWeight = FontWeight.Light,
                            color = WarehouseDarkRed
                        )
                    }
                },

                title = {
                    Column {
                        Text(
                            text = "Depo",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Dijital Depo Haritası",
                            fontSize = 12.sp,
                            color = WarehouseGray
                        )
                    }
                },

                actions = {

                    IconButton(
                        onClick = {
                            onSearchClick(searchText)
                        }
                    ) {
                        Text(
                            text = "ARA",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = WarehouseDarkRed
                        )
                    }

                    IconButton(
                        onClick = onAddRowClick
                    ) {
                        Text(
                            text = "+",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = WarehouseDarkRed
                        )
                    }
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            OutlinedTextField(
                value = searchText,

                onValueChange = {
                    searchText = it
                },

                modifier = Modifier.fillMaxWidth(),

                singleLine = true,

                placeholder = {
                    Text(
                        text = "Ürün kodu veya konum ara"
                    )
                },

                shape = RoundedCornerShape(12.dp)
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            WarehouseSummaryCard(
                rowCount = rows.count { it.isActive },
                onAddRowClick = onAddRowClick
            )

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            Text(
                text = "Depo Sıraları",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),

                modifier = Modifier.fillMaxSize(),

                contentPadding = PaddingValues(
                    top = 4.dp,
                    bottom = 24.dp
                ),

                horizontalArrangement = Arrangement.spacedBy(10.dp),

                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                items(
                    items = rows.filter { it.isActive },

                    key = {
                        it.id
                    }
                ) { row ->

                    WarehouseRowCard(
                        row = row,

                        onClick = {
                            onRowClick(row)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun WarehouseSummaryCard(
    rowCount: Int,
    onAddRowClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),

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
                .fillMaxWidth()
                .padding(16.dp),

            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = WarehouseDarkRed.copy(
                            alpha = 0.10f
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ),

                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = "D",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = WarehouseDarkRed
                )
            }

            Spacer(
                modifier = Modifier.width(12.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "Ana Depo",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "$rowCount aktif sıra",
                    fontSize = 13.sp,
                    color = WarehouseGray
                )
            }

            IconButton(
                onClick = onAddRowClick
            ) {

                Text(
                    text = "+",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = WarehouseDarkRed
                )
            }
        }
    }
}

@Composable
private fun WarehouseRowCard(
    row: WarehouseRow,
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
                        .size(40.dp)
                        .background(
                            color = WarehouseBlue.copy(
                                alpha = 0.12f
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ),

                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = "R",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = WarehouseBlue
                    )
                }

                Spacer(
                    modifier = Modifier.width(10.dp)
                )

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = row.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Sıra ${row.rowNumber}",
                        fontSize = 12.sp,
                        color = WarehouseGray
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(
                        color = WarehouseBlue.copy(
                            alpha = 0.18f
                        ),
                        shape = RoundedCornerShape(4.dp)
                    )
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "Rafları görüntüle",
                fontSize = 12.sp,
                color = WarehouseDarkRed,
                fontWeight = FontWeight.Medium
            )
        }
    }
}