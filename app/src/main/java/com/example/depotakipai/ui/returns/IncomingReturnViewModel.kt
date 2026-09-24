package com.example.depotakipai.ui.returns

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.depotakipai.domain.model.IncomingReturnAnalysisResult
import com.example.depotakipai.domain.usecase.returns.SaveIncomingReturnUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class IncomingReturnViewModel(
    private val saveIncomingReturnUseCase: SaveIncomingReturnUseCase
) : ViewModel() {

    private val _analysisResult =
        MutableStateFlow<IncomingReturnAnalysisResult?>(null)

    val analysisResult: StateFlow<IncomingReturnAnalysisResult?> =
        _analysisResult.asStateFlow()

    private val _isSaving =
        MutableStateFlow(false)

    val isSaving: StateFlow<Boolean> =
        _isSaving.asStateFlow()

    private val _saveSuccess =
        MutableStateFlow<Long?>(null)

    val saveSuccess: StateFlow<Long?> =
        _saveSuccess.asStateFlow()

    private val _errorMessage =
        MutableStateFlow<String?>(null)

    val errorMessage: StateFlow<String?> =
        _errorMessage.asStateFlow()

    fun setAnalysisResult(
        result: IncomingReturnAnalysisResult
    ) {
        _analysisResult.value = result
        _saveSuccess.value = null
        _errorMessage.value = null
    }

    fun updateAnalysisResult(
        result: IncomingReturnAnalysisResult
    ) {
        _analysisResult.value = result
        _errorMessage.value = null
    }

    fun confirmReturn() {
        val result = _analysisResult.value
            ?: run {
                _errorMessage.value =
                    "Onaylanacak iade bilgisi bulunamadı."
                return
            }

        if (_isSaving.value) {
            return
        }

        viewModelScope.launch {
            _isSaving.value = true
            _saveSuccess.value = null
            _errorMessage.value = null

            try {
                val id = saveIncomingReturnUseCase(
                    result
                )

                _saveSuccess.value = id
            } catch (exception: Exception) {
                _errorMessage.value =
                    exception.message
                        ?: "İade kaydedilirken bir hata oluştu."
            } finally {
                _isSaving.value = false
            }
        }
    }

    fun clearSaveState() {
        _saveSuccess.value = null
        _errorMessage.value = null
    }
}