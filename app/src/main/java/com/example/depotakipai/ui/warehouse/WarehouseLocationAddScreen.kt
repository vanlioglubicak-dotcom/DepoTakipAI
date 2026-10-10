package com.example.depotakipai.ui.warehouse

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.depotakipai.domain.model.ShelfPosition
import com.example.depotakipai.domain.model.WarehouseRack

@Composable
fun WarehouseLocationAddScreen(
    rack: WarehouseRack,
    onBack: () -> Unit,
    onSave: (
        shelfNumber: Int,
        locationNumber: Int,
        position: ShelfPosition
    ) -> Unit
) {
    var shelfText by remember { mutableStateOf("") }
    var locationText by remember { mutableStateOf("") }
    var position by remember {
        mutableStateOf(ShelfPosition.FRONT)
    }
    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    val shelfNumber = shelfText.toIntOrNull()
    val locationNumber = locationText.toIntOrNull()

    val locationCode = if (
        shelfNumber != null &&
        locationNumber != null &&
        shelfNumber > 0 &&
        locationNumber > 0
    ) {
        "${rack.rackCode}-K${shelfNumber}-N${locationNumber}-${
            if (position == ShelfPosition.FRONT) "ON" else "ARKA"
        }"
    } else {
        "Bilgiler girildiğinde oluşturulacak"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(onClick = onBack) {
                Text("‹ Geri")
            }
        }

        Text(
            text = "YENİ DEPO KONUMU",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Raf: ${rack.rackCode}",
            style = MaterialTheme.typography.titleMedium
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = shelfText,
                    onValueChange = {
                        shelfText = it.filter(Char::isDigit)
                        errorMessage = null
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Kat numarası") },
                    singleLine = true
                )

                OutlinedTextField(
                    value = locationText,
                    onValueChange = {
                        locationText = it.filter(Char::isDigit)
                        errorMessage = null
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Konum numarası") },
                    singleLine = true
                )

                Text(
                    text = "Konum tarafı",
                    fontWeight = FontWeight.SemiBold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            position = ShelfPosition.FRONT
                        },
                        enabled = position != ShelfPosition.FRONT
                    ) {
                        Text("ÖN")
                    }

                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            position = ShelfPosition.BACK
                        },
                        enabled = position != ShelfPosition.BACK
                    ) {
                        Text("ARKA")
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Oluşturulacak konum kodu",
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = locationCode,
                    color = Color(0xFF4682B4)
                )

                errorMessage?.let { message ->
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        when {
                            shelfNumber == null || shelfNumber <= 0 -> {
                                errorMessage =
                                    "Geçerli bir kat numarası gir."
                            }

                            locationNumber == null ||
                                    locationNumber <= 0 -> {
                                errorMessage =
                                    "Geçerli bir konum numarası gir."
                            }

                            else -> {
                                errorMessage = null
                                onSave(
                                    shelfNumber,
                                    locationNumber,
                                    position
                                )
                            }
                        }
                    }
                ) {
                    Text("KONUMU KAYDET")
                }
            }
        }
    }
}