package com.smartpantry.inventory.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class Category(val displayName: String) {
    MEAT("Carnes"),
    FISH("Pescados"),
    VEGETABLES("Verduras"),
    FRUITS("Frutas"),
    DAIRY("Lácteos"),
    FROZEN("Congelados"),
    BEVERAGES("Bebidas"),
    CANNED("Conservas"),
    LEGUMES("Legumbres"),
    PASTA("Pasta"),
    RICE("Arroz"),
    SPICES("Especias"),
    BREAD("Pan"),
    SAUCES("Salsas"),
    SNACKS("Snacks"),
    OTHER("Otros")
}