package com.smartpantry.inventory.domain.usecase

import com.smartpantry.inventory.domain.model.Movement
import com.smartpantry.inventory.domain.model.Product
import com.smartpantry.inventory.domain.repository.ProductRepository

class UpdateProductUseCase @javax.inject.Inject constructor(
    private val repository: ProductRepository
) {
    operator suspend fun invoke(product: Product) = repository.updateProduct(product)
}