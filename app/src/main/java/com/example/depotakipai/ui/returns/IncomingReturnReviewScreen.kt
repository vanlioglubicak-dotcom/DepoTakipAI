package com.example.depotakipai.ui.returns

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.depotakipai.data.local.AppDatabase
import com.example.depotakipai.domain.model.IncomingReturnAnalysisResult

@Composable
fun IncomingReturnReviewScreen(
    database: AppDatabase,
    result: IncomingReturnAnalysisResult,
    onBack: () -> Unit = {},
    onEdit: () -> Unit = {},
    onConfirmSuccess: () -> Unit = {}
) {
    val viewModel: IncomingReturnViewModel =
        viewModel(
            factory = IncomingReturnViewModelFactory(
                database = database
            )
        )

    val saveSuccess by viewModel.saveSuccess
        .collectAsStateWithLifecycle()

    val errorMessage by viewModel.errorMessage
        .collectAsStateWithLifecycle()

    LaunchedEffect(result) {
        viewModel.setAnalysisResult(result)
    }

    LaunchedEffect(saveSuccess) {
        if (saveSuccess != null) {
            onConfirmSuccess()
        }
    }

    if (saveSuccess != null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.Center
        ) {
            IncomingReturnSaveSuccessCard(
                recordId = saveSuccess!!
            )
        }

        return
    }

    IncomingReturnReviewPage(
        result = result,
        onBack = onBack,
        onConfirm = { currentResult ->

            viewModel.setAnalysisResult(
                currentResult
            )

            viewModel.confirmReturn()
        }
    )

    if (errorMessage != null) {
        IncomingReturnSaveErrorCard(
            message = errorMessage!!
        )
    }
}