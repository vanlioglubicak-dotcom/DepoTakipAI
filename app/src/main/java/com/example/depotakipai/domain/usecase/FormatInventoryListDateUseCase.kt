package com.example.depotakipai.domain.usecase

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FormatInventoryListDateUseCase {

    operator fun invoke(
        timestamp: Long
    ): String {
        val formatter = SimpleDateFormat(
            "dd MMMM yyyy — HH:mm",
            Locale("tr", "TR")
        )

        return formatter.format(Date(timestamp))
    }
}