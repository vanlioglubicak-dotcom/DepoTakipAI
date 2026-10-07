package com.example.depotakipai.ui.catalog.dialog

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.depotakipai.ui.catalog.components.CatalogSizeRow

@Composable
fun CatalogModelDetailDialog(
    visible: Boolean,
    modelNumber: String,
    imagePath: String,
    colors: List<String>,
    sizes: List<String>,
    selectedColor: String,
    selectedSize: String,
    onColorSelected: (String) -> Unit,
    onSizeSelected: (String) -> Unit,
    onEdit: () -> Unit,
    onAddColor: () -> Unit,
    onClose: () -> Unit,
    imageOverride: String? = null
) {
    if (!visible) {
        return
    }

    Dialog(
        onDismissRequest = onClose
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.94f),
            shape = RoundedCornerShape(20.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "KATALOG MODELİ",
                            fontSize = 13.sp,
                            color = Color(0xFF78909C),
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(
                            modifier = Modifier.height(2.dp)
                        )

                        Text(
                            text = modelNumber,
                            fontSize = 24.sp,
                            color = Color(0xFF263238),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    TextButton(
                        onClick = onClose
                    ) {
                        Text(
                            text = "KAPAT",
                            color = Color(0xFF455A64)
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFF3F5F6)
                ) {
                    val displayedImage =
                        imageOverride?.takeIf { it.isNotBlank() }
                            ?: imagePath

                    if (displayedImage.isNotBlank()) {
                        AsyncImage(
                            model = buildAssetUri(displayedImage),
                            contentDescription = modelNumber,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Ürün görseli yok",
                                fontSize = 15.sp,
                                color = Color(0xFF78909C)
                            )
                        }
                    }
                }

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Text(
                    text = "RENKLER",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF455A64)
                )

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                if (colors.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(
                                rememberScrollState()
                            ),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        colors
                            .filter { it.isNotBlank() }
                            .distinct()
                            .forEach { color ->

                                val selected = color.equals(
                                    selectedColor,
                                    ignoreCase = true
                                )

                                OutlinedButton(
                                    onClick = {
                                        onColorSelected(color)
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = if (selected) {
                                            Color(0xFFE8EEF0)
                                        } else {
                                            Color.White
                                        }
                                    )
                                ) {
                                    Text(
                                        text = color,
                                        fontSize = 12.sp,
                                        fontWeight = if (selected) {
                                            FontWeight.Bold
                                        } else {
                                            FontWeight.Normal
                                        }
                                    )
                                }
                            }
                    }
                } else {
                    Text(
                        text = "Henüz renk eklenmemiş",
                        fontSize = 13.sp,
                        color = Color(0xFF78909C)
                    )
                }

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                OutlinedButton(
                    onClick = onAddColor,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "+ Renk Ekle",
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                Text(
                    text = "BEDENLER",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF455A64)
                )

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                CatalogSizeRow(
                    sizes = sizes,
                    selectedSize = selectedSize,
                    onSizeSelected = onSizeSelected,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(
                    modifier = Modifier.height(14.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onEdit,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "DÜZENLE",
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = onClose,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "KAPAT",
                            fontWeight = FontWeight.Bold
                        )
                    }
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
        path = if (path.startsWith("images/")) {
            "catalog/$path"
        } else {
            "catalog/images/$path"
        }
    }

    return "file:///android_asset/$path"
}