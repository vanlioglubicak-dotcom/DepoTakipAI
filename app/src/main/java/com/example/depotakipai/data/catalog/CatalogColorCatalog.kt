package com.example.depotakipai.data.catalog

import androidx.compose.ui.graphics.Color

data class CatalogColorOption(
    val name: String,
    val color: Color
)

object CatalogColorCatalog {

    val colors: List<CatalogColorOption> = listOf(

        CatalogColorOption(
            "Siyah",
            Color(0xFF111111)
        ),

        CatalogColorOption(
            "Beyaz",
            Color(0xFFFFFFFF)
        ),

        CatalogColorOption(
            "Kırmızı",
            Color(0xFFD32F2F)
        ),

        CatalogColorOption(
            "Bordo",
            Color(0xFF8E0038)
        ),

        CatalogColorOption(
            "Lacivert",
            Color(0xFF172A55)
        ),

        CatalogColorOption(
            "Mavi",
            Color(0xFF1976D2)
        ),

        CatalogColorOption(
            "Açık Mavi",
            Color(0xFF64B5F6)
        ),

        CatalogColorOption(
            "Haki",
            Color(0xFF68734D)
        ),

        CatalogColorOption(
            "Yeşil",
            Color(0xFF388E3C)
        ),

        CatalogColorOption(
            "Zümrüt",
            Color(0xFF00897B)
        ),

        CatalogColorOption(
            "Petrol",
            Color(0xFF006064)
        ),

        CatalogColorOption(
            "Mor",
            Color(0xFF6A1B9A)
        ),

        CatalogColorOption(
            "Lila",
            Color(0xFFBA68C8)
        ),

        CatalogColorOption(
            "Pembe",
            Color(0xFFE91E63)
        ),

        CatalogColorOption(
            "Pudra",
            Color(0xFFE8B7B7)
        ),

        CatalogColorOption(
            "Fuşya",
            Color(0xFFC2185B)
        ),

        CatalogColorOption(
            "Turuncu",
            Color(0xFFF57C00)
        ),

        CatalogColorOption(
            "Sarı",
            Color(0xFFFBC02D)
        ),

        CatalogColorOption(
            "Bej",
            Color(0xFFD7CCC8)
        ),

        CatalogColorOption(
            "Krem",
            Color(0xFFFFF3D6)
        ),

        CatalogColorOption(
            "Vanilya",
            Color(0xFFF3E5AB)
        ),

        CatalogColorOption(
            "Kahverengi",
            Color(0xFF795548)
        ),

        CatalogColorOption(
            "Vizon",
            Color(0xFF9E8C7A)
        ),

        CatalogColorOption(
            "Gri",
            Color(0xFF9E9E9E)
        ),

        CatalogColorOption(
            "Antrasit",
            Color(0xFF424242)
        )
    )


    fun normalize(
        value: String
    ): String {

        return value
            .trim()
            .uppercase()
            .replace("İ", "I")
            .replace("Ğ", "G")
            .replace("Ü", "U")
            .replace("Ş", "S")
            .replace("Ö", "O")
            .replace("Ç", "C")
            .replace("-", "")
            .replace("_", "")
            .replace(" ", "")
    }


    fun findByName(
        value: String
    ): CatalogColorOption? {

        val normalized =
            normalize(value)

        return colors.firstOrNull {

            normalize(it.name) ==
                    normalized
        }
    }
}