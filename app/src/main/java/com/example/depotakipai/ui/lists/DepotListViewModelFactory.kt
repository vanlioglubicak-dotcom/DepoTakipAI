package com.example.depotakipai.ui.lists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.depotakipai.data.repository.DepotListRecordRepository
import com.example.depotakipai.domain.usecase.GetDepotListRecordsUseCase
import com.example.depotakipai.domain.usecase.SaveDepotListRecordUseCase

class DepotListViewModelFactory(
    private val getDepotListRecordsUseCase: GetDepotListRecordsUseCase,
    private val saveDepotListRecordUseCase: SaveDepotListRecordUseCase,
    private val repository: DepotListRecordRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (
            modelClass.isAssignableFrom(
                DepotListViewModel::class.java
            )
        ) {

            return DepotListViewModel(
                getDepotListRecordsUseCase =
                    getDepotListRecordsUseCase,
                saveDepotListRecordUseCase =
                    saveDepotListRecordUseCase,
                repository = repository
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}