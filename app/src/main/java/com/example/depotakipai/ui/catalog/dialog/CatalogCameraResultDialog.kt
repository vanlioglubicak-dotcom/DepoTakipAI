package com.example.depotakipai.ui.catalog.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class CatalogCameraCandidate(
    val modelNumber: String,
    val displayModel: String,
    val color: String = "",
    val similarity: Float = 0f
)

@Composable
fun CatalogCameraResultDialog(
    visible: Boolean,
    candidates: List<CatalogCameraCandidate>,
    scannedText: String = "",
    onCandidateSelected: (CatalogCameraCandidate) -> Unit,
    onNewModel: () -> Unit,
    onClose: () -> Unit
) {
    if (!visible) {
        return
    }

    AlertDialog(
        onDismissRequest = onClose,
        title = {
            Text(
                text = "KAMERA SONUCU",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {

                if (scannedText.isNotBlank()) {
                    Text(
                        text = "Okunan: $scannedText",
                        fontSize = 13.sp
                    )

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )
                }

                if (candidates.isEmpty()) {

                    Text(
                        text = "Katalogda eşleşen model bulunamadı.",
                        fontSize = 14.sp
                    )

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )

                    Text(
                        text = "Ürünü yeni model olarak ekleyebilir veya tekrar fotoğraf çekebilirsiniz.",
                        fontSize = 13.sp
                    )

                } else {

                    Text(
                        text = "Benzer modeller",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    candidates.forEach { candidate ->

                        OutlinedButton(
                            onClick = {
                                onCandidateSelected(candidate)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = candidate.displayModel,
                                    fontWeight = FontWeight.Bold
                                )

                                if (candidate.color.isNotBlank()) {
                                    Text(
                                        text = "Renk: ${candidate.color}",
                                        fontSize = 12.sp
                                    )
                                }

                                if (candidate.similarity > 0f) {
                                    Text(
                                        text = "Benzerlik: ${
                                            (candidate.similarity * 100)
                                                .coerceIn(0f, 100f)
                                                .toInt()
                                        }%",
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }

                        Spacer(
                            modifier = Modifier.height(6.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onNewModel,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("YENİ MODEL")
            }
        },
        dismissButton = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                TextButton(
                    onClick = onClose
                ) {
                    Text("KAPAT")
                }
            }
        }
    )
}