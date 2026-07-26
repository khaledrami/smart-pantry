package com.smartpantry.inventory.presentation.util

fun translateLocation(location: String): String {
    if (location.isBlank()) return location
    return location.split("/").joinToString(" / ") { segment ->
        when (segment.trim()) {
            "Freezer" -> "Congelador"
            "Fridge" -> "Nevera"
            "Pantry" -> "Despensa"
            "Upper Drawer" -> "Cajón Superior"
            "Middle Drawer" -> "Cajón Medio"
            "Lower Drawer" -> "Cajón Inferior"
            "Door" -> "Puerta"
            "Top" -> "Arriba"
            "Bottom" -> "Abajo"
            "Upper Shelf" -> "Estante Superior"
            "Middle Shelf" -> "Estante Medio"
            "Lower Shelf" -> "Estante Inferior"
            "Left" -> "Izquierda"
            "Right" -> "Derecha"
            "Veggie Drawer" -> "Cajón Verduras"
            "Top Shelf" -> "Estante Superior"
            "Basket" -> "Cesta"
            else -> segment
        }
    }
}
