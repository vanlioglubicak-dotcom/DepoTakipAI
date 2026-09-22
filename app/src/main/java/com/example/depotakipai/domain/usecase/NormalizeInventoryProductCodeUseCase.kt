package com.example.depotakipai.domain.usecase

class NormalizeInventoryProductCodeUseCase {

    operator fun invoke(
        productCode: String
    ): String? {

        val value = productCode
            .trim()
            .uppercase()

        if (value.isEmpty()) {
            return null
        }

        val compact = value
            .replace(" ", "")
            .replace("_", "-")

        val brands = listOf(
            "SNZ",
            "MTS",
            "FS"
        )

        for (brand in brands) {

            if (compact.startsWith(brand)) {

                val numericPart = compact
                    .removePrefix(brand)
                    .removePrefix("-")
                    .filter { it.isDigit() }

                if (numericPart.isEmpty()) {
                    return null
                }

                return "$brand-$numericPart"
            }
        }

        return null
    }
}