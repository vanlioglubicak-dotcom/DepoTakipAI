package com.example.depotakipai.domain.usecase

class CreateInventoryListTimestampUseCase {

    operator fun invoke(): Long {
        return System.currentTimeMillis()
    }
}