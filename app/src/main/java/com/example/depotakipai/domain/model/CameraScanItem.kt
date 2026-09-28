package com.example.depotakipai.domain.model

import java.util.UUID

/**
 * Kameranın tek bir başarılı taramada elde ettiği ürün bilgisi.
 *
 * Her tarama ayrı bir kayıt olarak tutulur.
 * Aynı ürün tekrar okutulursa yeni bir kayıt oluşur;
 * toplamlar daha sonra aggregation fonksiyonlarıyla hesaplanır.
 */
data class CameraScanItem(
    val id: String = UUID.randomUUID().toString(),

    /**
     * Örnek:
     * SNZ-2926
     * MTS-2949
     * FS-1393
     */
    val productCode: String,

    /**
     * Sistemin kullandığı 8 haneli barkod.
     * Örnek:
     * 00002926
     */
    val systemBarcode: String? = null,

    /**
     * Örnek:
     * SİYAH
     * BORDO
     * BEYAZ
     */
    val color: String,

    /**
     * Örnek:
     * S
     * M
     * L
     * XL
     */
    val size: String,

    /**
     * Taramanın gerçekleştiği zaman.
     */
    val scannedAt: Long = System.currentTimeMillis()
)

/**
 * Ürün kodu bazında toplam adet.
 *
 * Örnek:
 *
 * SNZ-2926 -> 24
 * SNZ-2759 -> 15
 * MTS-2949 -> 12
 */
data class ProductScanSummary(
    val productCode: String,
    val totalQuantity: Int
)

/**
 * Renk + beden bazındaki adet.
 *
 * Örnek:
 *
 * SİYAH / S -> 5
 * SİYAH / M -> 8
 * SİYAH / L -> 2
 */
data class ColorSizeScanSummary(
    val color: String,
    val size: String,
    val quantity: Int
)

/**
 * Kamera tarama oturumunun tamamını temsil eder.
 *
 * Burada herhangi bir 100 / 1000 / 10000 adet sınırı yoktur.
 */
data class CameraScanBatch(
    val items: List<CameraScanItem> = emptyList()
) {

    /**
     * Toplam okunan adet.
     */
    val totalQuantity: Int
        get() = items.size

    /**
     * Kaç farklı ürün kodu bulunduğu.
     */
    val differentProductCount: Int
        get() = items
            .asSequence()
            .map { it.productCode }
            .distinct()
            .count()

    /**
     * Ürün koduna göre toplamları hesaplar.
     *
     * Örnek:
     *
     * 300 tarama
     * ↓
     * 15 farklı ürün
     */
    fun groupByProductCode(): List<ProductScanSummary> {
        return items
            .groupingBy { it.productCode }
            .eachCount()
            .map { (productCode, quantity) ->
                ProductScanSummary(
                    productCode = productCode,
                    totalQuantity = quantity
                )
            }
            .sortedBy { it.productCode }
    }

    /**
     * Seçilen ürün kodunun renk + beden kırılımını verir.
     *
     * Örnek:
     *
     * SNZ-2926
     *
     * SİYAH
     *   S -> 5
     *   M -> 8
     *   L -> 2
     *
     * BORDO
     *   S -> 3
     *   L -> 2
     */
    fun groupByColorAndSize(
        productCode: String
    ): List<ColorSizeScanSummary> {
        return items
            .asSequence()
            .filter {
                it.productCode == productCode
            }
            .groupingBy {
                it.color to it.size
            }
            .eachCount()
            .map { (colorAndSize, quantity) ->
                ColorSizeScanSummary(
                    color = colorAndSize.first,
                    size = colorAndSize.second,
                    quantity = quantity
                )
            }
            .sortedWith(
                compareBy<ColorSizeScanSummary> {
                    it.color
                }.thenBy {
                    it.size
                }
            )
    }

    /**
     * Seçilen ürün kodunun toplam adedini verir.
     */
    fun getProductQuantity(
        productCode: String
    ): Int {
        return items.count {
            it.productCode == productCode
        }
    }

    /**
     * Yeni bir taramayı oturuma ekler.
     */
    fun addItem(
        item: CameraScanItem
    ): CameraScanBatch {
        return copy(
            items = items + item
        )
    }

    /**
     * Birden fazla taramayı aynı anda ekler.
     */
    fun addItems(
        newItems: List<CameraScanItem>
    ): CameraScanBatch {
        return copy(
            items = items + newItems
        )
    }

    /**
     * Tarama oturumunu temizler.
     */
    fun clear(): CameraScanBatch {
        return CameraScanBatch()
    }
}