package com.smartpantry.inventory.presentation.viewmodel

import com.smartpantry.inventory.domain.model.Product

sealed interface ProductListUiState {
    data class Loading(val isRefreshing: Boolean = false) : ProductListUiState
    data class Success(
        val products: List<Product>,
        val groupedByLocation: Map<String, List<Product>>
    ) : ProductListUiState
    data class Error(val message: String) : ProductListUiState
    object Empty : ProductListUiState
}