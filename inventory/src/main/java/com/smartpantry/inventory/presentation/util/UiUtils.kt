package com.smartpantry.inventory.presentation.util

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.smartpantry.inventory.domain.model.Category
import com.smartpantry.inventory.domain.model.Status
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Composable
fun getCategoryColor(category: Category): Color {
    return when (category) {
        Category.MEAT -> Color(0xFFEF5350)
        Category.FISH -> Color(0xFF42A5F5)
        Category.VEGETABLES -> Color(0xFF66BB6A)
        Category.FRUITS -> Color(0xFFFFA726)
        Category.DAIRY -> Color(0xFFB39DDB)
        Category.FROZEN -> Color(0xFF26C6DA)
        Category.BEVERAGES -> Color(0xFFEC407A)
        Category.CANNED -> Color(0xFF9575CD)
        Category.LEGUMES -> Color(0xFF78909C)
        Category.PASTA -> Color(0xFFFFB74D)
        Category.RICE -> Color(0xFFD4E157)
        Category.SPICES -> Color(0xFF8D6E63)
        Category.BREAD -> Color(0xFFF06292)
        Category.SAUCES -> Color(0xFF4DB6AC)
        Category.SNACKS -> Color(0xFFFF8A65)
        Category.OTHER -> MaterialTheme.colorScheme.primary
    }
}

fun getStatusColor(status: Status): Color {
    return when (status) {
        Status.AVAILABLE -> Color(0xFF4CAF50)
        Status.OPENED -> Color(0xFFFF9800)
        Status.FROZEN -> Color(0xFF2196F3)
        Status.CONSUMED -> Color(0xFF9E9E9E)
        Status.EXPIRED -> Color(0xFFF44336)
        Status.DONATED -> Color(0xFF9C27B0)
        Status.DISCARDED -> Color(0xFF795548)
    }
}

fun calculateDaysLeft(expiryDate: LocalDate): Long {
    val today = LocalDate.now()
    return ChronoUnit.DAYS.between(today, expiryDate)
}
