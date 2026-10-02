package com.example.depotakipai.ui.catalog

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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

private val BlueSlate = Color(0xFF557392)
private val Background = Color(0xFFF6F7F9)
private val DarkText = Color(0xFF263238)

@Composable
fun CatalogAddScreen(
    onBack: () -> Unit = {},
    onSave: (
        productNumber: String,
        company: String,
        color: String,
        size: String,
        imagePath: String?
    ) -> Unit = { _, _, _, _, _ -> }
) {

    var productNumber by remember {
        mutableStateOf("")
    }

    var selectedCompany by remember {
        mutableStateOf("")
    }

    var color by remember {
        mutableStateOf("")
    }

    var size by remember {
        mutableStateOf("")
    }

    var imagePath by remember {
        mutableStateOf<String?>(null)
    }

    var errorMessage by remember {
        mutableStateOf("")
    }

    val scrollState = rememberScrollState()

    val imagePicker =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri ->
            imagePath = uri?.toString()
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {

        // ÜST BAŞLIK
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(
                    start = 8.dp,
                    end = 16.dp,
                    top = 16.dp,
                    bottom = 14.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clickable {
                        onBack()
                    },
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = "‹",
                    fontSize = 42.sp,
                    color = Color(0xFF455A64)
                )
            }

            Spacer(
                modifier = Modifier.width(8.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "KATALOG ÜRÜNÜ",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkText
                )

                Spacer(
                    modifier = Modifier.height(2.dp)
                )

                Text(
                    text = "Yeni model ekle",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }
        }

        // KAYDIRILABİLİR FORM ALANI
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 16.dp,
                    bottom = 110.dp
                )
        ) {

            Text(
                text = "ÜRÜN BİLGİLERİ",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF455A64)
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            // ÜRÜN NUMARASI
            OutlinedTextField(
                value = productNumber,
                onValueChange = {
                    productNumber = it.filter { character ->
                        character.isDigit()
                    }
                    errorMessage = ""
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = {
                    Text("Ürün numarası")
                },
                placeholder = {
                    Text("Örn: 01")
                },
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            // FİRMA
            Text(
                text = "FİRMA",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF546E7A)
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                CompanyButton(
                    text = "SNZ",
                    selected = selectedCompany == "SNZ",
                    onClick = {
                        selectedCompany = "SNZ"
                        errorMessage = ""
                    },
                    modifier = Modifier.weight(1f)
                )

                CompanyButton(
                    text = "MTS",
                    selected = selectedCompany == "MTS",
                    onClick = {
                        selectedCompany = "MTS"
                        errorMessage = ""
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            // RENK
            OutlinedTextField(
                value = color,
                onValueChange = {
                    color = it
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = {
                    Text("Renk")
                },
                placeholder = {
                    Text("Örn: Siyah")
                },
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            // BEDEN
            OutlinedTextField(
                value = size,
                onValueChange = {
                    size = it
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = {
                    Text("Beden")
                },
                placeholder = {
                    Text("Örn: S, M, L, XL")
                },
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            // GÖRSEL
            Text(
                text = "ÜRÜN GÖRSELİ",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF546E7A)
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .border(
                        width = 1.dp,
                        color = Color(0xFFB0BEC5),
                        shape = RoundedCornerShape(14.dp)
                    )
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(14.dp)
                    )
                    .clickable {
                        imagePicker.launch("image/*")
                    },
                contentAlignment = Alignment.Center
            ) {

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = if (imagePath == null) {
                            "＋"
                        } else {
                            "✓"
                        },
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = BlueSlate
                    )

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    Text(
                        text = if (imagePath == null) {
                            "Galeriden görsel seç"
                        } else {
                            "Görsel seçildi"
                        },
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF546E7A)
                    )
                }
            }

            if (errorMessage.isNotBlank()) {

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                Text(
                    text = errorMessage,
                    fontSize = 13.sp,
                    color = Color(0xFFB00020),
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            // KAYDET
            Button(
                onClick = {

                    when {
                        productNumber.isBlank() -> {
                            errorMessage =
                                "Ürün numarasını girin."
                        }

                        selectedCompany.isBlank() -> {
                            errorMessage =
                                "SNZ veya MTS seçin."
                        }

                        else -> {
                            errorMessage = ""

                            onSave(
                                productNumber.trim(),
                                selectedCompany,
                                color.trim(),
                                size.trim(),
                                imagePath
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BlueSlate,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {

                Text(
                    text = "KAYDET",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(
                modifier = Modifier.height(24.dp)
            )
        }
    }
}

@Composable
private fun CompanyButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor =
                if (selected) {
                    BlueSlate
                } else {
                    Color.White
                },
            contentColor =
                if (selected) {
                    Color.White
                } else {
                    BlueSlate
                }
        ),
        shape = RoundedCornerShape(12.dp)
    ) {

        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}