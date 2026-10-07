package com.example.depotakipai.ui.catalog.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CatalogSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onCameraClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .weight(1f)
                .height(56.dp),
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            placeholder = {
                Text(
                    text = "Model, renk veya beden ara",
                    fontSize = 14.sp
                )
            }
        )

        Box(
            modifier = Modifier
                .width(56.dp)
                .height(56.dp)
                .background(
                    color = Color(0xFF263238),
                    shape = RoundedCornerShape(14.dp)
                )
                .clickable {
                    onCameraClick()
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "📷",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}