package com.example.depotakipai.ui.returns

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.depotakipai.data.local.AppDatabase
import com.example.depotakipai.data.repository.ReturnRepositoryProvider

class OutgoingReturnViewModelFactory(
    private val database: AppDatabase
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (
            modelClass.isAssignableFrom(
                OutgoingReturnViewModel::class.java
            )
        ) {

            val repository =
                ReturnRepositoryProvider.create(
                    database
                )

            @Suppress("UNCHECKED_CAST")
            return OutgoingReturnViewModel(
                returnRepository = repository
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}