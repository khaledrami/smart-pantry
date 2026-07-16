package com.smartpantry.inventory.domain.repository

import com.smartpantry.inventory.domain.model.ProductData
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable

interface BarcodeScannerRepository {
    suspend fun scan(): Result<BarcodeScanResult>
    suspend fun lookupProduct(barcode: String): Result<ProductData>
}

@Serializable
data class BarcodeScanResult(
    val barcode: String,
    val format: String
)