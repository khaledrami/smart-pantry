package com.smartpantry.inventory.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ProductData(
    val name: String,
    val brand: String,
    val category: Category,
    val defaultQuantity: Int,
    val unit: String,
    val imageUrl: String? = null
)