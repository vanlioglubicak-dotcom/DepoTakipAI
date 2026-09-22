package com.example.depotakipai.domain.usecase

import com.example.depotakipai.domain.model.InventoryListType

class ValidateInventoryListDestinationUseCase {

    private val standardDestinations = setOf(
        "Ana Depo",
        "Ütü paket",
        "Mağaza",
        "Tedarikçi"
    )

    operator fun invoke(
        type: InventoryListType,
        destination: String?
    ): Boolean {

        // Sadece Giden İade için hedef zorunludur.
        if (type != InventoryListType.GIDEN_IADE) {
            return true
        }

        val value = destination
            ?.trim()
            .orEmpty()

        // Boş hedef kabul edilmez.
        if (value.isEmpty()) {
            return false
        }

        // Standart hedeflerden biri ise geçerlidir.
        if (value in standardDestinations) {
            return true
        }

        // "Diğer" seçildiğinde kullanıcı kendi hedefini
        // yazabileceği için boş olmaması yeterlidir.
        return value != "Diğer"
    }
}