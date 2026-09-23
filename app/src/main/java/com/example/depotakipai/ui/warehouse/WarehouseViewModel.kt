package com.example.depotakipai.ui.warehouse

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.depotakipai.domain.model.WarehouseRack
import com.example.depotakipai.domain.model.WarehouseRow
import com.example.depotakipai.domain.repository.WarehouseRepositoryProvider
import com.example.depotakipai.domain.usecase.warehouse.GetWarehouseStructureUseCase
import com.example.depotakipai.domain.usecase.warehouse.InitializeWarehouseUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class WarehouseUiState(
    val isLoading: Boolean = false,
    val rows: List<WarehouseRow> = emptyList(),
    val racks: Map<Long, List<WarehouseRack>> = emptyMap(),
    val errorMessage: String? = null
)

class WarehouseViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository =
        WarehouseRepositoryProvider.provide(
            application.applicationContext
        )

    private val initializeWarehouseUseCase =
        InitializeWarehouseUseCase(
            warehouseRepository = repository
        )

    private val getWarehouseStructureUseCase =
        GetWarehouseStructureUseCase(
            warehouseRepository = repository
        )

    private val _uiState =
        MutableStateFlow(
            WarehouseUiState(
                isLoading = true
            )
        )

    val uiState: StateFlow<WarehouseUiState> =
        _uiState.asStateFlow()

    init {
        loadWarehouse()
    }

    fun loadWarehouse() {

        viewModelScope.launch {

            _uiState.value =
                _uiState.value.copy(
                    isLoading = true,
                    errorMessage = null
                )

            try {

                initializeWarehouseUseCase()

                val structure =
                    getWarehouseStructureUseCase()

                _uiState.value =
                    WarehouseUiState(
                        isLoading = false,
                        rows = structure.rows,
                        racks = structure.racks,
                        errorMessage = null
                    )

            } catch (exception: Exception) {

                _uiState.value =
                    _uiState.value.copy(
                        isLoading = false,
                        errorMessage =
                            exception.message
                                ?: "Depo verileri yüklenemedi."
                    )
            }
        }
    }

    fun getRacksForRow(
        rowId: Long
    ): List<WarehouseRack> {

        return _uiState.value.racks[rowId]
            ?: emptyList()
    }
}