package com.example.depotakipai.ui.lists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.depotakipai.data.repository.DepotListRecordRepository
import com.example.depotakipai.domain.model.DepotListCategory
import com.example.depotakipai.domain.model.DepotListRecord
import com.example.depotakipai.domain.usecase.GetDepotListRecordsUseCase
import com.example.depotakipai.domain.usecase.SaveDepotListRecordUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DepotListViewModel(
    private val getDepotListRecordsUseCase: GetDepotListRecordsUseCase,
    private val saveDepotListRecordUseCase: SaveDepotListRecordUseCase,
    private val repository: DepotListRecordRepository
) : ViewModel() {

    private val _records =
        MutableStateFlow<List<DepotListRecord>>(emptyList())

    val records: StateFlow<List<DepotListRecord>> =
        _records.asStateFlow()

    private val _selectedCategory =
        MutableStateFlow<DepotListCategory?>(null)

    val selectedCategory: StateFlow<DepotListCategory?> =
        _selectedCategory.asStateFlow()

    init {
        observeRecords()
    }

    private fun observeRecords() {
        viewModelScope.launch {
            getDepotListRecordsUseCase()
                .collect { records ->
                    _records.value = records
                }
        }
    }

    fun selectCategory(
        category: DepotListCategory?
    ) {
        _selectedCategory.value = category

        viewModelScope.launch {
            if (category == null) {
                getDepotListRecordsUseCase()
                    .collect { records ->
                        _records.value = records
                    }
            } else {
                getDepotListRecordsUseCase(category)
                    .collect { records ->
                        _records.value = records
                    }
            }
        }
    }

    fun saveRecord(
        record: DepotListRecord
    ) {
        viewModelScope.launch {
            saveDepotListRecordUseCase(record)
        }
    }

    fun saveRecords(
        records: List<DepotListRecord>
    ) {
        viewModelScope.launch {
            if (records.isNotEmpty()) {
                saveDepotListRecordUseCase(records)
            }
        }
    }

    fun deleteRecord(
        record: DepotListRecord
    ) {
        viewModelScope.launch {
            repository.delete(record)
        }
    }

    fun deleteRecords(
        records: List<DepotListRecord>
    ) {
        viewModelScope.launch {
            records.forEach { record ->
                repository.delete(record)
            }
        }
    }

    fun deleteRecordById(
        id: String
    ) {
        viewModelScope.launch {
            repository.deleteById(id)
        }
    }
}