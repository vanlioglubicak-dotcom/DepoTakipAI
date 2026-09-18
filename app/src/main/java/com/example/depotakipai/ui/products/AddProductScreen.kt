package com.example.depotakipai.ui.products

import android.net.Uri

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.lifecycle.viewmodel.compose.viewModel

private val DarkRed = Color(0xFF8B0000)
private val SteelBlue = Color(0xFF4682B4)
private val Gray = Color(0xFF606060)
private val LightBackground = Color(0xFFF5F5F5)

private val FieldText = Color(0xFF202020)
private val FieldBorder = Color(0xFF777777)
private val FieldFocusedBorder = Color(0xFF8B0000)
private val FieldPlaceholder = Color(0xFF707070)

@Composable
fun AddProductScreen(
    onBack: () -> Unit = {},
    onProductSaved: () -> Unit = {},
    onCameraClick: () -> Unit = {}
) {
    val context = LocalContext.current

    val productsViewModel: ProductsViewModel = viewModel(
        factory = ProductViewModelProvider(
            context = context
        )
    )

    var productCode by remember { mutableStateOf("") }
    var systemBarcode by remember { mutableStateOf("") }
    var color by remember { mutableStateOf("") }
    var size by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var rowNumber by remember { mutableStateOf("") }
    var shelfNumber by remember { mutableStateOf("") }
    var position by remember { mutableStateOf("") }

    var photoUri by remember {
        mutableStateOf<Uri?>(null)
    }

    var message by remember {
        mutableStateOf<String?>(null)
    }

    var isSaving by remember {
        mutableStateOf(false)
    }

    val galleryLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri ->
            photoUri = uri

            if (uri != null) {
                message = "Ürün fotoğrafı seçildi."
            }
        }

    val fieldColors =
        OutlinedTextFieldDefaults.colors(
            focusedTextColor = FieldText,
            unfocusedTextColor = FieldText,
            focusedLabelColor = DarkRed,
            unfocusedLabelColor = FieldText,
            focusedPlaceholderColor = FieldPlaceholder,
            unfocusedPlaceholderColor = FieldPlaceholder,
            focusedBorderColor = FieldFocusedBorder,
            unfocusedBorderColor = FieldBorder,
            cursorColor = DarkRed
        )

    LaunchedEffect(productsViewModel.saveResult) {
        when (productsViewModel.saveResult) {

            true -> {
                message = "Ürün başarıyla kaydedildi."
                isSaving = false
                productsViewModel.clearSaveResult()
                onProductSaved()
            }

            false -> {
                message =
                    "Ürün kaydedilemedi. Bu ürün kodu zaten kayıtlı olabilir."
                isSaving = false
                productsViewModel.clearSaveResult()
            }

            null -> Unit
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
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
                Text(
                    text = "Geri",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
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
                    onValueChange = {
                        productCode = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = {
                        Text("Ürün Kodu")
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = fieldColors
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    OutlinedTextField(
                        value = systemBarcode,
                        onValueChange = {
                            systemBarcode =
                                it.filter { char ->
                                    char.isDigit()
                                }
                        },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        label = {
                            Text("Sistem Barkodu")
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = fieldColors
                    )

                    Button(
                        onClick = {

                            val numericPart =
                                productCode.filter {
                                    it.isDigit()
                                }

                            if (numericPart.isNotEmpty()) {

                                systemBarcode =
                                    numericPart
                                        .takeLast(8)
                                        .padStart(8, '0')

                                message = null

                            } else {

                                message =
                                    "Önce ürün kodunu girin."
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SteelBlue
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {

                        Text(
                            text = "BARKOD",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onCameraClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DarkRed
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {

                    Text(
                        text = "📷  KAMERADAN OKU",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        galleryLauncher.launch("image/*")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SteelBlue
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {

                    Text(
                        text = "🖼  GALERİDEN FOTOĞRAF EKLE",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (photoUri != null) {

                    Text(
                        text = "✓ Ürün fotoğrafı seçildi",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = SteelBlue
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

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
                    shape = RoundedCornerShape(12.dp),
                    colors = fieldColors
                )

                Spacer(modifier = Modifier.height(12.dp))

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
                    shape = RoundedCornerShape(12.dp),
                    colors = fieldColors
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = quantity,
                    onValueChange = {
                        quantity =
                            it.filter { char ->
                                char.isDigit()
                            }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = {
                        Text("Adet")
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = fieldColors
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
                        onValueChange = {
                            rowNumber =
                                it.filter { char ->
                                    char.isDigit()
                                }
                        },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        label = {
                            Text("Sıra")
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = fieldColors
                    )

                    OutlinedTextField(
                        value = shelfNumber,
                        onValueChange = {
                            shelfNumber =
                                it.filter { char ->
                                    char.isDigit()
                                }
                        },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        label = {
                            Text("Raf")
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = fieldColors
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = position,
                    onValueChange = {
                        position = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = {
                        Text("Konum")
                    },
                    placeholder = {
                        Text("ÖN veya ARKA")
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = fieldColors
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        message?.let {

            Text(
                text = it,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = DarkRed
            )

            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = {

                message = null
                isSaving = true

                val formState =
                    ProductFormState(
                        productCode = productCode,
                        systemBarcode = systemBarcode,
                        color = color,
                        size = size,
                        quantity =
                            if (quantity.isBlank()) {
                                "0"
                            } else {
                                quantity
                            },
                        rowNumber = rowNumber,
                        shelfNumber = shelfNumber,
                        position = position,
                        photoUri = photoUri?.toString()
                    )

                val product =
                    formState.toProduct()

                when {

                    product.productCode.isBlank() -> {

                        message =
                            "Ürün kodu boş bırakılamaz."

                        isSaving = false
                    }

                    product.color.isBlank() -> {

                        message =
                            "Renk boş bırakılamaz."

                        isSaving = false
                    }

                    product.size.isBlank() -> {

                        message =
                            "Beden boş bırakılamaz."

                        isSaving = false
                    }

                    else -> {

                        productsViewModel.addProduct(
                            product = product
                        )
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            enabled = !isSaving,
            colors = ButtonDefaults.buttonColors(
                containerColor = DarkRed
            ),
            shape = RoundedCornerShape(14.dp)
        ) {

            Text(
                text =
                    if (isSaving) {
                        "KAYDEDİLİYOR..."
                    } else {
                        "ÜRÜNÜ KAYDET"
                    },
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}