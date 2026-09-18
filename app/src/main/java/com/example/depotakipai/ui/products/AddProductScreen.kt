package com.example.depotakipai.ui.products

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.unit.sp

private val DarkRed = Color(0xFF8B0000)
private val SteelBlue = Color(0xFF4682B4)
private val Gray = Color(0xFF808080)
private val LightBackground = Color(0xFFF5F5F5)

@Composable
fun AddProductScreen(
    onBack: () -> Unit = {},
    onProductSaved: (ProductFormState) -> Unit = {}
) {
    var productCode by remember { mutableStateOf("") }
    var systemBarcode by remember { mutableStateOf("") }
    var color by remember { mutableStateOf("") }
    var size by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("0") }
    var rowNumber by remember { mutableStateOf("") }
    var shelfNumber by remember { mutableStateOf("") }
    var position by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBackground)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Column {
                Text(
                    text = "ÜRÜN EKLE",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkRed
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = "Yeni ürün kaydı oluştur",
                    fontSize = 13.sp,
                    color = Gray
                )
            }

            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Gray
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Geri")
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {

                Text(
                    text = "ÜRÜN BİLGİLERİ",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkRed
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = productCode,
                    onValueChange = { productCode = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text("Ürün Kodu") },
                    placeholder = { Text("Örn. SNZ-2926") },
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = systemBarcode,
                    onValueChange = { systemBarcode = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text("Sistem Barkodu") },
                    placeholder = { Text("Örn. 00002926") },
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "85 ile başlayan üretici barkodu sistem barkodu olarak kullanılmaz.",
                    fontSize = 11.sp,
                    color = Gray
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = color,
                    onValueChange = { color = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text("Renk") },
                    placeholder = { Text("Örn. SİYAH") },
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = size,
                    onValueChange = { size = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text("Beden") },
                    placeholder = { Text("Örn. S, M, L, XL") },
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text("Adet") },
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {

                Text(
                    text = "DEPO KONUMU",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = SteelBlue
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {

                    OutlinedTextField(
                        value = rowNumber,
                        onValueChange = { rowNumber = it },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        label = { Text("Sıra") },
                        placeholder = { Text("1") },
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = shelfNumber,
                        onValueChange = { shelfNumber = it },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        label = { Text("Raf") },
                        placeholder = { Text("1") },
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = position,
                    onValueChange = { position = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text("Konum") },
                    placeholder = { Text("ÖN veya ARKA") },
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                val formState = ProductFormState(
                    productCode = productCode,
                    systemBarcode = systemBarcode,
                    color = color,
                    size = size,
                    quantity = quantity,
                    rowNumber = rowNumber,
                    shelfNumber = shelfNumber,
                    position = position
                )

                onProductSaved(formState)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = DarkRed
            ),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text(
                text = "ÜRÜNÜ KAYDET",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}