package com.example.depotakipai.data.catalog

import androidx.compose.ui.graphics.Color

data class CatalogColorOption(
    val name: String,
    val color: Color
)

object CatalogColorCatalog {

    /**
     * Uygulamanın merkezi renk kataloğu.
     *
     * Tüm modeller bu ortak renk listesini kullanır.
     * Model sayısı artsa bile burada yeni model için
     * ayrıca renk listesi oluşturulmaz.
     */
    val colors: List<CatalogColorOption> = listOf(

        CatalogColorOption(
            name = "Siyah",
            color = Color(0xFF151515)
        ),

        CatalogColorOption(
            name = "Beyaz",
            color = Color(0xFFFFFFFF)
        ),

        CatalogColorOption(
            name = "Kırmızı",
            color = Color(0xFFD32F2F)
        ),

        CatalogColorOption(
            name = "Bordo",
            color = Color(0xFF800020)
        ),

        CatalogColorOption(
            name = "Lacivert",
            color = Color(0xFF172A5C)
        ),

        CatalogColorOption(
            name = "Mavi",
            color = Color(0xFF1976D2)
        ),

        CatalogColorOption(
            name = "Açık Mavi",
            color = Color(0xFF64B5F6)
        ),

        CatalogColorOption(
            name = "Haki",
            color = Color(0xFF6B7043)
        ),

        CatalogColorOption(
            name = "Yeşil",
            color = Color(0xFF388E3C)
        ),

        CatalogColorOption(
            name = "Zümrüt",
            color = Color(0xFF00897B)
        ),

        CatalogColorOption(
            name = "Petrol",
            color = Color(0xFF006064)
        ),

        CatalogColorOption(
            name = "Mor",
            color = Color(0xFF7B1FA2)
        ),

        CatalogColorOption(
            name = "Lila",
            color = Color(0xFFB39DDB)
        ),

        CatalogColorOption(
            name = "Pembe",
            color = Color(0xFFE91E63)
        ),

        CatalogColorOption(
            name = "Pudra",
            color = Color(0xFFE8B7B7)
        ),

        CatalogColorOption(
            name = "Fuşya",
            color = Color(0xFFC2185B)
        ),

        CatalogColorOption(
            name = "Turuncu",
            color = Color(0xFFF57C00)
        ),

        CatalogColorOption(
            name = "Sarı",
            color = Color(0xFFFBC02D)
        ),

        CatalogColorOption(
            name = "Bej",
            color = Color(0xFFD7C4A3)
        ),

        CatalogColorOption(
            name = "Krem",
            color = Color(0xFFFFF1D0)
        ),

        CatalogColorOption(
            name = "Vanilya",
            color = Color(0xFFF3E5AB)
        ),

        CatalogColorOption(
            name = "Kahverengi",
            color = Color(0xFF6D4C41)
        ),

        CatalogColorOption(
            name = "Vizon",
            color = Color(0xFF8C7561)
        ),

        CatalogColorOption(
            name = "Gri",
            color = Color(0xFF808080)
        ),

        CatalogColorOption(
            name = "Antrasit",
            color = Color(0xFF424242)
        )
    )

    /**
     * Renk karşılaştırmalarında kullanılan standart normalizasyon.
     *
     * Örnek:
     * "Bordo"      -> "BORDO"
     * " bordo "    -> "BORDO"
     * "Kırmızı"    -> "KIRMIZI"
     * "KIRMIZI"    -> "KIRMIZI"
     */
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

    /**
     * İsme göre merkezi renk kataloğundan renk bulur.
     */
    fun findByName(
        value: String
    ): CatalogColorOption? {

        val normalized = normalize(value)

        return colors.firstOrNull { option ->
            normalize(option.name) == normalized
        }
    }

    /**
     * Rengin merkezi katalogda bulunup bulunmadığını kontrol eder.
     */
    fun contains(
        value: String
    ): Boolean {

        return findByName(value) != null
    }

    /**
     * Merkezi katalogdaki gerçek renk adını döndürür.
     *
     * Örneğin:
     * "bordo" -> "Bordo"
     * "LACİVERT" -> "Lacivert"
     */
    fun canonicalName(
        value: String
    ): String? {

        return findByName(value)?.name
    }
}