package com.smartpantry.inventory.data.remote.openfoodfacts

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OpenFoodFactsResponse(
    val code: String,
    val status: Int,
    @SerialName("status_verbose")
    val statusVerbose: String,
    val product: OpenFoodFactsProduct? = null
)

@Serializable
data class OpenFoodFactsProduct(
    @SerialName("product_name")
    val productName: String? = null,
    val brands: String? = null,
    val quantity: String? = null,
    val categories: String? = null,
    @SerialName("image_url")
    val imageUrl: String? = null,
    @SerialName("image_small_url")
    val imageSmallUrl: String? = null,
    @SerialName("image_front_url")
    val imageFrontUrl: String? = null,
    @SerialName("image_front_small_url")
    val imageFrontSmallUrl: String? = null
)
