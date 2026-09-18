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
import com.example.depotakipai.domain.model.Product

private val DarkRed = Color(0xFF8B0000)
private val SteelBlue = Color(0xFF4682B4)
private val Gray = Color(0xFF808080)
private val LightBackground = Color(0xFFF5F5F5)

@Composable
fun ProductsScreen(
    onBack: () -> Unit = {},
    onAddProduct: () -> Unit = {}
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
            .navigationBarsPadding()
            .padding(horizontal = 16.dp)
    ) {

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
                onClick = onAddProduct,
                colors = ButtonDefaults.buttonColors(
                    containerColor = DarkRed
                ),
                shape = RoundedCornerShape(12.dp)
            ) {

                Text(
                    text = "+ Ürün",
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(
            modifier = Modifier.height(18.dp)
        )

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

        Spacer(
            modifier = Modifier.height(18.dp)
        )

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

        Spacer(
            modifier = Modifier.height(18.dp)
        )

        Text(
            text = "ÜRÜN LİSTESİ",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color.DarkGray
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {

            when {

                isLoading -> {

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {

                        Text(
                            text = "Ürünler yükleniyor...",
                            fontSize = 14.sp,
                            color = Gray
                        )
                    }
                }

                filteredProducts.isEmpty() -> {

                    Box(
                        modifier = Modifier.fillMaxSize(),
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
                                        "İlk ürününüzü eklemek için + Ürün butonuna basın."
                                    } else {
                                        "Farklı bir ürün kodu, renk veya beden deneyin."
                                    },
                                fontSize = 12.sp,
                                color = Gray
                            )
                        }
                    }
                }

                else -> {

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement =
                            Arrangement.spacedBy(10.dp)
                    ) {

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
            }
        }
    }
}

@Composable
private fun ProductListItem(
    product: Product
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween,
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = product.productCode,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkRed
                    )

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    Text(
                        text =
                            "${product.color} • ${product.size}",
                        fontSize = 13.sp,
                        color = Color.DarkGray
                    )
                }

                Column(
                    horizontalAlignment =
                        Alignment.End
                ) {

                    Text(
                        text = "${product.quantity} adet",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = SteelBlue
                    )

                    Spacer(
                        modifier = Modifier.height(3.dp)
                    )

                    val locationText =
                        if (
                            product.rowNumber != null &&
                            product.shelfNumber != null
                        ) {
                            "Sıra ${product.rowNumber} • Raf ${product.shelfNumber}"
                        } else {
                            "Konum belirtilmemiş"
                        }

                    Text(
                        text = locationText,
                        fontSize = 11.sp,
                        color = Gray
                    )
                }
            }

            if (!product.systemBarcode.isNullOrBlank()) {

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text =
                        "Sistem Barkodu: ${product.systemBarcode}",
                    fontSize = 11.sp,
                    color = Gray
                )
            }
        }
    }
}

@Composable
private fun ProductSummaryItem(
    title: String,
    value: String,
    color: Color
) {

    Column(
        horizontalAlignment =
            Alignment.CenterHorizontally
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