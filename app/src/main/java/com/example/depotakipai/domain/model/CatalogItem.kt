package com.example.depotakipai.domain.model

import java.util.UUID

data class CatalogItem(
    val id: String = UUID.randomUUID().toString(),

    // Katalogdan OCR ile okunan esas numara.
    // Örnek: EY01 -> 01
    val productNumber: String,

    // Kayıt sırasında kullanıcı seçer:
    // SNZ veya MTS
    val company: String,

    // Katalogdaki ürün görselinin cihazdaki yolu
    val imagePath: String,

    // Katalog PDF'sindeki sayfa
    val catalogPage: Int = 0,

    // Katalogdan okunabilen renk
    val color: String = "",

    // Katalogdan okunabilen beden
    val size: String = "",

    val createdAt: Long = System.currentTimeMillis()
)