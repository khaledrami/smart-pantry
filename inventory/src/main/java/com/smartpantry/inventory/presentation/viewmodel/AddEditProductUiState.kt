package com.smartpantry.inventory.presentation.viewmodel

import com.smartpantry.inventory.domain.model.Category
import com.smartpantry.inventory.domain.model.Product
import com.smartpantry.inventory.domain.model.ProductData
import com.smartpantry.inventory.domain.model.Status

sealed interface AddEditProductUiState {
    data class Editing(
        val name: String = "",
        val description: String = "",
        val brand: String = "",
        val category: Category = Category.OTHER,
        val barcode: String = "",
        val quantity: Int = 1,
        val unit: String = "units",
        val price: Double = 0.0,
        val purchaseDate: String = "",
        val openDate: String = "",
        val freezeDate: String = "",
        val bestBeforeDate: String = "",
        val expiryDate: String = "",
        val location: String = "",
        val status: Status = Status.AVAILABLE,
        val notes: String = "",
        val tags: String = "",
        val photoUri: String? = null,
        val isSaving: Boolean = false,
        val error: String? = null
    ) : AddEditProductUiState

    object Saving : AddEditProductUiState
    data class Saved(val product: Product) : AddEditProductUiState
    data class Error(val message: String) : AddEditProductUiState
    data class BarcodeLookupResult(val productData: ProductData) : AddEditProductUiState
    data class BarcodeLookupError(val message: String) : AddEditProductUiState
}