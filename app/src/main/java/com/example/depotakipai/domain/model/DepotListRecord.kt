package com.example.depotakipai.domain.model

import java.util.UUID

/**
 * Depo Listeleri içerisinde tutulacak tek kayıt.
 *
 * Örnek:
 * Ütü Paketi - 155 adet
 */
data class DepotListRecord(
    val id: String = UUID.randomUUID().toString(),

    /**
     * Kayıdın hangi listeye ait olduğu.
     */
    val category: DepotListCategory,

    /**
     * Ürün kodu.
     * Örnek: SNZ-2770
     */
    val productCode: String,

    /**
     * Ürün rengi.
     */
    val color: String = "",

    /**
     * Ürün bedeni.
     */
    val size: String = "",

    /**
     * Toplam adet.
     */
    val quantity: Int = 0,

    /**
     * Kayıt oluşturulma zamanı.
     */
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Depo Listeleri ana kategorileri.
 */
enum class DepotListCategory {

    /**
     * Yeni gelen ürünler.
     */
    YENI_URUNLER,

    /**
     * Depodan çıkan ürünler.
     */
    GIDEN,

    /**
     * Depoya gelen iadeler.
     */
    GELEN_IADE,

    /**
     * Depodan gönderilen iadeler.
     */
    GIDEN_IADE,

    /**
     * Kamerayla okunup onaylanan tüm kayıtlar.
     */
    OKUNANLAR
}