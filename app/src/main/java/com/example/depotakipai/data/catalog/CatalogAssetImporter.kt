package com.example.depotakipai.data.catalog

import android.content.Context
import com.example.depotakipai.data.local.entity.CatalogItemEntity
import kotlinx.coroutines.flow.first

class CatalogAssetImporter(
    private val context: Context,
    private val repository: CatalogRepository
) {

    suspend fun importIfEmpty() {

        // Katalog zaten doluysa tekrar içe aktarma.
        val existingItems = repository
            .getAll()
            .first()

        if (existingItems.isNotEmpty()) {
            return
        }

        val now = System.currentTimeMillis()

        val items = context.assets
            .open("catalog/catalog_items.csv")
            .bufferedReader(Charsets.UTF_8)
            .useLines { lines ->

                lines
                    .drop(1)
                    .mapIndexedNotNull { index, line ->

                        val parts = parseCsvLine(line)

                        // CatalogItemEntity için gerekli minimum
                        // sütun sayısı 7'dir:
                        //
                        // 0 = id
                        // 1 = productNumber
                        // 2 = company
                        // 3 = imagePath
                        // 4 = catalogPage
                        // 5 = color
                        // 6 = size

                        if (parts.size < 7) {
                            null
                        } else {

                            val id = parts[0].trim()

                            val productNumber = parts[1]
                                .trim()

                            val company = normalizeCompany(
                                parts[2].trim()
                            )

                            val imagePath = parts[3]
                                .trim()

                            val catalogPage = parts[4]
                                .trim()
                                .toIntOrNull()
                                ?: 0

                            val color = parts[5]
                                .trim()

                            val size = parts[6]
                                .trim()

                            if (
                                id.isBlank() ||
                                productNumber.isBlank() ||
                                imagePath.isBlank()
                            ) {
                                null
                            } else {

                                CatalogItemEntity(
                                    id = id,
                                    productNumber = productNumber,
                                    company = company,
                                    imagePath = imagePath,
                                    catalogPage = catalogPage,
                                    color = color,
                                    size = size,
                                    createdAt = now + index
                                )
                            }
                        }
                    }
                    .toList()
            }

        if (items.isNotEmpty()) {
            repository.insertAll(items)
        }
    }

    /**
     * PDF'deki farklı firma yazımlarını
     * uygulamadaki standart firma adına çevirir.
     *
     * Örnek:
     * EY -> SNZ
     * SIEY -> SNZ
     * SİEY -> SNZ
     */
    private fun normalizeCompany(
        value: String
    ): String {

        val normalized = value
            .trim()
            .uppercase()
            .replace("İ", "I")
            .replace(" ", "")

        return when (normalized) {

            "EY",
            "SIEY",
            "SIEY",
            "SİEY" -> "SNZ"

            else -> value.trim()
        }
    }

    /**
     * Basit CSV parser.
     *
     * Virgül içeren ve tırnak içine alınmış
     * alanları da düzgün ayırır.
     */
    private fun parseCsvLine(
        line: String
    ): List<String> {

        val result = mutableListOf<String>()
        val current = StringBuilder()

        var insideQuotes = false
        var index = 0

        while (index < line.length) {

            val char = line[index]

            when {

                char == '"' -> {

                    if (
                        insideQuotes &&
                        index + 1 < line.length &&
                        line[index + 1] == '"'
                    ) {

                        current.append('"')
                        index++

                    } else {

                        insideQuotes = !insideQuotes
                    }
                }

                char == ',' && !insideQuotes -> {

                    result.add(
                        current.toString()
                    )

                    current.clear()
                }

                else -> {

                    current.append(char)
                }
            }

            index++
        }

        result.add(
            current.toString()
        )

        return result
    }
}