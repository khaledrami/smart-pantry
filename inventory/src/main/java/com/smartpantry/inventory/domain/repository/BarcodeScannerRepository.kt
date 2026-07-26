package com.smartpantry.inventory.domain.repository

import com.smartpantry.inventory.domain.model.BarcodeScanResult
import com.smartpantry.inventory.domain.model.ProductData

interface BarcodeScannerRepository {
    suspend fun scan(): Result<BarcodeScanResult>
    suspend fun lookupProduct(barcode: String): Result<ProductData>
}
