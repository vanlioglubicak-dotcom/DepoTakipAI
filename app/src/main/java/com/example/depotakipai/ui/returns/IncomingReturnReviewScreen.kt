package com.example.depotakipai.ui.returns

import androidx.compose.runtime.Composable
import com.example.depotakipai.domain.model.IncomingReturnAnalysisResult

@Composable
fun IncomingReturnReviewScreen(
    result: IncomingReturnAnalysisResult,
    onBack: () -> Unit = {},
    onEdit: () -> Unit = {},
    onConfirm: () -> Unit = {}
) {
    IncomingReturnReviewPage(
        result = result,
        onBack = onBack,
        onConfirm = {
            onConfirm()
        }
    )
}