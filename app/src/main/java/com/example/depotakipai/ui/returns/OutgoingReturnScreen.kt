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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.depotakipai.data.local.AppDatabase
import com.example.depotakipai.data.local.entity.ReturnRecordEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val OutgoingBackground = Color(0xFFF5F5F5)
private val OutgoingAccent = Color(0xFF4682B4)

@Composable
fun OutgoingReturnScreen(
    database: AppDatabase,
    onBack: () -> Unit = {}
) {
    val viewModel: OutgoingReturnViewModel =
        viewModel(
            factory = OutgoingReturnViewModelFactory(
                database = database
            )
        )

    val returns by viewModel.returns
        .collectAsStateWithLifecycle()

    val isLoading by viewModel.isLoading
        .collectAsStateWithLifecycle()

    val errorMessage by viewModel.errorMessage
        .collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadOutgoingReturns()
    }

    val dateText = SimpleDateFormat(
        "dd MMMM yyyy",
        Locale("tr", "TR")
    ).format(Date())

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(OutgoingBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(
                start = 20.dp,
                end = 20.dp,
                top = 14.dp,
                bottom = 12.dp
            ),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        item {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Giden İade",
                    color = OutgoingAccent,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = "Giden iade ürünlerini görüntüleyin.",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = dateText,
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        if (isLoading) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }

        errorMessage?.let { message ->
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Giden iadeler yüklenemedi.",
                            color = Color(0xFF8B0000),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(
                            modifier = Modifier.height(6.dp)
                        )

                        Text(
                            text = message,
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }

        if (
            !isLoading &&
            errorMessage == null &&
            returns.isEmpty()
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 2.dp
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Giden İadeler",
                            color = Color.DarkGray,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(
                            modifier = Modifier.height(6.dp)
                        )

                        Text(
                            text = "Henüz giden iade kaydı bulunmuyor.",
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }

        items(
            items = returns,
            key = { it.id }
        ) { returnRecord ->

            OutgoingReturnCard(
                returnRecord = returnRecord
            )
        }

        item {
            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier.height(38.dp)
                ) {
                    Text(
                        text = "GERİ",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun OutgoingReturnCard(
    returnRecord: ReturnRecordEntity
) {
    val dateText = SimpleDateFormat(
        "dd MMMM yyyy",
        Locale("tr", "TR")
    ).format(
        Date(returnRecord.createdAt)
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                text = returnRecord.productCode,
                color = OutgoingAccent,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "${returnRecord.color} • ${returnRecord.size}",
                color = Color.DarkGray,
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "${returnRecord.quantity} adet",
                color = Color.DarkGray,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )

            returnRecord.destination?.let { destination ->
                Text(
                    text = "Hedef: ${formatValue(destination)}",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Text(
                text = "Durum: ${formatValue(returnRecord.status)}",
                color = Color.Gray,
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = dateText,
                color = Color.Gray,
                style = MaterialTheme.typography.bodySmall
            )

            returnRecord.systemBarcode?.let { barcode ->
                Text(
                    text = "Sistem Barkodu: $barcode",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

private fun formatValue(
    value: String
): String {
    return value
        .replace("_", " ")
        .lowercase(Locale("tr", "TR"))
        .replaceFirstChar {
            it.titlecase(Locale("tr", "TR"))
        }
}