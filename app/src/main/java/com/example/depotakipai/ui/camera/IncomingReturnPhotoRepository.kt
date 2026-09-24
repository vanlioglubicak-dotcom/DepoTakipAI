package com.example.depotakipai.data.camera

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class IncomingReturnPhotoRepository(
    private val context: Context
) {

    suspend fun savePhoto(
        bitmap: Bitmap
    ): String? = withContext(Dispatchers.IO) {

        val fileName = "GelenIade_${
            SimpleDateFormat(
                "yyyyMMdd_HHmmss",
                Locale.US
            ).format(Date())
        }.jpg"

        val contentValues = ContentValues().apply {
            put(
                MediaStore.Images.Media.DISPLAY_NAME,
                fileName
            )
            put(
                MediaStore.Images.Media.MIME_TYPE,
                "image/jpeg"
            )
            put(
                MediaStore.Images.Media.RELATIVE_PATH,
                "Pictures/DepoTakipAI/GelenIadeler"
            )
        }

        val resolver = context.contentResolver

        val imageUri = resolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        ) ?: return@withContext null

        try {
            resolver.openOutputStream(imageUri)?.use { outputStream ->
                bitmap.compress(
                    Bitmap.CompressFormat.JPEG,
                    95,
                    outputStream
                )
            } ?: return@withContext null

            imageUri.toString()
        } catch (exception: Exception) {
            resolver.delete(
                imageUri,
                null,
                null
            )

            null
        }
    }
}