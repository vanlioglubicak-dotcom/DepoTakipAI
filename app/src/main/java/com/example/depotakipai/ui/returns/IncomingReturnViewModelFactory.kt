package com.example.depotakipai.ui.returns

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.depotakipai.data.local.AppDatabase
import com.example.depotakipai.data.repository.ReturnRepositoryProvider
import com.example.depotakipai.domain.usecase.returns.SaveIncomingReturnUseCase

class IncomingReturnViewModelFactory(
    private val database: AppDatabase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (
            modelClass.isAssignableFrom(
                IncomingReturnViewModel::class.java
            )
        ) {

            val repository =
                ReturnRepositoryProvider.create(
                    database
                )

            val saveIncomingReturnUseCase =
                SaveIncomingReturnUseCase(
                    returnRepository = repository
                )

            return IncomingReturnViewModel(
                saveIncomingReturnUseCase =
                    saveIncomingReturnUseCase
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}