package com.smartpantry.inventory.data.remote.openfoodfacts

import com.smartpantry.inventory.domain.model.Category
import com.smartpantry.inventory.domain.model.ProductData
import com.smartpantry.inventory.domain.repository.ProductLookupRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OpenFoodFactsProductLookupRepository @Inject constructor(
    private val api: OpenFoodFactsApi
) : ProductLookupRepository {

    override suspend fun lookup(barcode: String): Result<ProductData> = withContext(Dispatchers.IO) {
        try {
            val response = api.getProduct(barcode)

            if (response.status != 1 || response.product == null) {
                return@withContext Result.failure(
                    IllegalArgumentException("Product not found for barcode: $barcode")
                )
            }

            val product = response.product
            val parsedQuantity = OpenFoodFactsQuantityParser.parse(product.quantity)
            val category = OpenFoodFactsCategoryMapper.map(product.categories)

            val productData = ProductData(
                name = product.productName?.trim()?.takeIf { it.isNotBlank() } ?: "Unknown product",
                brand = product.brands?.split(",")?.firstOrNull()?.trim()?.takeIf { it.isNotBlank() } ?: "",
                category = category,
                defaultQuantity = parsedQuantity.quantity,
                unit = parsedQuantity.unit,
                imageUrl = product.imageFrontUrl
                    ?: product.imageFrontSmallUrl
                    ?: product.imageUrl
                    ?: product.imageSmallUrl
            )

            Result.success(productData)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
