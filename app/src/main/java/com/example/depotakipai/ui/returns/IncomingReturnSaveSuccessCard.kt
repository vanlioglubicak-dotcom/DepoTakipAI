package com.example.depotakipai.ui.returns

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

private val SuccessBackground = Color(0xFFE8F5E9)
private val SuccessText = Color(0xFF2E7D32)

@Composable
fun IncomingReturnSaveSuccessCard(
    recordId: Long
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = SuccessBackground
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "İade başarıyla kaydedildi.",
                color = SuccessText,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = "Kayıt No: $recordId",
                color = SuccessText
            )
        }
    }
}