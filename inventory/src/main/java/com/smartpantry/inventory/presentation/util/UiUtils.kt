package com.smartpantry.inventory.presentation.util

import androidx.compose.ui.graphics.Color
import java.time.LocalDate
import java.time.temporal.ChronoUnit

fun getCategoryColor(category: String): Color {
    return when (category.lowercase()) {
        "carnes", "meat" -> Color(0xFFE53935)
        "pescados", "fish" -> Color(0xFF1E88E5)
        "verduras", "vegetables" -> Color(0xFF43A047)
        "frutas", "fruits" -> Color(0xFFFF9800)
        "lácteos", "dairy" -> Color(0xFF8E24AA)
        "congelados", "frozen" -> Color(0xFF00ACC1)
        "bebidas", "beverages" -> Color(0xFFD81B60)
        "conservas", "canned" -> Color(0xFF546E7A)
        "legumbres", "legumes" -> Color(0xFF7CB342)
        "pasta", "pasta" -> Color(0xFFF4511E)
        "arroz", "rice" -> Color(0xFFFB8C00)
        "especias", "spices" -> Color(0xFF6D4C41)
        "pan", "bread" -> Color(0xFF5D4037)
        "salsas", "sauces" -> Color(0xFFAD1457)
        "snacks", "snacks" -> Color(0xFFE65100)
        else -> Color(0xFF757575)
    }
}

fun getStatusColor(status: String): Color {
    return when (status.uppercase()) {
        "AVAILABLE" -> Color(0xFF4CAF50)  // green
        "OPENED" -> Color(0xFF2196F3)    // blue
        "FROZEN" -> Color(0xFF00BCD4)    // cyan
        "CONSUMED" -> Color(0xFF757575)  // grey
        "EXPIRED" -> Color(0xFFF44336)   // red
        "DONATED" -> Color(0xFF9E9E9E)   // grey
        "DISCARDED" -> Color(0xFFF44336) // red
        else -> Color(0xFF757575)        // grey
    }
}

fun calculateDaysLeft(expiryDate: LocalDate): Int {
    val today = LocalDate.now()
    return if (expiryDate.isBefore(today)) {
        0
    } else {
        ChronoUnit.DAYS.between(today, expiryDate).toInt()
    }
}