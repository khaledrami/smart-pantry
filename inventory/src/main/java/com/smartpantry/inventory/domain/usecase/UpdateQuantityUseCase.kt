package com.smartpantry.inventory.domain.usecase

import com.smartpantry.inventory.domain.model.Movement
import com.smartpantry.inventory.domain.repository.ProductRepository

class UpdateQuantityUseCase @javax.inject.Inject constructor(
    private val repository: ProductRepository
) {
    operator suspend fun invoke(id: Long, newQuantity: Int): Movement = repository.updateQuantity(id, newQuantity)
}