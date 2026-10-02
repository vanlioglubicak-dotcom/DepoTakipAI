package com.example.depotakipai.ui.catalog

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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

private val BlueSlate = Color(0xFF557392)
private val Background = Color(0xFFF6F7F9)
private val DarkText = Color(0xFF263238)
private val SecondaryText = Color(0xFF78909C)

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

    val filteredItems = remember(searchText, items) {
        if (searchText.isBlank()) {
            items
        } else {
            items.filter { item ->
                item.productNumber.contains(
                    searchText.trim(),
                    ignoreCase = true
                ) ||
                        item.company.contains(
                            searchText.trim(),
                            ignoreCase = true
                        ) ||
                        item.color.contains(
                            searchText.trim(),
                            ignoreCase = true
                        ) ||
                        item.size.contains(
                            searchText.trim(),
                            ignoreCase = true
                        )
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {

        // ÜST BAŞLIK
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .statusBarsPadding()
                .padding(
                    start = 6.dp,
                    end = 10.dp,
                    top = 6.dp,
                    bottom = 10.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // AYRI GERİ BUTONU
            IconButton(
                onClick = onBack,
                modifier = Modifier.size(42.dp)
            ) {
                Text(
                    text = "‹",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF455A64)
                )
            }

            Spacer(
                modifier = Modifier.width(6.dp)
            )

            // KATALOG — SADECE BAŞLIK/BUTON GÖRÜNÜMÜ
            // Geri işlevi YOK.
            Box(
                modifier = Modifier
                    .background(
                        color = BlueSlate,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(
                        horizontal = 16.dp,
                        vertical = 10.dp
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "KATALOG",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(
                modifier = Modifier.weight(1f)
            )

            // EKLE BUTONU
            Button(
                onClick = onAddCatalogItem,
                colors = ButtonDefaults.buttonColors(
                    containerColor = BlueSlate,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(
                    horizontal = 10.dp,
                    vertical = 8.dp
                )
            ) {
                Text(
                    text = "+",
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(
                    modifier = Modifier.width(5.dp)
                )

                Text(
                    text = "EKLE",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        // ALT BAŞLIK
        Text(
            text = "Ürün modelleri",
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(
                    start = 104.dp,
                    end = 16.dp,
                    bottom = 14.dp
                ),
            fontSize = 13.sp,
            color = Color.Gray
        )

        // ARAMA
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
            singleLine = false,
            leadingIcon = {
                Text(
                    text = "⌕",
                    fontSize = 25.sp,
                    color = Color(0xFF546E7A)
                )
            },
            placeholder = {
                Text(
                    text = "Model no, firma, renk veya beden ara..."
                )
            },
            shape = RoundedCornerShape(18.dp)
        )

        // LİSTE BAŞLIĞI
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 16.dp,
                    vertical = 4.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "KATALOG MODELLERİ",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF455A64)
            )

            Spacer(
                modifier = Modifier.weight(1f)
            )

            Text(
                text = "${filteredItems.size} model",
                fontSize = 13.sp,
                color = Color.Gray
            )
        }

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        // KATALOG BOŞ
        if (filteredItems.isEmpty()) {

            EmptyCatalogState(
                onAddCatalogItem = onAddCatalogItem
            )

        } else {

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 12.dp,
                    end = 12.dp,
                    bottom = 24.dp
                ),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
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
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp)
                    .background(
                        Color(0xFFE9ECEF)
                    ),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = "ÜRÜN GÖRSELİ",
                    color = SecondaryText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {

                Text(
                    text = "${item.company}-${item.productNumber}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkText
                )

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                if (item.color.isNotBlank()) {

                    Text(
                        text = "Renk: ${item.color}",
                        fontSize = 13.sp,
                        color = Color(0xFF546E7A)
                    )
                }

                if (item.size.isNotBlank()) {

                    Text(
                        text = "Beden: ${item.size}",
                        fontSize = 13.sp,
                        color = Color(0xFF546E7A)
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyCatalogState(
    onAddCatalogItem: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Katalog henüz boş",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF37474F)
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = "Katalog modellerini eklediğinde burada görünecek.",
            fontSize = 14.sp,
            color = Color.Gray
        )

        Spacer(
            modifier = Modifier.height(18.dp)
        )

        Button(
            onClick = onAddCatalogItem,
            colors = ButtonDefaults.buttonColors(
                containerColor = BlueSlate,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(10.dp)
        ) {

            Text(
                text = "+",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(
                modifier = Modifier.width(4.dp)
            )

            Text(
                text = "KATALOG ÜRÜNÜ EKLE",
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}