package com.smartpantry.inventory.domain.model

import java.time.LocalDate
import java.time.LocalDateTime

data class Product(
    val id: Long? = null,
    val name: String,
    val description: String = "",
    val brand: String = "",
    val category: Category,
    val barcode: String = "",
    val quantity: Int,
    val unit: String,
    val price: Double = 0.0,
    val purchaseDate: LocalDate? = null,
    val openDate: LocalDate? = null,
    val freezeDate: LocalDate? = null,
    val bestBeforeDate: LocalDate? = null,
    val expiryDate: LocalDate? = null,
    val location: String,
    val status: Status,
    val notes: String = "",
    val tags: List<String> = emptyList(),
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)