package com.example.depotakipai.domain.model

data class WarehouseRow(
    val id: Long = 0L,
    val rowNumber: Int,
    val name: String = "Sıra $rowNumber",
    val displayOrder: Int = rowNumber,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
) {
    companion object {

        const val TOTAL_ROWS = 17

        fun createDefaultRows(): List<WarehouseRow> {
            return (1..TOTAL_ROWS).map { number ->
                WarehouseRow(
                    id = number.toLong(),
                    rowNumber = number,
                    name = "Sıra %02d".format(number),
                    displayOrder = number,
                    isActive = true
                )
            }
        }
    }
}