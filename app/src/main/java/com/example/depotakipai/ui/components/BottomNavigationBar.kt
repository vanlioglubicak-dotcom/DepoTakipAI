package com.example.depotakipai.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
private val TextDark = Color(0xFF222222)

@Composable
fun BottomNavigationBar(
    modifier: Modifier = Modifier,
    selectedItem: BottomNavigationItemType = BottomNavigationItemType.HOME,
    onHomeClick: () -> Unit,
    onProductsClick: () -> Unit,
    onQuickActionClick: () -> Unit,
    onReturnsClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .navigationBarsPadding()
            .padding(
                start = 8.dp,
                end = 8.dp,
                top = 8.dp,
                bottom = 6.dp
            )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {

            NavigationItem(
                modifier = Modifier.weight(1f),
                symbol = "⌂",
                title = "Ana Sayfa",
                selected = selectedItem == BottomNavigationItemType.HOME,
                onClick = onHomeClick
            )

            NavigationItem(
                modifier = Modifier.weight(1f),
                symbol = "▣",
                title = "Ürün",
                selected = selectedItem == BottomNavigationItemType.PRODUCTS,
                onClick = onProductsClick
            )

            QuickActionButton(
                modifier = Modifier.weight(1f),
                onClick = onQuickActionClick
            )

            NavigationItem(
                modifier = Modifier.weight(1f),
                symbol = "↩",
                title = "İade",
                selected = selectedItem == BottomNavigationItemType.RETURNS,
                onClick = onReturnsClick
            )

            NavigationItem(
                modifier = Modifier.weight(1f),
                symbol = "⚙",
                title = "Ayarlar",
                selected = selectedItem == BottomNavigationItemType.SETTINGS,
                onClick = onSettingsClick
            )
        }
    }
}

@Composable
private fun NavigationItem(
    modifier: Modifier,
    symbol: String,
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(vertical = 3.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = symbol,
            fontSize = 22.sp,
            fontWeight = if (selected) {
                FontWeight.Bold
            } else {
                FontWeight.Normal
            },
            color = if (selected) {
                DarkRed
            } else {
                Gray
            }
        )

        Spacer(
            modifier = Modifier.height(3.dp)
        )

        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = if (selected) {
                FontWeight.SemiBold
            } else {
                FontWeight.Normal
            },
            color = if (selected) {
                DarkRed
            } else {
                TextDark
            }
        )
    }
}

@Composable
private fun QuickActionButton(
    modifier: Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .padding(horizontal = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(58.dp)
                .clickable(onClick = onClick)
                .background(
                    color = DarkRed,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "+",
                fontSize = 32.sp,
                fontWeight = FontWeight.Light,
                color = Color.White
            )
        }
    }
}

enum class BottomNavigationItemType {
    HOME,
    PRODUCTS,
    RETURNS,
    SETTINGS
}