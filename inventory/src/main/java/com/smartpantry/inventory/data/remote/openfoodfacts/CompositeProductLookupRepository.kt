package com.smartpantry.inventory.data.remote.openfoodfacts

import com.smartpantry.inventory.data.repository.MockProductLookupRepository
import com.smartpantry.inventory.domain.model.ProductData
import com.smartpantry.inventory.domain.repository.ProductLookupRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CompositeProductLookupRepository @Inject constructor(
    private val openFoodFactsRepository: OpenFoodFactsProductLookupRepository,
    private val mockRepository: MockProductLookupRepository
) : ProductLookupRepository {

    override suspend fun lookup(barcode: String): Result<ProductData> = withContext(Dispatchers.IO) {
        // Try OpenFoodFacts first (real API)
        val offResult = openFoodFactsRepository.lookup(barcode)

        if (offResult.isSuccess) {
            return@withContext offResult
        }

        // Fallback to mock database (offline / development)
        mockRepository.lookup(barcode)
    }
}
