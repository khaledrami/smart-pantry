package com.smartpantry.inventory.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.smartpantry.inventory.domain.model.Category
import com.smartpantry.inventory.domain.model.Status

@Entity(
    tableName = "products",
    indices = [
        Index(value = ["location"]),
        Index(value = ["expiryDate"]),
        Index(value = ["category"]),
        Index(value = ["status"]),
        Index(value = ["barcode"])
    ]
)
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String = "",
    val brand: String = "",
    val category: Category,
    val barcode: String = "",
    val quantity: Int,
    val unit: String,
    val price: Double = 0.0,
    val purchaseDate: Long? = null,
    val openDate: Long? = null,
    val freezeDate: Long? = null,
    val bestBeforeDate: Long? = null,
    val expiryDate: Long? = null,
    val location: String,
    val status: Status,
    val notes: String = "",
    val tags: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)