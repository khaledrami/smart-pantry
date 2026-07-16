package com.smartpantry.inventory.domain.usecase

import com.smartpantry.inventory.domain.repository.ProductRepository

class MoveProductUseCase @javax.inject.Inject constructor(
    private val repository: ProductRepository
) {
    operator suspend fun invoke(id: Long, newLocation: String) = repository.moveProduct(id, newLocation)
}