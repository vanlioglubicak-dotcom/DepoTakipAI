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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.depotakipai.domain.model.DepotListCategory
import com.example.depotakipai.domain.model.DepotListRecord

private val DarkRed = Color(0xFF8B0000)
private val SteelBlue = Color(0xFF4682B4)
private val LightBackground = Color(0xFFF5F5F5)
private val TextDark = Color(0xFF222222)

private data class GroupedDepotListRecord(
    val productCode: String,
    val color: String,
    val size: String,
    val quantity: Int,
    val latestCreatedAt: Long
)

@Composable
fun DepotListDetailScreen(
    category: DepotListCategory,
    viewModel: DepotListViewModel,
    onBack: () -> Unit
) {
    val records by viewModel.records.collectAsState()

    val categoryRecords = records.filter {
        it.category == category
    }

    val groupedRecords =
        categoryRecords
            .groupBy {
                Triple(
                    it.productCode,
                    it.color,
                    it.size
                )
            }
            .map { (key, groupedItems) ->

                GroupedDepotListRecord(
                    productCode = key.first,
                    color = key.second,
                    size = key.third,
                    quantity = groupedItems.sumOf {
                        it.quantity
                    },
                    latestCreatedAt = groupedItems.maxOfOrNull {
                        it.createdAt
                    } ?: 0L
                )
            }
            .sortedByDescending {
                it.latestCreatedAt
            }

    val totalQuantity =
        groupedRecords.sumOf {
            it.quantity
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBackground)
            .navigationBarsPadding()
    ) {

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
                        .clickable {
                            onBack()
                        }
                        .padding(end = 10.dp)
                )

                Column {

                    Text(
                        text = categoryTitle(category),
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(2.dp)
                    )

                    Text(
                        text =
                            "${groupedRecords.size} ürün • $totalQuantity adet",
                        color = Color.White.copy(
                            alpha = 0.85f
                        ),
                        fontSize = 13.sp
                    )
                }
            }
        }

        if (groupedRecords.isEmpty()) {

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = "Bu listede henüz kayıt yok.",
                    color = Color.Gray,
                    fontSize = 15.sp
                )
            }

        } else {

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        horizontal = 16.dp,
                        vertical = 16.dp
                    ),
                verticalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {

                items(
                    items = groupedRecords,
                    key = {
                        "${it.productCode}_${it.color}_${it.size}"
                    }
                ) { record ->

                    DepotListRecordCard(
                        record = record
                    )
                }
            }
        }
    }
}

@Composable
private fun DepotListRecordCard(
    record: GroupedDepotListRecord
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = record.productCode,
                        color = TextDark,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    if (record.color.isNotBlank()) {

                        Spacer(
                            modifier = Modifier.height(3.dp)
                        )

                        Text(
                            text = "Renk: ${record.color}",
                            color = Color.Gray,
                            fontSize = 13.sp
                        )
                    }

                    if (record.size.isNotBlank()) {

                        Text(
                            text = "Beden: ${record.size}",
                            color = Color.Gray,
                            fontSize = 13.sp
                        )
                    }
                }

                Column(
                    horizontalAlignment =
                        Alignment.End
                ) {

                    Text(
                        text = "${record.quantity}",
                        color = SteelBlue,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "adet",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

private fun categoryTitle(
    category: DepotListCategory
): String {

    return when (category) {

        DepotListCategory.YENI_URUNLER ->
            "Yeni Ürünler"

        DepotListCategory.GIDEN ->
            "Giden"

        DepotListCategory.GELEN_IADE ->
            "Gelen İade"

        DepotListCategory.GIDEN_IADE ->
            "Giden İade"

        DepotListCategory.OKUNANLAR ->
            "Okunanlar"
    }
}