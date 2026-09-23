package com.example.depotakipai.domain.usecase

import com.example.depotakipai.data.local.InventoryListDao

class DeleteExistingInventoryListUseCase(
    private val dao: InventoryListDao
) {

    suspend operator fun invoke(
        listId: Long
    ): Boolean {

        // Geçersiz ID silinemez.
        if (listId <= 0L) {
            return false
        }

        // Listenin gerçekten mevcut olup olmadığını kontrol et.
        val existingList = dao.getListById(listId)
            ?: return false

        // Önce bağlı ürün satırlarını sil.
        dao.deleteItemsByListId(existingList.id)

        // Daha sonra ana liste kaydını sil.
        dao.deleteList(existingList)

        return true
    }
}