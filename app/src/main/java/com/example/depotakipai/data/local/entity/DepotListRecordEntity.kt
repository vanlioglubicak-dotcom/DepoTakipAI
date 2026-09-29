package com.example.depotakipai.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "depot_list_records"
)
data class DepotListRecordEntity(

    @PrimaryKey
    val id: String,

    /**
     * Liste kategorisi:
     *
     * YENI_URUNLER
     * GIDEN
     * GELEN_IADE
     * GIDEN_IADE
     * OKUNANLAR
     */
    val category: String,

    /**
     * Ürün kodu.
     * Örnek: SNZ-2770
     */
    val productCode: String,

    /**
     * Sistem barkodu.
     */
    val systemBarcode: String? = null,

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
     * Kaydın oluşturulma zamanı.
     */
    val createdAt: Long
)