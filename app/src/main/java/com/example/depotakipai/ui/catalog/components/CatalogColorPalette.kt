package com.example.depotakipai.ui.catalog.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.depotakipai.data.catalog.CatalogColorCatalog

@Composable
fun CatalogColorPalette(
    selectedColor: String,
    onColorSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Row(
        modifier = modifier
            .horizontalScroll(scrollState)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        CatalogColorCatalog.colors.forEach { colorOption ->

            CatalogColorChip(
                colorName = colorOption.name,
                color = colorOption.color,
                selected = colorOption.name.equals(
                    selectedColor,
                    ignoreCase = true
                ),
                onClick = {
                    onColorSelected(colorOption.name)
                }
            )
        }
    }
}