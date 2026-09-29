package com.example.depotakipai.ui.home

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val DarkRed = Color(0xFF8B0000)
private val SteelBlue = Color(0xFF4682B4)
private val Gray = Color(0xFF808080)
private val Background = Color(0xFFF5F5F5)
private val TextDark = Color(0xFF222222)
private val CatalogBlue = Color(0xFF5C6BC0)

@Composable
fun HomeScreen(
    onProductsClick: () -> Unit = {},
    onAddProductClick: () -> Unit = {},
    onReturnsClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onWarehouseClick: () -> Unit = {},
    onIncomingReturnClick: () -> Unit = {},
    onOutgoingReturnClick: () -> Unit = {},
    onListsClick: () -> Unit = {},
    onCatalogClick: () -> Unit = {}
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 24.dp
                ),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {

            item {

                Spacer(
                    modifier = Modifier.height(24.dp)
                )

                HomeHeader()

                Spacer(
                    modifier = Modifier.height(28.dp)
                )
            }

            item {

                DashboardGrid(
                    onProductsClick = onProductsClick,
                    onReturnsClick = onReturnsClick,
                    onWarehouseClick = onWarehouseClick,
                    onListsClick = onListsClick,
                    onCatalogClick = onCatalogClick
                )

                Spacer(
                    modifier = Modifier.height(30.dp)
                )
            }

            item {

                Text(
                    text = "Son İşlemler",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                EmptyOperationsCard()

                Spacer(
                    modifier = Modifier.height(30.dp)
                )
            }
        }
    }
}

// ================================================================
// BAŞLIK
// ================================================================

@Composable
private fun HomeHeader() {

    val dateText = SimpleDateFormat(
        "dd MMMM yyyy",
        Locale("tr", "TR")
    ).format(Date())

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column {

            Text(
                text = "DEPO TAKİP AI",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = DarkRed
            )

            Spacer(
                modifier = Modifier.height(5.dp)
            )

            Text(
                text = "Akıllı Depo Yönetimi",
                fontSize = 13.sp,
                color = Gray
            )
        }

        Text(
            text = dateText,
            fontSize = 13.sp,
            color = Gray
        )
    }
}

// ================================================================
// DASHBOARD
// ================================================================

@Composable
private fun DashboardGrid(
    onProductsClick: () -> Unit,
    onReturnsClick: () -> Unit,
    onWarehouseClick: () -> Unit,
    onListsClick: () -> Unit,
    onCatalogClick: () -> Unit
) {

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        // --------------------------------------------------------
        // 1. SATIR
        // --------------------------------------------------------

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            DashboardCard(
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        onProductsClick()
                    },
                symbol = "▣",
                title = "Ürünler",
                color = DarkRed
            )

            DashboardCard(
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        onReturnsClick()
                    },
                symbol = "↩",
                title = "İadeler",
                color = SteelBlue
            )
        }

        // --------------------------------------------------------
        // 2. SATIR
        // --------------------------------------------------------

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            DashboardCard(
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        onWarehouseClick()
                    },
                symbol = "⌂",
                title = "Depo",
                color = Gray
            )

            DashboardCard(
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        onListsClick()
                    },
                symbol = "▤",
                title = "Listeler",
                color = DarkRed
            )
        }

        // --------------------------------------------------------
        // KATALOG
        // --------------------------------------------------------

        DashboardWideCard(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onCatalogClick()
                },
            symbol = "▦",
            title = "Katalog",
            subtitle = "Ürün ve katalog yönetimi",
            color = CatalogBlue
        )
    }
}

// ================================================================
// NORMAL DASHBOARD KARTI
// ================================================================

@Composable
private fun DashboardCard(
    modifier: Modifier,
    symbol: String,
    title: String,
    color: Color
) {

    Box(
        modifier = modifier
            .height(125.dp)
            .background(
                color = Color.White,
                shape = RoundedCornerShape(18.dp)
            )
            .padding(16.dp)
    ) {

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Box(
                modifier = Modifier
                    .background(
                        color = color.copy(alpha = 0.10f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = symbol,
                    fontSize = 23.sp,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            }

            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextDark
            )
        }
    }
}

// ================================================================
// KATALOG KARTI
// ================================================================

@Composable
private fun DashboardWideCard(
    modifier: Modifier,
    symbol: String,
    title: String,
    subtitle: String,
    color: Color
) {

    Box(
        modifier = modifier
            .height(105.dp)
            .background(
                color = Color.White,
                shape = RoundedCornerShape(18.dp)
            )
            .padding(16.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .background(
                        color = color.copy(alpha = 0.10f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = symbol,
                    fontSize = 23.sp,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            }

            Spacer(
                modifier = Modifier.padding(
                    horizontal = 8.dp
                )
            )

            Column {

                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )

                Spacer(
                    modifier = Modifier.height(3.dp)
                )

                Text(
                    text = subtitle,
                    fontSize = 13.sp,
                    color = Gray
                )
            }
        }
    }
}

// ================================================================
// SON İŞLEMLER
// ================================================================

@Composable
private fun EmptyOperationsCard() {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .background(
                color = Color.White,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp),
        contentAlignment = Alignment.CenterStart
    ) {

        Text(
            text = "Henüz işlem bulunmuyor.",
            fontSize = 14.sp,
            color = Gray
        )
    }
}