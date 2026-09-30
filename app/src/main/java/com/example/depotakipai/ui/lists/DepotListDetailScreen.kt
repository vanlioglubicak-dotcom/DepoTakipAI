package com.example.depotakipai.ui.lists

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.depotakipai.domain.model.DepotListCategory
import com.example.depotakipai.domain.model.DepotListRecord

private val DarkRed = Color(0xFF8B0000)
private val SteelBlue = Color(0xFF4682B4)
private val LightBackground = Color(0xFFF5F5F5)
private val TextDark = Color(0xFF222222)

private data class GroupedDepotListRecord(
    val productCode: String,
    val color: String,
    val size: String,
    val quantity: Int,
    val latestCreatedAt: Long,
    val records: List<DepotListRecord>
)

@Composable
fun DepotListDetailScreen(
    category: DepotListCategory,
    viewModel: DepotListViewModel,
    onBack: () -> Unit
) {
    val records by viewModel.records.collectAsState()

    val categoryRecords = records.filter {
        it.category == category
    }

    val groupedRecords =
        categoryRecords
            .groupBy {
                Triple(
                    it.productCode,
                    it.color,
                    it.size
                )
            }
            .map { (key, groupedItems) ->
                GroupedDepotListRecord(
                    productCode = key.first,
                    color = key.second,
                    size = key.third,
                    quantity = groupedItems.sumOf {
                        it.quantity
                    },
                    latestCreatedAt = groupedItems.maxOfOrNull {
                        it.createdAt
                    } ?: 0L,
                    records = groupedItems
                )
            }
            .sortedByDescending {
                it.latestCreatedAt
            }

    val totalQuantity =
        groupedRecords.sumOf {
            it.quantity
        }

    var selectedCardKey by remember {
        mutableStateOf<String?>(null)
    }

    var recordToDelete by remember {
        mutableStateOf<GroupedDepotListRecord?>(null)
    }

    var recordToEdit by remember {
        mutableStateOf<GroupedDepotListRecord?>(null)
    }

    /*
     * =========================================================
     * SİLME DİYALOĞU
     * =========================================================
     */

    recordToDelete?.let { record ->

        AlertDialog(
            onDismissRequest = {
                recordToDelete = null
            },

            title = {
                Text(
                    text = "Ürünü Sil"
                )
            },

            text = {
                Text(
                    text =
                        "${record.productCode} ürününü ve ${record.quantity} adet kaydını silmek istediğinize emin misiniz?"
                )
            },

            confirmButton = {

                TextButton(
                    onClick = {

                        viewModel.deleteRecords(
                            record.records
                        )

                        recordToDelete = null
                        selectedCardKey = null
                    }
                ) {

                    Text(
                        text = "SİL",
                        color = DarkRed,
                        fontWeight = FontWeight.Bold
                    )
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {
                        recordToDelete = null
                    }
                ) {

                    Text(
                        text = "İPTAL"
                    )
                }
            }
        )
    }

    /*
     * =========================================================
     * DÜZENLEME DİYALOĞU
     * =========================================================
     */

    recordToEdit?.let { record ->

        EditDepotListRecordDialog(
            record = record,

            onDismiss = {
                recordToEdit = null
            },

            onSave = {
                    productCode,
                    color,
                    size,
                    quantity ->

                viewModel.updateRecords(
                    records = record.records,
                    productCode = productCode,
                    color = color,
                    size = size,
                    quantity = quantity
                )

                recordToEdit = null
            }
        )
    }

    /*
     * =========================================================
     * ANA EKRAN
     * =========================================================
     */

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBackground)
            .navigationBarsPadding()
    ) {

        /*
         * =====================================================
         * ÜST BAŞLIK
         * =====================================================
         */

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkRed)
                .padding(
                    start = 20.dp,
                    end = 20.dp,
                    top = 18.dp,
                    bottom = 18.dp
                )
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "‹",
                    color = Color.White,
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Light,

                    modifier = Modifier
                        .clickable {
                            onBack()
                        }
                        .padding(end = 10.dp)
                )

                Column {

                    Text(
                        text = categoryTitle(category),
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(2.dp)
                    )

                    Text(
                        text =
                            "${groupedRecords.size} ürün • $totalQuantity adet",

                        color = Color.White.copy(
                            alpha = 0.85f
                        ),

                        fontSize = 13.sp
                    )
                }
            }
        }

        /*
         * =====================================================
         * LİSTE
         * =====================================================
         */

        if (groupedRecords.isEmpty()) {

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = "Bu listede henüz kayıt yok.",
                    color = Color.Gray,
                    fontSize = 15.sp
                )
            }

        } else {

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        horizontal = 16.dp,
                        vertical = 10.dp
                    ),

                verticalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {

                items(
                    items = groupedRecords,

                    key = {
                        "${it.productCode}_${it.color}_${it.size}"
                    }
                ) { record ->

                    val cardKey =
                        "${record.productCode}_${record.color}_${record.size}"

                    DepotListRecordCard(
                        record = record,

                        isSelected =
                            selectedCardKey == cardKey,

                        onClick = {

                            selectedCardKey =
                                if (selectedCardKey == cardKey) {
                                    null
                                } else {
                                    cardKey
                                }
                        },

                        onEdit = {
                            recordToEdit = record
                        },

                        onDelete = {
                            recordToDelete = record
                        }
                    )
                }
            }
        }
    }
}

/*
 * =============================================================
 * ÜRÜN KARTI
 * =============================================================
 */

