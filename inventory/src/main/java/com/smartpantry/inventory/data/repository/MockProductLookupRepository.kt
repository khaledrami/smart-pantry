package com.smartpantry.inventory.data.repository

import com.smartpantry.inventory.domain.model.ProductData
import com.smartpantry.inventory.domain.repository.ProductLookupRepository
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockProductLookupRepository @Inject constructor() : ProductLookupRepository {

    private val products = mapOf(
        "1234567890123" to ProductData(
            name = "Test Product",
            brand = "Test Brand",
            category = com.smartpantry.inventory.domain.model.Category.OTHER,
            defaultQuantity = 1,
            unit = "pcs"
        )
    )

    override suspend fun lookup(barcode: String): Result<ProductData> {
        return products[barcode]?.let { Result.success(it) } 
            ?: Result.failure(Exception("Product not found"))
    }
}