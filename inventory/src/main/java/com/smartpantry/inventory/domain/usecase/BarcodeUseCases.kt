package com.smartpantry.inventory.domain.usecase

import com.smartpantry.inventory.domain.model.ProductData
import com.smartpantry.inventory.domain.repository.BarcodeScanResult
import com.smartpantry.inventory.domain.repository.BarcodeScannerRepository
import com.smartpantry.inventory.domain.repository.ProductLookupRepository

class ScanBarcodeUseCase @javax.inject.Inject constructor(
    private val scannerRepository: BarcodeScannerRepository
) {
    operator suspend fun invoke() = scannerRepository.scan()
}

class LookupProductUseCase @javax.inject.Inject constructor(
    private val lookupRepository: ProductLookupRepository
) {
    operator suspend fun invoke(barcode: String) = lookupRepository.lookup(barcode)
}