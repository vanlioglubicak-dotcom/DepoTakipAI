package com.example.depotakipai.ui.catalog.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CatalogModelEditDialog(
    visible: Boolean,
    initialModelNumber: String,
    onSave: (String) -> Unit,
    onCancel: () -> Unit
) {
    if (!visible) {
        return
    }

    var modelNumber by remember(initialModelNumber) {
        mutableStateOf(initialModelNumber)
    }

    LaunchedEffect(initialModelNumber) {
        modelNumber = initialModelNumber
    }

    AlertDialog(
        onDismissRequest = onCancel,

        title = {
            Text(
                text = "MODEL KODU DÜZENLE"
            )
        },

        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Model kodunu değiştirin.",
                    modifier = Modifier.padding(
                        bottom = 10.dp
                    )
                )

                OutlinedTextField(
                    value = modelNumber,
                    onValueChange = {
                        modelNumber = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = {
                        Text("Model Kodu")
                    },
                    placeholder = {
                        Text("Örn. SNZ-0011")
                    },
                    shape = RoundedCornerShape(10.dp)
                )
            }
        },

        confirmButton = {
            Button(
                onClick = {
                    val cleaned = modelNumber
                        .trim()
                        .removePrefix("SNZ-")
                        .removePrefix("snz-")
                        .trim()

                    if (cleaned.isNotBlank()) {
                        onSave(cleaned)
                    }
                },
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("KAYDET")
            }
        },

        dismissButton = {
            TextButton(
                onClick = onCancel
            ) {
                Text("İPTAL")
            }
        }
    )
}