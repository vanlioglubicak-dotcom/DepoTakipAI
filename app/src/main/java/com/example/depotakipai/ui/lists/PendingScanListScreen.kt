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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.depotakipai.domain.model.CameraScanBatch
import com.example.depotakipai.domain.model.CameraScanItem
import java.util.UUID

/*
 * =============================================================
 * KAMERA EKRANIYLE AYNI RENK PALETİ
 * =============================================================
 */

private val StormBlue = Color(0xFF34506D)
private val BlueSlate = Color(0xFF557392)
private val DuskBlue = Color(0xFF7A8CA6)
private val FoggyBlue = Color(0xFFA1B2C4)

private val Gray = Color(0xFF777777)
private val LightBackground = Color(0xFFF5F5F5)
private val TextDark = Color(0xFF222222)

private val SuccessGreen = Color(0xFF2E7D32)

/*
 * =============================================================
 * GEÇİCİ ÜRÜN GRUBU
 * =============================================================
 */

private data class PendingProductGroup(
    val productCode: String,
    val color: String,
    val size: String,
    val systemBarcode: String?,
    val items: List<CameraScanItem>
) {
    val quantity: Int
        get() = items.size
}

/*
 * =============================================================
 * ANA EKRAN
 * =============================================================
 */

@Composable
fun PendingScanListScreen(
    title: String,
    initialBatch: CameraScanBatch,
    onBack: () -> Unit,
    onConfirm: (CameraScanBatch) -> Unit
) {

    var items by remember(initialBatch) {
        mutableStateOf(initialBatch.items)
    }

    var editingGroup by remember {
        mutableStateOf<PendingProductGroup?>(null)
    }

    /*
     * ---------------------------------------------------------
     * ÜRÜNLERİ GRUPLA
     * ---------------------------------------------------------
     */

    val groups = remember(items) {

        items
            .groupBy {
                Triple(
                    it.productCode,
                    it.color,
                    it.size
                )
            }
            .map { (_, groupedItems) ->

                val first = groupedItems.first()

                PendingProductGroup(
                    productCode = first.productCode,
                    color = first.color,
                    size = first.size,
                    systemBarcode = first.systemBarcode,
                    items = groupedItems
                )
            }
            .sortedWith(
                compareBy<PendingProductGroup> {
                    it.productCode
                }.thenBy {
                    it.color
                }.thenBy {
                    it.size
                }
            )
    }

    val totalQuantity = items.size

    val differentProducts = groups
        .map { it.productCode }
        .distinct()
        .size

    /*
     * =========================================================
     * ANA DÜZEN
     * =========================================================
     */

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBackground)
    ) {

        /*
         * =====================================================
         * ÜST BAŞLIK
         * =====================================================
         */

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(StormBlue)
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 16.dp,
                    bottom = 16.dp
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
                        .size(42.dp)
                        .clickable {
                            onBack()
                        }
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                ) {

                    Text(
                        text = title,
                        color = Color.White,
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(3.dp)
                    )

                    Text(
                        text = "Onay bekleyen okuma listesi",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 12.sp
                    )
                }
            }
        }

        /*
         * =====================================================
         * ÖZET
         * =====================================================
         */

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 14.dp,
                    bottom = 8.dp
                ),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            SummaryCard(
                modifier = Modifier.weight(1f),
                title = "OKUNAN",
                value = totalQuantity.toString(),
                color = StormBlue
            )

            SummaryCard(
                modifier = Modifier.weight(1f),
                title = "FARKLI ÜRÜN",
                value = differentProducts.toString(),
                color = BlueSlate
            )
        }

        /*
         * =====================================================
         * LİSTE
         * =====================================================
         */

        if (groups.isEmpty()) {

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = "Henüz okunmuş ürün yok.",
                    color = Gray,
                    fontSize = 15.sp
                )
            }

        } else {

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 8.dp,
                    bottom = 12.dp
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                items(
                    items = groups,
                    key = {
                        "${it.productCode}_${it.color}_${it.size}"
                    }
                ) { group ->

                    PendingProductCard(
                        group = group,
                        onEdit = {
                            editingGroup = group
                        },
                        onRemove = {

                            val idsToRemove =
                                group.items
                                    .map { it.id }
                                    .toSet()

                            items = items.filterNot {
                                it.id in idsToRemove
                            }
                        }
                    )
                }
            }
        }

        /*
         * =====================================================
         * SON ONAY
         * =====================================================
         */

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .navigationBarsPadding()
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 10.dp,
                    bottom = 10.dp
                )
        ) {

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),

                enabled = items.isNotEmpty(),

                onClick = {
                    onConfirm(
                        CameraScanBatch(
                            items = items
                        )
                    )
                },

                colors = ButtonDefaults.buttonColors(
                    containerColor = BlueSlate,
                    disabledContainerColor = FoggyBlue
                ),

                shape = RoundedCornerShape(14.dp)
            ) {

                Text(
                    text = "✓  ONAYLA",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    /*
     * =========================================================
     * DÜZELT PENCERESİ
     * =========================================================
     */

    editingGroup?.let { group ->

        EditPendingProductDialog(
            group = group,

            onDismiss = {
                editingGroup = null
            },

            onSave = { newCode, newColor, newSize, newQuantity ->

                val oldIds =
                    group.items
                        .map { it.id }
                        .toSet()

                val baseItem =
                    group.items.first()

                val newItems =
                    List(newQuantity.coerceAtLeast(1)) {

                        baseItem.copy(
                            id = UUID.randomUUID().toString(),
                            productCode = newCode,
                            color = newColor,
                            size = newSize,
                            scannedAt = System.currentTimeMillis()
                        )
                    }

                items =
                    items
                        .filterNot {
                            it.id in oldIds
                        } + newItems

                editingGroup = null
            }
        )
    }
}

/*
 * =============================================================
 * ÜRÜN KARTI
 * =============================================================
 */

@Composable
private fun PendingProductCard(
    group: PendingProductGroup,
    onEdit: () -> Unit,
    onRemove: () -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(12.dp),

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
                .padding(12.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = group.productCode,
                        color = StormBlue,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    Text(
                        text = "Renk: ${group.color.ifBlank { "BELİRSİZ" }}",
                        color = Gray,
                        fontSize = 13.sp
                    )

                    Text(
                        text = "Beden: ${group.size.ifBlank { "BELİRSİZ" }}",
                        color = Gray,
                        fontSize = 13.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .background(
                            color = DuskBlue.copy(alpha = 0.18f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(
                            horizontal = 12.dp,
                            vertical = 8.dp
                        )
                ) {

                    Text(
                        text = "${group.quantity} adet",
                        color = StormBlue,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Button(
                    modifier = Modifier.weight(1f),

                    onClick = onEdit,

                    colors = ButtonDefaults.buttonColors(
                        containerColor = BlueSlate
                    ),

                    shape = RoundedCornerShape(9.dp)
                ) {

                    Text(
                        text = "DÜZELT",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    modifier = Modifier.weight(1f),

                    onClick = onRemove,

                    colors = ButtonDefaults.buttonColors(
                        containerColor = StormBlue
                    ),

                    shape = RoundedCornerShape(9.dp)
                ) {

                    Text(
                        text = "ÇIKAR",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/*
 * =============================================================
 * ÖZET KARTI
 * =============================================================
 */

@Composable
private fun SummaryCard(
    modifier: Modifier,
    title: String,
    value: String,
    color: Color
) {

    Card(
        modifier = modifier,

        shape = RoundedCornerShape(12.dp),

        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 12.dp,
                    vertical = 11.dp
                ),

            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = title,
                color = TextDark,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.size(7.dp)
            )

            Text(
                text = value,
                color = color,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/*
 * =============================================================
 * DÜZELT DİYALOĞU
 * =============================================================
 */

@Composable
private fun EditPendingProductDialog(
    group: PendingProductGroup,
    onDismiss: () -> Unit,
    onSave: (
        productCode: String,
        color: String,
        size: String,
        quantity: Int
    ) -> Unit
) {

    var productCode by remember(group) {
        mutableStateOf(group.productCode)
    }

    var color by remember(group) {
        mutableStateOf(group.color)
    }

    var size by remember(group) {
        mutableStateOf(group.size)
    }

    var quantityText by remember(group) {
        mutableStateOf(group.quantity.toString())
    }

    AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            Text(
                text = "Ürün Düzelt",
                color = StormBlue,
                fontWeight = FontWeight.Bold
            )
        },

        text = {

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),

                    value = productCode,

                    onValueChange = {
                        productCode = it
                    },

                    label = {
                        Text("Ürün Kodu")
                    },

                    singleLine = true
                )

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),

                    value = color,

                    onValueChange = {
                        color = it
                    },

                    label = {
                        Text("Renk")
                    },

                    singleLine = true
                )

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),

                    value = size,

                    onValueChange = {
                        size = it
                    },

                    label = {
                        Text("Beden")
                    },

                    singleLine = true
                )

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),

                    value = quantityText,

                    onValueChange = {
                        if (it.all { char -> char.isDigit() }) {
                            quantityText = it
                        }
                    },

                    label = {
                        Text("Adet")
                    },

                    singleLine = true
                )
            }
        },

        confirmButton = {

            TextButton(
                onClick = {

                    val quantity =
                        quantityText
                            .toIntOrNull()
                            ?.coerceAtLeast(1)
                            ?: 1

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
                    color = BlueSlate,
                    fontWeight = FontWeight.Bold
                )
            }
        },

        dismissButton = {

            TextButton(
                onClick = onDismiss
            ) {

                Text(
                    text = "İPTAL",
                    color = DuskBlue
                )
            }
        }
    )
}