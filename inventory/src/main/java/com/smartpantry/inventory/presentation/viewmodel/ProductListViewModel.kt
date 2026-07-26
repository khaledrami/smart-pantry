package com.smartpantry.inventory.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartpantry.inventory.domain.model.Product
import com.smartpantry.inventory.domain.usecase.GetProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProductListUiState>(ProductListUiState.Loading())
    val uiState: StateFlow<ProductListUiState> = _uiState

    init {
        loadProducts()
    }

    fun loadProducts(refresh: Boolean = false) {
        if (refresh) {
            _uiState.value = ProductListUiState.Loading(isRefreshing = true)
        }
        viewModelScope.launch {
            getProductsUseCase().collect { products ->
                if (products.isEmpty()) {
                    _uiState.value = ProductListUiState.Empty
                } else {
                    val grouped = products.groupBy { it.location }
                    _uiState.value = ProductListUiState.Success(products, grouped)
                }
            }
        }
    }

    fun refresh() {
        loadProducts(refresh = true)
    }
}