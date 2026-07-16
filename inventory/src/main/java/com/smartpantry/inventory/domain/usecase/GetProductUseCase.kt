package com.smartpantry.inventory.domain.usecase

import com.smartpantry.inventory.domain.model.Product
import com.smartpantry.inventory.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow

class GetProductUseCase @javax.inject.Inject constructor(
    private val repository: ProductRepository
) {
    operator suspend fun invoke(id: Long): Flow<Product> = repository.getProduct(id)
}