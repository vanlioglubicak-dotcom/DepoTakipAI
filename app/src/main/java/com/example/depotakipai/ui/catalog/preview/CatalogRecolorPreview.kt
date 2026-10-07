package com.example.depotakipai.ui.catalog.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun CatalogRecolorPreview(
    imagePath: String,
    modifier: Modifier = Modifier,
    imageOverride: String? = null,
    contentDescription: String = "Katalog ürün görseli"
) {
    val displayedImage =
        imageOverride
            ?.takeIf { it.isNotBlank() }
            ?: imagePath

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(360.dp)
            .background(
                color = Color(0xFFF3F5F6),
                shape = RoundedCornerShape(16.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        if (displayedImage.isNotBlank()) {
            AsyncImage(
                model = buildAssetUri(displayedImage),
                contentDescription = contentDescription,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        } else {
            Text(
                text = "Ürün görseli yok",
                color = Color(0xFF78909C)
            )
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
        path = if (path.startsWith("images/")) {
            "catalog/$path"
        } else {
            "catalog/images/$path"
        }
    }

    return "file:///android_asset/$path"
}