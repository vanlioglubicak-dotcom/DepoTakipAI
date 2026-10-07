package com.example.depotakipai.ui.catalog.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CatalogColorEditDialog(
    visible: Boolean,
    initialColor: String = "",
    title: String = "Renk Düzenle",
    onSave: (String) -> Unit,
    onCancel: () -> Unit
) {
    if (!visible) {
        return
    }

    var colorName by remember(initialColor) {
        mutableStateOf(initialColor)
    }

    LaunchedEffect(initialColor) {
        colorName = initialColor
    }

    AlertDialog(
        onDismissRequest = onCancel,
        title = {
            Text(text = title)
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Modelin katalog rengini girin.",
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                OutlinedTextField(
                    value = colorName,
                    onValueChange = {
                        colorName = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = {
                        Text("Renk")
                    },
                    placeholder = {
                        Text("Örn. Lacivert")
                    }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val value = colorName.trim()

                    if (value.isNotBlank()) {
                        onSave(value)
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