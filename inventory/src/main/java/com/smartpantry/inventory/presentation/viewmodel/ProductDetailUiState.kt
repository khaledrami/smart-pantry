package com.smartpantry.inventory.presentation.viewmodel

import com.smartpantry.inventory.domain.model.Movement
import com.smartpantry.inventory.domain.model.Product

sealed interface ProductDetailUiState {
    data class Loading(val isRefreshing: Boolean = false) : ProductDetailUiState
    data class Success(val product: Product, val movements: List<Movement>) : ProductDetailUiState
    data class Error(val message: String) : ProductDetailUiState
}