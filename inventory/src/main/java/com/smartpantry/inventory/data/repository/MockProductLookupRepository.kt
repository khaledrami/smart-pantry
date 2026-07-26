package com.smartpantry.inventory.data.repository

import android.content.Context
import com.smartpantry.inventory.domain.model.Category
import com.smartpantry.inventory.domain.model.ProductData
import com.smartpantry.inventory.domain.repository.ProductLookupRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockProductLookupRepository @Inject constructor(
    @ApplicationContext private val context: Context
) : ProductLookupRepository {

    private val productCache: ConcurrentHashMap<String, ProductData> = ConcurrentHashMap()
    private var isLoaded = false
    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun lookup(barcode: String): Result<ProductData> = withContext(Dispatchers.IO) {
        loadIfNeeded()
        productCache[barcode]?.let { Result.success(it) }
            ?: Result.failure(IllegalArgumentException("Product not found for barcode: $barcode"))
    }

    private fun loadIfNeeded() {
        if (isLoaded) return
        synchronized(this) {
            if (isLoaded) return
            try {
                val inputStream = context.assets.open("mock_products.json")
                val jsonString = inputStream.bufferedReader().use { it.readText() }
                val map: Map<String, ProductData> = json.decodeFromString(jsonString)
                map.forEach { (barcode, data) ->
                    productCache[barcode] = data.copy(
                        category = Category.valueOf(data.category.name)
                    )
                }
                isLoaded = true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
