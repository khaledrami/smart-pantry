package com.smartpantry.inventory.domain.repository

import com.smartpantry.inventory.domain.model.ProductData

interface ProductLookupRepository {
    suspend fun lookup(barcode: String): Result<ProductData>
}