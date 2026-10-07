package com.example.depotakipai.ui.catalog.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

data class CatalogModelCardItem(
    val modelNumber: String,
    val displayModel: String,
    val imagePath: String,
    val colors: List<String>,
    val sizes: List<String>
)

@Composable
fun CatalogModelCard(
    model: CatalogModelCardItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },
        shape = androidx.compose.foundation.shape.RoundedCornerShape(14.dp),
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
                    .height(230.dp)
                    .background(
                        Color(0xFFE9ECEF)
                    ),
                contentAlignment = Alignment.Center
            ) {

                if (model.imagePath.isNotBlank()) {

                    AsyncImage(
                        model = buildAssetUri(
                            model.imagePath
                        ),
                        contentDescription = model.displayModel,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {

                Text(
                    text = model.displayModel,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF263238)
                )

                if (model.colors.isNotEmpty()) {

                    Spacer(
                        modifier = Modifier.height(5.dp)
                    )

                    Text(
                        text = "Renk: ${model.colors.joinToString(" • ")}",
                        fontSize = 12.sp,
                        color = Color(0xFF607D8B),
                        maxLines = 2
                    )
                }

                if (model.sizes.isNotEmpty()) {

                    Spacer(
                        modifier = Modifier.height(3.dp)
                    )

                    Text(
                        text = "Beden: ${model.sizes.joinToString(" • ")}",
                        fontSize = 12.sp,
                        color = Color(0xFF607D8B),
                        maxLines = 1
                    )
                }
            }
        }
    }
}

private fun buildAssetUri(
    imagePath: String
): String {

    var path = imagePath
        .trim()
        .replace("\\", "/")

    path = path.removePrefix(
        "file:///android_asset/"
    )

    path = path.removePrefix(
        "android_asset/"
    )

    if (!path.startsWith("catalog/")) {

        path =
            if (path.startsWith("images/")) {
                "catalog/$path"
            } else {
                "catalog/images/$path"
            }
    }

    return "file:///android_asset/$path"
}