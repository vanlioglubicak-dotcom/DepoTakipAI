package com.example.depotakipai.ui.catalog.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CatalogSizeRow(
    sizes: List<String>,
    selectedSize: String = "",
    onSizeSelected: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    if (sizes.isEmpty()) {
        return
    }

    val scrollState = rememberScrollState()

    Row(
        modifier = modifier
            .horizontalScroll(scrollState)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        sizes
            .filter { it.isNotBlank() }
            .distinct()
            .forEach { size ->

                val isSelected = size.equals(
                    selectedSize,
                    ignoreCase = true
                )

                Surface(
                    modifier = Modifier,
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSelected) {
                        Color(0xFF263238)
                    } else {
                        Color(0xFFF1F3F4)
                    },
                    onClick = {
                        onSizeSelected(size)
                    }
                ) {
                    Text(
                        text = size,
                        modifier = Modifier.padding(
                            horizontal = 14.dp,
                            vertical = 9.dp
                        ),
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) {
                            FontWeight.Bold
                        } else {
                            FontWeight.Normal
                        },
                        color = if (isSelected) {
                            Color.White
                        } else {
                            Color(0xFF37474F)
                        }
                    )
                }
            }
    }
}