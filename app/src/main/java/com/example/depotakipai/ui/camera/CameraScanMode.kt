package com.example.depotakipai.ui.camera

/**
 * Kameranın hangi amaçla çalıştığını belirler.
 *
 * PRODUCT:
 * Ana ürün tarama ekranı.
 *
 * INCOMING_RETURN:
 * Gelen iade tarama ekranı.
 * Bu modda çekilen görüntü galeriye kaydedilmez.
 */
enum class CameraScanMode {
    PRODUCT,
    INCOMING_RETURN
}