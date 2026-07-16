package com.smartpantry.inventory.domain.usecase

import com.smartpantry.inventory.domain.model.Movement
import com.smartpantry.inventory.domain.model.Product
import com.smartpantry.inventory.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow

class GetProductsUseCase @javax.inject.Inject constructor(
    private val repository: ProductRepository
) {
    operator suspend fun invoke(): Flow<List<Product>> = repository.getAllProducts()
}