@Composable
private fun DepotListRecordCard(
    record: GroupedDepotListRecord,
    isSelected: Boolean,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },

        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(8.dp)
        ) {

            /*
             * =================================================
             * ÜRÜN BİLGİLERİ
             * =================================================
             */

            Row(
                modifier = Modifier.fillMaxWidth(),

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = record.productCode,

                        color = TextDark,

                        fontSize = 18.sp,

                        fontWeight =
                            FontWeight.Bold
                    )

                    if (record.color.isNotBlank()) {

                        Spacer(
                            modifier =
                                Modifier.height(1.dp)
                        )

                        Text(
                            text =
                                "Renk: ${record.color}",

                            color = Color.Gray,

                            fontSize = 13.sp
                        )
                    }

                    if (record.size.isNotBlank()) {

                        Text(
                            text =
                                "Beden: ${record.size}",

                            color = Color.Gray,

                            fontSize = 13.sp
                        )
                    }
                }

                /*
                 * ADET
                 */

                Column(
                    horizontalAlignment =
                        Alignment.End
                ) {

                    Text(
                        text =
                            "${record.quantity}",

                        color = SteelBlue,

                        fontSize = 23.sp,

                        fontWeight =
                            FontWeight.Bold
                    )

                    Text(
                        text = "adet",

                        color = Color.Gray,

                        fontSize = 11.sp
                    )
                }
            }

            /*
             * =================================================
             * SADECE KART SEÇİLİNCE BUTONLAR
             * =================================================
             */

            if (isSelected) {

                Spacer(
                    modifier =
                        Modifier.height(2.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),

                    horizontalArrangement =
                        Arrangement.End,

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Text(
                        text = "DÜZENLE",

                        color = SteelBlue,

                        fontSize = 12.sp,

                        fontWeight =
                            FontWeight.Bold,

                        modifier = Modifier
                            .clickable {
                                onEdit()
                            }
                            .padding(
                                horizontal = 7.dp,
                                vertical = 3.dp
                            )
                    )

                    Text(
                        text = "SİL",

                        color = DarkRed,

                        fontSize = 12.sp,

                        fontWeight =
                            FontWeight.Bold,

                        modifier = Modifier
                            .clickable {
                                onDelete()
                            }
                            .padding(
                                horizontal = 7.dp,
                                vertical = 3.dp
                            )
                    )
                }
            }
        }
    }
}

/*
 * =============================================================
 * DÜZENLEME DİYALOĞU
 * =============================================================
 */

@Composable
private fun EditDepotListRecordDialog(
    record: GroupedDepotListRecord,

    onDismiss: () -> Unit,

    onSave: (
        productCode: String,
        color: String,
        size: String,
        quantity: Int
    ) -> Unit
) {

    var productCode by remember(record) {
        mutableStateOf(
            record.productCode
        )
    }

    var color by remember(record) {
        mutableStateOf(
            record.color
        )
    }

    var size by remember(record) {
        mutableStateOf(
            record.size
        )
    }

    var quantityText by remember(record) {
        mutableStateOf(
            record.quantity.toString()
        )
    }

    val quantity =
        quantityText.toIntOrNull() ?: 0

    val canSave =
        productCode.isNotBlank() &&
                quantity > 0

    AlertDialog(

        onDismissRequest = onDismiss,

        title = {

            Text(
                text = "Ürünü Düzenle",

                fontWeight =
                    FontWeight.Bold
            )
        },

        text = {

            Column(
                verticalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {

                OutlinedTextField(
                    modifier =
                        Modifier.fillMaxWidth(),

                    value = productCode,

                    onValueChange = {
                        productCode = it
                    },

                    singleLine = true,

                    label = {
                        Text("Ürün Kodu")
                    }
                )

                OutlinedTextField(
                    modifier =
                        Modifier.fillMaxWidth(),

                    value = color,

                    onValueChange = {
                        color = it
                    },

                    singleLine = true,

                    label = {
                        Text("Renk")
                    }
                )

                OutlinedTextField(
                    modifier =
                        Modifier.fillMaxWidth(),

                    value = size,

                    onValueChange = {
                        size = it
                    },

                    singleLine = true,

                    label = {
                        Text("Beden")
                    }
                )

                OutlinedTextField(
                    modifier =
                        Modifier.fillMaxWidth(),

                    value = quantityText,

                    onValueChange = { value ->

                        quantityText =
                            value.filter {
                                it.isDigit()
                            }
                    },

                    singleLine = true,

                    keyboardOptions =
                        KeyboardOptions(
                            keyboardType =
                                KeyboardType.Number
                        ),

                    label = {
                        Text("Adet")
                    }
                )
            }
        },

        confirmButton = {

            TextButton(
                enabled = canSave,

                onClick = {

                    onSave(
                        productCode.trim(),
                        color.trim(),
                        size.trim(),
                        quantity
                    )
                }
            ) {

                Text(
                    text = "KAYDET",

                    color =
                        if (canSave) {
                            SteelBlue
                        } else {
                            Color.Gray
                        },

                    fontWeight =
                        FontWeight.Bold
                )
            }
        },

        dismissButton = {

            TextButton(
                onClick = onDismiss
            ) {

                Text(
                    text = "İPTAL"
                )
            }
        }
    )
}

/*
 * =============================================================
 * KATEGORİ BAŞLIĞI
 * =============================================================
 */

private fun categoryTitle(
    category: DepotListCategory
): String {

    return when (category) {

        DepotListCategory.YENI_URUNLER ->
            "Yeni Ürünler"

        DepotListCategory.GIDEN ->
            "Giden"

        DepotListCategory.GELEN_IADE ->
            "Gelen İade"

        DepotListCategory.GIDEN_IADE ->
            "Giden İade"

        DepotListCategory.OKUNANLAR ->
            "Okunanlar"
    }
}