package com.example.depotakipai.ui.returns

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.depotakipai.domain.model.IncomingReturnAnalysisResult

private val PageBackground = Color(0xFFF5F5F5)
private val PageAccent = Color(0xFF8B0000)
private val PageBlue = Color(0xFF4682B4)

@Composable
fun IncomingReturnReviewPage(
    result: IncomingReturnAnalysisResult,
    onBack: () -> Unit = {},
    onConfirm: (
        IncomingReturnAnalysisResult
    ) -> Unit = {}
) {
    var currentResult by remember(result) {
        mutableStateOf(result)
    }

    var editMode by remember {
        mutableStateOf(false)
    }

    if (editMode) {
        IncomingReturnEditScreen(
            result = currentResult,
            onBack = {
                editMode = false
            },
            onSave = { editedResult ->
                currentResult = editedResult
                editMode = false
            }
        )

        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(
                start = 20.dp,
                end = 20.dp,
                top = 14.dp,
                bottom = 12.dp
            )
    ) {
        Text(
            text = "İade Kontrolü",
            color = PageAccent,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        Text(
            text = "Kayıt yapılmadan önce bilgileri kontrol edin.",
            color = Color.Gray
        )

        Spacer(
            modifier = Modifier.height(18.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ReviewValueRow(
                    label = "Ürün Kodu",
                    value = currentResult.productCode
                        ?: "Belirlenemedi"
                )

                ReviewValueRow(
                    label = "Sistem Barkodu",
                    value = currentResult.systemBarcode
                        ?: "Belirlenmedi"
                )

                ReviewValueRow(
                    label = "Renk",
                    value = currentResult.color
                        ?: "Belirlenmedi"
                )

                ReviewValueRow(
                    label = "Beden",
                    value = currentResult.size
                        ?: "Belirlenmedi"
                )

                ReviewValueRow(
                    label = "Adet",
                    value = currentResult.quantity
                        ?.toString()
                        ?: "Belirlenmedi"
                )

                ReviewValueRow(
                    label = "Kaynak",
                    value = currentResult.source
                        ?.name
                        ?: "Belirtilmedi"
                )
            }
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        if (currentResult.requiresManualReview) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFF7E6)
                )
            ) {
                Text(
                    text = "Bazı bilgiler otomatik olarak belirlenemedi. " +
                            "Kaydetmeden önce düzenleyin.",
                    modifier = Modifier.padding(14.dp),
                    color = Color(0xFF7A5700)
                )
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )
        }

        Spacer(
            modifier = Modifier.weight(1f)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.height(40.dp)
            ) {
                Text(
                    text = "GERİ"
                )
            }

            Spacer(
                modifier = Modifier.width(10.dp)
            )

            OutlinedButton(
                onClick = {
                    editMode = true
                },
                modifier = Modifier.height(40.dp)
            ) {
                Text(
                    text = "DÜZENLE",
                    color = PageBlue
                )
            }

            Spacer(
                modifier = Modifier.width(10.dp)
            )

            OutlinedButton(
                onClick = {
                    onConfirm(currentResult)
                },
                modifier = Modifier.height(40.dp)
            ) {
                Text(
                    text = "ONAYLA",
                    color = PageAccent
                )
            }
        }
    }
}

@Composable
private fun ReviewValueRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = Color.Gray,
            fontWeight = FontWeight.Medium
        )

        Text(
            text = value,
            color = Color.DarkGray,
            fontWeight = FontWeight.SemiBold
        )
    }
}