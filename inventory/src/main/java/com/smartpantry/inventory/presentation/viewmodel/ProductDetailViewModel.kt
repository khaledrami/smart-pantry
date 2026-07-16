package com.smartpantry.inventory.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartpantry.inventory.domain.model.Movement
import com.smartpantry.inventory.domain.model.Product
import com.smartpantry.inventory.domain.usecase.GetProductUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class ProductDetailViewModel @Inject constructor(
    private val getProductUseCase: GetProductUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProductDetailUiState>(ProductDetailUiState.Loading())
    val uiState: StateFlow<ProductDetailUiState> = _uiState

    fun loadProduct(productId: Long) {
        _uiState.value = ProductDetailUiState.Loading()
        viewModelScope.launch {
            val productFlow = getProductUseCase(productId)
            val movementsFlow = emptyFlow<List<Movement>>()

            combine(productFlow, movementsFlow) { product, movements ->
                ProductDetailUiState.Success(product, movements)
            }.distinctUntilChanged().collect { state ->
                _uiState.value = state
            }
        }
    }

    fun refresh(productId: Long) {
        loadProduct(productId)
    }
}