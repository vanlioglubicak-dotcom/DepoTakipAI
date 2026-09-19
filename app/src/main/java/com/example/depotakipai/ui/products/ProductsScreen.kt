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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

private val DarkRed = Color(0xFF8B0000)
private val SteelBlue = Color(0xFF4682B4)
private val Gray = Color(0xFF808080)
private val LightBackground = Color(0xFFF5F5F5)

@Composable
fun ProductsScreen(
    onBack: () -> Unit = {},
    onAddProduct: () -> Unit = {},
    onReturnsClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {}
) {

    val context = LocalContext.current

    val productsViewModel: ProductsViewModel = viewModel(
        factory = ProductViewModelProvider(
            context = context
        )
    )

    val products = productsViewModel.products
    val isLoading = productsViewModel.isLoading

    var searchText by remember {
        mutableStateOf("")
    }

    LaunchedEffect(Unit) {
        productsViewModel.loadProducts()
    }

    val filteredProducts = remember(
        products,
        searchText
    ) {

        val query = searchText.trim()

        if (query.isBlank()) {
            products
        } else {

            products.filter { product ->

                product.productCode.contains(
                    query,
                    ignoreCase = true
                ) ||
                        product.color.contains(
                            query,
                            ignoreCase = true
                        ) ||
                        product.size.contains(
                            query,
                            ignoreCase = true
                        )
            }
        }
    }

    val totalProductTypes = products.size

    val totalQuantity = products.sumOf {
        it.quantity
    }

    val totalShelves = products
        .mapNotNull { product ->

            if (
                product.rowNumber != null &&
                product.shelfNumber != null
            ) {
                "${product.rowNumber}-${product.shelfNumber}"
            } else {
                null
            }
        }
        .distinct()
        .size

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBackground)
            .statusBarsPadding()
    ) {

        // =========================================================
        // ÜST İÇERİK
        // =========================================================

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),

            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // -----------------------------------------------------
            // BAŞLIK
            // -----------------------------------------------------

            item {

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

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

                        Spacer(
                            modifier = Modifier.height(3.dp)
                        )

                        Text(
                            text = "Depodaki ürünlerinizi yönetin",
                            fontSize = 13.sp,
                            color = Gray
                        )
                    }

                    Button(
                        onClick = onBack,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Gray
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {

                        Text(
                            text = "Geri",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // -----------------------------------------------------
            // ARAMA
            // -----------------------------------------------------

            item {

                OutlinedTextField(
                    value = searchText,
                    onValueChange = {
                        searchText = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = {
                        Text("Ürün ara")
                    },
                    placeholder = {
                        Text(
                            "Ürün kodu, renk veya beden"
                        )
                    },
                    shape = RoundedCornerShape(14.dp)
                )
            }

            // -----------------------------------------------------
            // ÖZET
            // -----------------------------------------------------

            item {

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
                            value = totalProductTypes.toString(),
                            color = DarkRed
                        )

                        ProductSummaryItem(
                            title = "ADET",
                            value = totalQuantity.toString(),
                            color = SteelBlue
                        )

                        ProductSummaryItem(
                            title = "RAF",
                            value = totalShelves.toString(),
                            color = Gray
                        )
                    }
                }
            }

            // -----------------------------------------------------
            // LİSTE BAŞLIĞI
            // -----------------------------------------------------

            item {

                Text(
                    text = "ÜRÜN LİSTESİ",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray
                )
            }

            // -----------------------------------------------------
            // YÜKLENİYOR
            // -----------------------------------------------------

            if (isLoading) {

                item {

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        contentAlignment = Alignment.Center
                    ) {

                        Text(
                            text = "Ürünler yükleniyor...",
                            fontSize = 14.sp,
                            color = Gray
                        )
                    }
                }
            }

            // -----------------------------------------------------
            // ÜRÜN YOK
            // -----------------------------------------------------

            else if (filteredProducts.isEmpty()) {

                item {

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp),
                        contentAlignment = Alignment.Center
                    ) {

                        Column(
                            horizontalAlignment =
                                Alignment.CenterHorizontally
                        ) {

                            Text(
                                text = "📦",
                                fontSize = 42.sp
                            )

                            Spacer(
                                modifier = Modifier.height(10.dp)
                            )

                            Text(
                                text =
                                    if (products.isEmpty()) {
                                        "Henüz ürün bulunmuyor"
                                    } else {
                                        "Aramaya uygun ürün bulunamadı"
                                    },
                                fontSize = 16.sp,
                                fontWeight =
                                    FontWeight.SemiBold,
                                color = Color.DarkGray
                            )

                            Spacer(
                                modifier = Modifier.height(5.dp)
                            )

                            Text(
                                text =
                                    if (products.isEmpty()) {
                                        "Ürün eklemek için aşağıdaki + butonunu kullanın."
                                    } else {
                                        "Farklı bir ürün kodu, renk veya beden deneyin."
                                    },
                                fontSize = 12.sp,
                                color = Gray
                            )
                        }
                    }
                }
            }

            // -----------------------------------------------------
            // ÜRÜNLER
            // -----------------------------------------------------

            else {

                items(
                    items = filteredProducts,
                    key = {
                        it.productCode
                    }
                ) { product ->

                    ProductListItem(
                        product = product
                    )
                }

                item {

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )
                }
            }
        }

        // =========================================================
        // ALT MENÜ
        // =========================================================

        ProductBottomNavigation(
            onHomeClick = onBack,
            onAddClick = onAddProduct,
            onReturnsClick = onReturnsClick,
            onSettingsClick = onSettingsClick
        )
    }
}

// =================================================================
// ÖZET BİLGİSİ
// =================================================================

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

        Spacer(
            modifier = Modifier.height(3.dp)
        )

        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Gray
        )
    }
}

// =================================================================
// ÜRÜNLER SAYFASI ALT MENÜSÜ
// =================================================================

@Composable
private fun ProductBottomNavigation(
    onHomeClick: () -> Unit,
    onAddClick: () -> Unit,
    onReturnsClick: () -> Unit,
    onSettingsClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .navigationBarsPadding()
            .padding(
                horizontal = 8.dp,
                vertical = 8.dp
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {

        BottomNavigationItem(
            icon = "⌂",
            title = "Ana Sayfa",
            selected = false,
            onClick = onHomeClick
        )

        BottomNavigationItem(
            icon = "▣",
            title = "Ürün",
            selected = true,
            onClick = {}
        )

        Button(
            onClick = onAddClick,
            modifier = Modifier.height(64.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = DarkRed
            ),
            shape = RoundedCornerShape(50)
        ) {

            Text(
                text = "+",
                fontSize = 30.sp,
                color = Color.White,
                fontWeight = FontWeight.Normal
            )
        }

        BottomNavigationItem(
            icon = "↩",
            title = "İade",
            selected = false,
            onClick = onReturnsClick
        )

        BottomNavigationItem(
            icon = "⚙",
            title = "Ayarlar",
            selected = false,
            onClick = onSettingsClick
        )
    }
}

// =================================================================
// ALT MENÜ ELEMANI
// =================================================================

@Composable
private fun BottomNavigationItem(
    icon: String,
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {

    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor =
                if (selected) {
                    DarkRed
                } else {
                    Gray
                }
        ),
        shape = RoundedCornerShape(12.dp)
    ) {

        Column(
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Text(
                text = icon,
                fontSize = 22.sp,
                color =
                    if (selected) {
                        DarkRed
                    } else {
                        Gray
                    }
            )

            Text(
                text = title,
                fontSize = 10.sp,
                color =
                    if (selected) {
                        DarkRed
                    } else {
                        Gray
                    }
            )
        }
    }
}