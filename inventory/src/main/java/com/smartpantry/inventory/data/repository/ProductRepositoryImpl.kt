package com.smartpantry.inventory.data.repository

import com.smartpantry.inventory.domain.model.Movement
import com.smartpantry.inventory.domain.model.Product
import com.smartpantry.inventory.domain.model.Status
import com.smartpantry.inventory.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepositoryImpl @Inject constructor() : ProductRepository {

    private val products = mutableListOf<Product>()

    override fun getAllProducts(): Flow<List<Product>> = flowOf(products)

    override fun getProduct(id: Long): Flow<Product> = flowOf(products.firstOrNull { it.id == id }!!)

    override suspend fun addProduct(product: Product): Long {
        val newProduct = product.copy(id = products.size + 1L)
        products.add(newProduct)
        return newProduct.id!!
    }

    override suspend fun updateProduct(product: Product) {
        val index = products.indexOfFirst { it.id == product.id }
        if (index >= 0) products[index] = product
    }

    override suspend fun deleteProduct(id: Long) {
        products.removeAll { it.id == id }
    }

    override suspend fun moveProduct(id: Long, newLocation: String) {
        val index = products.indexOfFirst { it.id == id }
        if (index >= 0) {
            products[index] = products[index].copy(location = newLocation)
        }
    }

    override suspend fun updateQuantity(id: Long, newQuantity: Int): Movement {
        val index = products.indexOfFirst { it.id == id }
        val oldQuantity = products[index].quantity
        if (index >= 0) {
            products[index] = products[index].copy(quantity = newQuantity)
        }
        return Movement(productId = id, type = com.smartpantry.inventory.domain.model.MovementType.Correction(field = "quantity", oldValue = oldQuantity.toString(), newValue = newQuantity.toString()))
    }

    override suspend fun updateStatus(id: Long, newStatus: Status): Movement {
        val index = products.indexOfFirst { it.id == id }
        val oldStatus = products[index].status
        if (index >= 0) {
            products[index] = products[index].copy(status = newStatus)
        }
        return Movement(productId = id, type = com.smartpantry.inventory.domain.model.MovementType.Correction(field = "status", oldValue = oldStatus.name, newValue = newStatus.name))
    }
}