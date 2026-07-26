package com.smartpantry.inventory.data.remote.openfoodfacts

import com.smartpantry.inventory.domain.model.Category

object OpenFoodFactsCategoryMapper {

    private val categoryKeywords = mapOf(
        Category.MEAT to listOf("meat", "carnes", "beef", "pork", "chicken", "ham", "sausage", "bacon"),
        Category.FISH to listOf("fish", "pescado", "seafood", "salmon", "tuna", "shrimp"),
        Category.VEGETABLES to listOf("vegetables", "verduras", "vegetable", "greens", "salad", "lettuce", "tomato", "carrot", "onion", "potato", "pepper"),
        Category.FRUITS to listOf("fruits", "frutas", "fruit", "apple", "banana", "orange", "strawberry", "grape", "pear"),
        Category.DAIRY to listOf("dairy", "lacteos", "milk", "cheese", "yogurt", "yoghurt", "butter", "cream", "queso", "leche"),
        Category.FROZEN to listOf("frozen", "congelados", "ice cream", "gelato", "surgele"),
        Category.BEVERAGES to listOf("beverages", "bebidas", "drinks", "water", "juice", "soda", "soft drink", "tea", "coffee", "beer", "wine", "alcohol"),
        Category.CANNED to listOf("canned", "conservas", "tin", "jar", "preserve"),
        Category.LEGUMES to listOf("legumes", "legumbres", "beans", "lentils", "chickpeas", "peas", "lentejas", "garbanzos", "judias"),
        Category.PASTA to listOf("pasta", "noodles", "macaroni", "spaghetti", "fideos"),
        Category.RICE to listOf("rice", "arroz", "risotto"),
        Category.SPICES to listOf("spices", "especias", "herbs", "herb", "seasoning", "salt", "pepper", "cinnamon", "paprika", "oregano", "basil"),
        Category.BREAD to listOf("bread", "pan", "bakery", "buns", "rolls", "croissant", "baguette", "toast"),
        Category.SAUCES to listOf("sauces", "salsas", "sauce", "condiments", "condimento", "dressing", "ketchup", "mayonnaise", "mustard", "salsa", "tomate frito"),
        Category.SNACKS to listOf("snacks", "snack", "chips", "crisps", "nuts", "popcorn", "cookies", "biscuits", "chocolate", "candy", "sweets")
    )

    fun map(categoriesString: String?): Category {
        if (categoriesString.isNullOrBlank()) return Category.OTHER

        val lowerCategories = categoriesString.lowercase()

        return categoryKeywords.entries.firstOrNull { (_, keywords) ->
            keywords.any { keyword -> lowerCategories.contains(keyword) }
        }?.key ?: Category.OTHER
    }
}
