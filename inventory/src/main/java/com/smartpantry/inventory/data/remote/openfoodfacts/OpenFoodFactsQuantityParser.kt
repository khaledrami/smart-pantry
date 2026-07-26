package com.smartpantry.inventory.data.remote.openfoodfacts

object OpenFoodFactsQuantityParser {

    private val quantityRegex = Regex("""(?:(\d+(?:[.,]\d+)?)\s*x\s*)?(\d+(?:[.,]\d+)?)\s*(\w+)""")

    data class ParsedQuantity(
        val quantity: Int,
        val unit: String
    )

    fun parse(quantityString: String?): ParsedQuantity {
        if (quantityString.isNullOrBlank()) {
            return ParsedQuantity(1, "units")
        }

        val match = quantityRegex.find(quantityString.trim())
        return if (match != null) {
            val multiplier = match.groupValues[1].replace(",", ".").toDoubleOrNull() ?: 1.0
            val value = match.groupValues[2].replace(",", ".").toDoubleOrNull() ?: 1.0
            val rawUnit = match.groupValues[3].lowercase().trim()

            val unit = when (rawUnit) {
                "g", "gram", "grams", "gramos" -> "g"
                "kg", "kilogram", "kilograms", "kilos" -> "kg"
                "ml", "milliliter", "milliliters", "mililitros" -> "ml"
                "l", "liter", "liters", "litre", "litres", "litros" -> "L"
                "cl", "centiliter", "centiliters" -> "cl"
                "dl", "deciliter", "deciliters" -> "dl"
                "oz", "ounce", "ounces" -> "oz"
                "lb", "pound", "pounds" -> "lb"
                "unit", "units", "unidad", "unidades", "piece", "pieces", "portion", "portions" -> "units"
                "pack", "packs", "package", "packages", "paquete", "paquetes" -> "pack"
                "bottle", "bottles", "botella", "botellas" -> "bottle"
                "can", "cans", "lata", "latas" -> "can"
                "jar", "jars", "frasco", "frascos" -> "jar"
                "box", "boxes", "caja", "cajas" -> "box"
                "bag", "bags", "bolsa", "bolsas" -> "bag"
                else -> rawUnit
            }

            val totalQuantity = (value * multiplier).toInt()
            ParsedQuantity(quantity = if (totalQuantity > 0) totalQuantity else 1, unit = unit)
        } else {
            // Fallback: try to extract just a number
            val numberMatch = Regex("""(\d+)""").find(quantityString)
            if (numberMatch != null) {
                ParsedQuantity(
                    quantity = numberMatch.groupValues[1].toInt(),
                    unit = "units"
                )
            } else {
                ParsedQuantity(1, "units")
            }
        }
    }
}
