package com.example.depotakipai.ui.catalog.dialog

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.depotakipai.data.catalog.CatalogColorCatalog
import com.example.depotakipai.data.catalog.CatalogColorOption

@Composable
fun CatalogColorEditDialog(
    visible: Boolean,
    initialColor: String = "",
    title: String = "Renk Düzenle",
    onSave: (String) -> Unit,
    onCancel: () -> Unit
) {
    if (!visible) {
        return
    }

    var colorName by remember(initialColor) {
        mutableStateOf(initialColor)
    }

    LaunchedEffect(initialColor) {
        colorName = initialColor
    }

    val selectedCatalogColor =
        CatalogColorCatalog.findByName(colorName)

    AlertDialog(
        onDismissRequest = onCancel,

        title = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF263238)
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = "RENK KATALOĞU",
                    fontSize = 12.sp,
                    color = Color(0xFF78909C),
                    fontWeight = FontWeight.Bold
                )
            }
        },

        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {

                Text(
                    text = "Model için bir renk seçin.",
                    fontSize = 13.sp,
                    color = Color(0xFF607D8B)
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                // =====================================================
                // MERKEZİ 25 RENK KATALOĞU
                // =====================================================

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(
                            rememberScrollState()
                        ),
                    horizontalArrangement =
                        Arrangement.spacedBy(10.dp),
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    CatalogColorCatalog.colors.forEach { colorOption ->

                        CatalogPaletteColorItem(
                            colorOption = colorOption,
                            selected =
                                CatalogColorCatalog.normalize(
                                    colorName
                                ) ==
                                        CatalogColorCatalog.normalize(
                                            colorOption.name
                                        ),
                            onClick = {
                                colorName = colorOption.name
                            }
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(14.dp)
                )

                // =====================================================
                // SEÇİLEN RENK
                // =====================================================

                if (selectedCatalogColor != null) {

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFF3F6F7)
                    ) {

                        Row(
                            modifier = Modifier.padding(
                                horizontal = 12.dp,
                                vertical = 10.dp
                            ),
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Surface(
                                modifier = Modifier.size(32.dp),
                                shape = CircleShape,
                                color =
                                    selectedCatalogColor.color,
                                border =
                                    BorderStroke(
                                        1.dp,
                                        if (
                                            selectedCatalogColor.name ==
                                            "Beyaz"
                                        ) {
                                            Color(0xFFB0BEC5)
                                        } else {
                                            Color.Transparent
                                        }
                                    ),
                                shadowElevation = 1.dp
                            ) {
                            }

                            Spacer(
                                modifier = Modifier.width(10.dp)
                            )

                            Column {
                                Text(
                                    text = "SEÇİLEN RENK",
                                    fontSize = 10.sp,
                                    color = Color(0xFF78909C),
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    text =
                                        selectedCatalogColor.name,
                                    fontSize = 14.sp,
                                    color = Color(0xFF263238),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )
                }

                // =====================================================
                // RENK ADI
                // =====================================================

                OutlinedTextField(
                    value = colorName,
                    onValueChange = {
                        colorName = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = {
                        Text("Renk")
                    },
                    placeholder = {
                        Text("Örn. Lacivert")
                    }
                )
            }
        },

        confirmButton = {

            Button(
                onClick = {

                    val value =
                        colorName.trim()

                    if (value.isNotBlank()) {

                        /*
                         * Merkezi katalogdaki bir renk seçilmişse
                         * daima standart isim kaydedilir.
                         *
                         * Örneğin:
                         * lacivert -> Lacivert
                         * LACİVERT -> Lacivert
                         * bordo -> Bordo
                         */

                        val canonicalName =
                            CatalogColorCatalog
                                .canonicalName(value)

                        onSave(
                            canonicalName ?: value
                        )
                    }
                },
                shape = RoundedCornerShape(10.dp),
                enabled = colorName.trim().isNotBlank()
            ) {
                Text(
                    text = "KAYDET",
                    fontWeight = FontWeight.Bold
                )
            }
        },

        dismissButton = {

            TextButton(
                onClick = onCancel
            ) {
                Text(
                    text = "İPTAL"
                )
            }
        }
    )
}


// =====================================================================
// MERKEZİ RENK KATALOĞU ÖĞESİ
// =====================================================================

@Composable
private fun CatalogPaletteColorItem(
    colorOption: CatalogColorOption,
    selected: Boolean,
    onClick: () -> Unit
) {

    Surface(
        onClick = onClick,
        modifier = Modifier.width(78.dp),
        shape = RoundedCornerShape(12.dp),
        color =
            if (selected) {
                Color(0xFFE8EEF0)
            } else {
                Color.White
            },
        border =
            if (selected) {
                BorderStroke(
                    2.dp,
                    Color(0xFF455A64)
                )
            } else {
                BorderStroke(
                    1.dp,
                    Color(0xFFD5DDE0)
                )
            },
        shadowElevation =
            if (selected) {
                2.dp
            } else {
                0.dp
            }
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 6.dp,
                    vertical = 9.dp
                ),
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            // ---------------------------------------------------------
            // RENK DAİRESİ
            // ---------------------------------------------------------

            Surface(
                modifier = Modifier.size(38.dp),
                shape = CircleShape,
                color = colorOption.color,
                border =
                    BorderStroke(
                        1.dp,
                        if (
                            colorOption.name == "Beyaz"
                        ) {
                            Color(0xFFB0BEC5)
                        } else {
                            Color.Transparent
                        }
                    ),
                shadowElevation = 1.dp
            ) {
            }

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            // ---------------------------------------------------------
            // RENK ADI
            // ---------------------------------------------------------

            Text(
                text = colorOption.name,
                fontSize = 10.sp,
                fontWeight =
                    if (selected) {
                        FontWeight.Bold
                    } else {
                        FontWeight.Medium
                    },
                color = Color(0xFF263238),
                maxLines = 1
            )
        }
    }
}