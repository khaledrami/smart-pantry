package com.smartpantry.inventory.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartpantry.inventory.domain.model.Movement
import com.smartpantry.inventory.domain.model.Product
import com.smartpantry.inventory.domain.usecase.GetProductUseCase
import com.smartpantry.inventory.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val getProductUseCase: GetProductUseCase,
    private val repository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProductDetailUiState>(ProductDetailUiState.Loading())
    val uiState: StateFlow<ProductDetailUiState> = _uiState

    fun loadProduct(productId: Long) {
        _uiState.value = ProductDetailUiState.Loading()
        viewModelScope.launch {
            val productFlow = getProductUseCase(productId)
            val movementsFlow = repository.getAllProducts()
                .map { products -> products.firstOrNull { it.id == productId } }
                .flatMapLatest {
                    flowOf(emptyList<Movement>())
                }

            combine(productFlow, movementsFlow) { product, movements ->
                ProductDetailUiState.Success(product, movements)
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun refresh(productId: Long) {
        loadProduct(productId)
    }
}
