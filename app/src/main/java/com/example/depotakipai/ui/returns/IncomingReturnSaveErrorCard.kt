package com.example.depotakipai.ui.returns

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

private val ErrorBackground = Color(0xFFFFEBEE)
private val ErrorText = Color(0xFFC62828)

@Composable
fun IncomingReturnSaveErrorCard(
    message: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = ErrorBackground
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "İade kaydedilemedi.",
                color = ErrorText,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = message,
                modifier = Modifier.padding(top = 6.dp),
                color = ErrorText
            )
        }
    }
}