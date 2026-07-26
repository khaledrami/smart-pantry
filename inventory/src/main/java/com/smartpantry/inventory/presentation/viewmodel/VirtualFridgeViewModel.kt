package com.smartpantry.inventory.presentation.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartpantry.inventory.domain.model.Product
import com.smartpantry.inventory.domain.usecase.GetProductsUseCase
import com.smartpantry.inventory.presentation.util.translateLocation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VirtualFridgeViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<VirtualFridgeUiState>(VirtualFridgeUiState.Loading)
    val uiState: StateFlow<VirtualFridgeUiState> = _uiState

    init {
        loadProducts()
    }

    fun loadProducts() {
        viewModelScope.launch {
            try {
                getProductsUseCase().collect { products ->
                    _uiState.value = VirtualFridgeUiState.Success(
                        zones = buildZones(products)
                    )
                }
            } catch (e: Exception) {
                _uiState.value = VirtualFridgeUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    private fun buildZones(products: List<Product>): List<StorageZone> {
        val grouped = products.groupBy { it.location }

        val zoneOrder = listOf("Freezer", "Fridge", "Pantry")
        val zoneColors = mapOf(
            "Freezer" to Color(0xFF80DEEA),
            "Fridge" to Color(0xFFA5D6A7),
            "Pantry" to Color(0xFFFFCC80)
        )

        return zoneOrder.map { zoneType ->
            val zoneProducts = grouped.filter { (location, _) ->
                location.startsWith(zoneType)
            }

            val compartments = zoneProducts.map { (location, prods) ->
                Compartment(
                    path = location,
                    displayName = translateLocation(location).split(" / ").drop(1).joinToString(" / "),
                    products = prods.sortedBy { it.expiryDate }
                )
            }.sortedBy { it.displayName }

            StorageZone(
                type = zoneType,
                displayName = when (zoneType) {
                    "Freezer" -> "Congelador"
                    "Fridge" -> "Nevera"
                    "Pantry" -> "Despensa"
                    else -> zoneType
                },
                color = zoneColors[zoneType] ?: Color.Gray,
                compartments = compartments
            )
        }.filter { it.compartments.isNotEmpty() }
    }
}
