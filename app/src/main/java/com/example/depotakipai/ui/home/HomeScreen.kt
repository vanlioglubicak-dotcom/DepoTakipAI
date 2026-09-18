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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val DarkRed = Color(0xFF8B0000)
private val SteelBlue = Color(0xFF4682B4)
private val Gray = Color(0xFF808080)
private val Background = Color(0xFFF5F5F5)
private val TextDark = Color(0xFF222222)

@Composable
fun HomeScreen(
    onProductsClick: () -> Unit = {}
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 18.dp,
                    bottom = 96.dp
                )
        ) {

            HomeHeader()

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            DashboardGrid(
                onProductsClick = onProductsClick
            )

            Spacer(
                modifier = Modifier.height(28.dp)
            )

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
        }

        BottomNavigationBar(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding(),
            onProductsClick = onProductsClick
        )
    }
}

@Composable
private fun HomeHeader() {

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
            text = "15:xx",
            fontSize = 13.sp,
            color = Gray
        )
    }
}

@Composable
private fun DashboardGrid(
    onProductsClick: () -> Unit
) {

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

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
                modifier = Modifier.weight(1f),
                symbol = "↩",
                title = "İadeler",
                color = SteelBlue
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            DashboardCard(
                modifier = Modifier.weight(1f),
                symbol = "⌂",
                title = "Depo",
                color = Gray
            )

            DashboardCard(
                modifier = Modifier.weight(1f),
                symbol = "▤",
                title = "Kamera",
                color = DarkRed
            )
        }
    }
}

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
                    .size(44.dp)
                    .background(
                        color = color.copy(alpha = 0.10f),
                        shape = RoundedCornerShape(12.dp)
                    ),
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

@Composable
private fun BottomNavigationBar(
    modifier: Modifier = Modifier,
    onProductsClick: () -> Unit
) {

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(76.dp)
            .background(Color.White)
    ) {

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {

            BottomNavigationItem(
                symbol = "⌂",
                text = "Ana Sayfa",
                selected = true
            )

            BottomNavigationItem(
                modifier = Modifier.clickable {
                    onProductsClick()
                },
                symbol = "▣",
                text = "Ürün",
                selected = false
            )

            Box(
                modifier = Modifier
                    .size(54.dp)
                    .background(
                        color = DarkRed,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = "+",
                    fontSize = 30.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Light
                )
            }

            BottomNavigationItem(
                symbol = "↩",
                text = "İade",
                selected = false
            )

            BottomNavigationItem(
                symbol = "⚙",
                text = "Ayarlar",
                selected = false
            )
        }
    }
}

@Composable
private fun BottomNavigationItem(
    modifier: Modifier = Modifier,
    symbol: String,
    text: String,
    selected: Boolean
) {

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = symbol,
            fontSize = 21.sp,
            color = if (selected) DarkRed else Gray
        )

        Spacer(
            modifier = Modifier.height(3.dp)
        )

        Text(
            text = text,
            fontSize = 10.sp,
            color = if (selected) DarkRed else Gray
        )
    }
}