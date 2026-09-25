package com.example.depotakipai.ui.returns

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.depotakipai.data.local.entity.ReturnRecordEntity
import com.example.depotakipai.data.repository.ReturnRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OutgoingReturnViewModel(
    private val returnRepository: ReturnRepository
) : ViewModel() {

    private val _returns =
        MutableStateFlow<List<ReturnRecordEntity>>(emptyList())

    val returns: StateFlow<List<ReturnRecordEntity>> =
        _returns.asStateFlow()

    private val _isLoading =
        MutableStateFlow(false)

    val isLoading: StateFlow<Boolean> =
        _isLoading.asStateFlow()

    private val _errorMessage =
        MutableStateFlow<String?>(null)

    val errorMessage: StateFlow<String?> =
        _errorMessage.asStateFlow()

    fun loadOutgoingReturns() {

        if (_isLoading.value) {
            return
        }

        viewModelScope.launch {

            _isLoading.value = true
            _errorMessage.value = null

            try {

                val records =
                    returnRepository.getReturnsByType(
                        "OUTGOING"
                    )

                _returns.value = records

            } catch (exception: Exception) {

                _errorMessage.value =
                    exception.message
                        ?: "Giden iadeler yüklenirken bir hata oluştu."

            } finally {

                _isLoading.value = false
            }
        }
    }

    fun refresh() {
        loadOutgoingReturns()
    }
}