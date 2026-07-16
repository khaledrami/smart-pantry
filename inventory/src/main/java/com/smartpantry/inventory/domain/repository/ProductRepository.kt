package com.smartpantry.inventory.domain.repository

import com.smartpantry.inventory.domain.model.Movement
import com.smartpantry.inventory.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun getAllProducts(): Flow<List<Product>>
    fun getProduct(id: Long): Flow<Product>
    suspend fun addProduct(product: Product): Long
    suspend fun updateProduct(product: Product)
    suspend fun deleteProduct(id: Long)
    suspend fun moveProduct(id: Long, newLocation: String)
    suspend fun updateQuantity(id: Long, newQuantity: Int): Movement
    suspend fun updateStatus(id: Long, newStatus: com.smartpantry.inventory.domain.model.Status): Movement
}