package com.example.depotakipai.domain.usecase

class GetInventoryListStatusLabelUseCase {

    operator fun invoke(
        isApproved: Boolean
    ): String {
        return if (isApproved) {
            "Onaylandı"
        } else {
            "Taslak"
        }
    }
}