package com.example.depotakipai.ui.catalog

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
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
import androidx.compose.foundation.verticalScroll
private val BlueSlate = Color(0xFF557392)

data class CatalogPreviewItem(
    val productNumber: String,
    val company: String,
    val color: String,
    val size: String
)

@Composable
fun CatalogScreen(
    onBack: () -> Unit = {},
    onAddCatalogItem: () -> Unit = {},
    items: List<CatalogPreviewItem> = emptyList()
) {

    var searchText by remember {
        mutableStateOf("")
    }

    val filteredItems =
        remember(searchText, items) {

            if (searchText.isBlank()) {

                items

            } else {

                val query =
                    searchText.trim()

                items.filter { item ->

                    item.productNumber.contains(
                        query,
                        ignoreCase = true
                    ) ||
                            item.company.contains(
                                query,
                                ignoreCase = true
                            ) ||
                            item.color.contains(
                                query,
                                ignoreCase = true
                            ) ||
                            item.size.contains(
                                query,
                                ignoreCase = true
                            )
                }
            }
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Color(0xFFF6F7F9)
            )
    ) {

        /*
         * =====================================================
         * BAŞLIK
         * =====================================================
         */

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(
                    start = 8.dp,
                    end = 16.dp,
                    top = 12.dp,
                    bottom = 12.dp
                ),
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            IconButton(
                onClick = onBack
            ) {

                Text(
                    text = "‹",
                    fontSize = 36.sp,
                    color =
                        Color(0xFF37474F)
                )
            }

            Column(
                modifier =
                    Modifier.weight(1f)
            ) {

                Text(
                    text = "KATALOG",
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold,
                    color =
                        Color(0xFF263238)
                )

                Text(
                    text = "Ürün modelleri",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }
        }

        /*
         * ARAMA
         */

        OutlinedTextField(
            value = searchText,
            onValueChange = {
                searchText = it
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 16.dp,
                    vertical = 12.dp
                ),
            singleLine = true,
            placeholder = {
                Text(
                    "Model no, firma, renk veya beden ara..."
                )
            },
            shape =
                RoundedCornerShape(12.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 16.dp,
                    vertical = 4.dp
                ),
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Text(
                text = "KATALOG MODELLERİ",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color =
                    Color(0xFF455A64)
            )

            Spacer(
                modifier = Modifier.weight(1f)
            )

            Text(
                text =
                    "${filteredItems.size} model",
                fontSize = 13.sp,
                color = Color.Gray
            )
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        if (filteredItems.isEmpty()) {

            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                horizontalAlignment =
                    Alignment.CenterHorizontally,
                verticalArrangement =
                    Arrangement.Center
            ) {

                Text(
                    text = "Katalog henüz boş",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color =
                        Color(0xFF37474F)
                )

                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )

                Text(
                    text =
                        "Katalog modelini eklemek için aşağıdaki butonu kullan.",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Spacer(
                    modifier =
                        Modifier.height(22.dp)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp)
                        .background(
                            BlueSlate,
                            RoundedCornerShape(12.dp)
                        )
                        .clickable {
                            onAddCatalogItem()
                        },
                    contentAlignment =
                        Alignment.Center
                ) {

                    Text(
                        text =
                            "+ KATALOG ÜRÜNÜ EKLE",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

        } else {

            LazyVerticalGrid(
                columns =
                    GridCells.Fixed(2),
                modifier =
                    Modifier.fillMaxSize(),
                contentPadding =
                    PaddingValues(
                        start = 12.dp,
                        end = 12.dp,
                        bottom = 24.dp
                    ),
                horizontalArrangement =
                    Arrangement.spacedBy(10.dp),
                verticalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {

                items(
                    items = filteredItems,
                    key = {
                        "${it.company}-${it.productNumber}"
                    }
                ) { item ->

                    CatalogProductCard(
                        item = item
                    )
                }
            }
        }
    }
}

@Composable
private fun CatalogProductCard(
    item: CatalogPreviewItem
) {

    Card(
        modifier =
            Modifier.fillMaxWidth(),
        shape =
            RoundedCornerShape(14.dp),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color.White
            ),
        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
    ) {

        Column(
            modifier =
                Modifier.fillMaxWidth()
        ) {

            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(170.dp)
                        .background(
                            Color(0xFFE9ECEF)
                        ),
                contentAlignment =
                    Alignment.Center
            ) {

                Text(
                    text = "ÜRÜN GÖRSELİ",
                    color =
                        Color(0xFF78909C),
                    fontSize = 12.sp,
                    fontWeight =
                        FontWeight.Medium
                )
            }

            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
            ) {

                Text(
                    text =
                        "${item.company}-${item.productNumber}",
                    fontSize = 18.sp,
                    fontWeight =
                        FontWeight.Bold,
                    color =
                        Color(0xFF263238)
                )

                if (item.color.isNotBlank()) {

                    Spacer(
                        modifier =
                            Modifier.height(5.dp)
                    )

                    Text(
                        text =
                            "Renk: ${item.color}",
                        fontSize = 13.sp,
                        color =
                            Color(0xFF546E7A)
                    )
                }

                if (item.size.isNotBlank()) {

                    Spacer(
                        modifier =
                            Modifier.height(3.dp)
                    )

                    Text(
                        text =
                            "Beden: ${item.size}",
                        fontSize = 13.sp,
                        color =
                            Color(0xFF546E7A)
                    )
                }
            }
        }
    }
}