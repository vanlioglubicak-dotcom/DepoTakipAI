package com.example.depotakipai.ui.lists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.depotakipai.domain.usecase.GetDepotListRecordsUseCase
import com.example.depotakipai.domain.usecase.SaveDepotListRecordUseCase

class DepotListViewModelFactory(
    private val getDepotListRecordsUseCase: GetDepotListRecordsUseCase,
    private val saveDepotListRecordUseCase: SaveDepotListRecordUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (modelClass.isAssignableFrom(DepotListViewModel::class.java)) {
            return DepotListViewModel(
                getDepotListRecordsUseCase = getDepotListRecordsUseCase,
                saveDepotListRecordUseCase = saveDepotListRecordUseCase
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}