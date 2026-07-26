package com.smartpantry.inventory.presentation.viewmodel

import com.smartpantry.inventory.domain.model.Product

sealed interface VirtualFridgeUiState {
    object Loading : VirtualFridgeUiState
    data class Success(
        val zones: List<StorageZone>
    ) : VirtualFridgeUiState
    data class Error(val message: String) : VirtualFridgeUiState
}

data class StorageZone(
    val type: String,
    val displayName: String,
    val color: androidx.compose.ui.graphics.Color,
    val compartments: List<Compartment>
)

data class Compartment(
    val path: String,
    val displayName: String,
    val products: List<Product>
)
