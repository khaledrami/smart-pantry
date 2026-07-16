package com.smartpantry.inventory.data.repository

import com.smartpantry.inventory.domain.model.ProductData
import com.smartpantry.inventory.domain.repository.BarcodeScannerRepository
import com.smartpantry.inventory.domain.repository.BarcodeScanResult
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BarcodeScannerRepositoryImpl @Inject constructor() : BarcodeScannerRepository {

    private var pendingScan: CompletableDeferred<Result<BarcodeScanResult>>? = null

    override suspend fun scan(): Result<BarcodeScanResult> {
        val deferred = CompletableDeferred<Result<BarcodeScanResult>>()
        pendingScan = deferred
        return deferred.await()
    }

    override suspend fun lookupProduct(barcode: String): Result<ProductData> {
        // Delegate to the mock lookup repository
        return Result.failure(Exception("lookupProduct not implemented in BarcodeScannerRepositoryImpl"))
    }

    fun simulateScan(barcode: String, format: String) {
        pendingScan?.complete(Result.success(BarcodeScanResult(barcode = barcode, format = format)))
        pendingScan = null
    }

    fun simulateError(throwable: Throwable) {
        pendingScan?.complete(Result.failure(throwable))
        pendingScan = null
    }
}