package com.smartpantry.inventory.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartpantry.inventory.domain.model.Category
import com.smartpantry.inventory.domain.model.Product
import com.smartpantry.inventory.domain.model.ProductData
import com.smartpantry.inventory.domain.model.Status
import com.smartpantry.inventory.domain.repository.ProductRepository
import com.smartpantry.inventory.domain.usecase.AddProductUseCase
import com.smartpantry.inventory.domain.usecase.LookupProductUseCase
import com.smartpantry.inventory.domain.usecase.UpdateProductUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditProductViewModel @Inject constructor(
    private val addProductUseCase: AddProductUseCase,
    private val updateProductUseCase: UpdateProductUseCase,
    private val lookupProductUseCase: LookupProductUseCase,
    private val productRepository: ProductRepository
) : ViewModel() {

    var editingProduct: Product? = null
        private set

    private val _uiState = MutableStateFlow<AddEditProductUiState>(
        AddEditProductUiState.Editing()
    )
    val uiState: StateFlow<AddEditProductUiState> = _uiState

    fun initializeForEdit(product: Product) {
        editingProduct = product
        _uiState.value = AddEditProductUiState.Editing(
            name = product.name,
            description = product.description,
            brand = product.brand,
            category = product.category,
            barcode = product.barcode,
            quantity = product.quantity,
            unit = product.unit,
            price = product.price,
            purchaseDate = product.purchaseDate?.toString() ?: "",
            openDate = product.openDate?.toString() ?: "",
            freezeDate = product.freezeDate?.toString() ?: "",
            bestBeforeDate = product.bestBeforeDate?.toString() ?: "",
            expiryDate = product.expiryDate?.toString() ?: "",
            location = product.location,
            status = product.status,
            notes = product.notes,
            tags = product.tags.joinToString(", ")
        )
    }

    fun initializeForNew(barcode: String? = null) {
        editingProduct = null
        _uiState.value = AddEditProductUiState.Editing(barcode = barcode ?: "")
    }

    fun loadProductForEdit(productId: Long) {
        viewModelScope.launch {
            try {
                val product = productRepository.getProduct(productId).first()
                initializeForEdit(product)
            } catch (e: Exception) {
                _uiState.value = AddEditProductUiState.Error("Failed to load product: ${e.message}")
            }
        }
    }

    fun updateName(name: String) {
        updateEditingState { it.copy(name = name, error = null) }
    }

    fun updateDescription(description: String) {
        updateEditingState { it.copy(description = description) }
    }

    fun updateBrand(brand: String) {
        updateEditingState { it.copy(brand = brand) }
    }

    fun updateCategory(category: Category) {
        updateEditingState { it.copy(category = category) }
    }

    fun updateBarcode(barcode: String) {
        updateEditingState { it.copy(barcode = barcode, error = null) }
    }

    fun updateQuantity(quantity: Int) {
        updateEditingState { it.copy(quantity = quantity) }
    }

    fun updateUnit(unit: String) {
        updateEditingState { it.copy(unit = unit) }
    }

    fun updatePrice(price: Double) {
        updateEditingState { it.copy(price = price) }
    }

    fun updatePurchaseDate(date: String) {
        updateEditingState { it.copy(purchaseDate = date) }
    }

    fun updateOpenDate(date: String) {
        updateEditingState { it.copy(openDate = date) }
    }

    fun updateFreezeDate(date: String) {
        updateEditingState { it.copy(freezeDate = date) }
    }

    fun updateBestBeforeDate(date: String) {
        updateEditingState { it.copy(bestBeforeDate = date) }
    }

    fun updateExpiryDate(date: String) {
        updateEditingState { it.copy(expiryDate = date) }
    }

    fun updateLocation(location: String) {
        updateEditingState { it.copy(location = location) }
    }

    fun updateStatus(status: Status) {
        updateEditingState { it.copy(status = status) }
    }

    fun updateNotes(notes: String) {
        updateEditingState { it.copy(notes = notes) }
    }

    fun updateTags(tags: String) {
        updateEditingState { it.copy(tags = tags) }
    }

    private fun updateEditingState(block: (AddEditProductUiState.Editing) -> AddEditProductUiState.Editing) {
        _uiState.value = when (val current = _uiState.value) {
            is AddEditProductUiState.Editing -> block(current)
            else -> current
        }
    }

    fun save() {
        _uiState.value = when (val current = _uiState.value) {
            is AddEditProductUiState.Editing -> {
                if (current.name.isBlank()) {
                    current.copy(error = "Name is required")
                } else if (current.location.isBlank()) {
                    current.copy(error = "Location is required")
                } else if (current.quantity <= 0) {
                    current.copy(error = "Quantity must be positive")
                } else {
                    current.copy(isSaving = true)
                }
            }
            else -> current
        }

        viewModelScope.launch {
            val current = _uiState.value
            if (current is AddEditProductUiState.Editing && current.isSaving) {
                val product = Product(
                    id = editingProduct?.id,
                    name = current.name,
                    description = current.description,
                    brand = current.brand,
                    category = current.category,
                    barcode = current.barcode,
                    quantity = current.quantity,
                    unit = current.unit,
                    price = current.price,
                    purchaseDate = current.purchaseDate.parseDate(),
                    openDate = current.openDate.parseDate(),
                    freezeDate = current.freezeDate.parseDate(),
                    bestBeforeDate = current.bestBeforeDate.parseDate(),
                    expiryDate = current.expiryDate.parseDate(),
                    location = current.location,
                    status = current.status,
                    notes = current.notes,
                    tags = current.tags.split(",").map { it.trim() }.filter { it.isNotBlank() }
                )

                try {
                    if (editingProduct != null) {
                        updateProductUseCase(product.copy(id = editingProduct?.id))
                    } else {
                        val id = addProductUseCase(product)
                    }
                    _uiState.value = AddEditProductUiState.Saved(product)
                } catch (e: Exception) {
                    _uiState.value = AddEditProductUiState.Error(e.message ?: "Failed to save")
                }
            }
        }
    }

    fun lookupBarcode(barcode: String) {
        viewModelScope.launch {
            val result = lookupProductUseCase(barcode)
            _uiState.value = if (result.isSuccess) {
                AddEditProductUiState.BarcodeLookupResult(result.getOrThrow())
            } else {
                AddEditProductUiState.BarcodeLookupError(result.exceptionOrNull()?.message ?: "Lookup failed")
            }
        }
    }

    fun applyBarcodeLookupResult(productData: ProductData) {
        _uiState.value = when (val current = _uiState.value) {
            is AddEditProductUiState.Editing -> current.copy(
                name = productData.name,
                brand = productData.brand,
                category = productData.category,
                quantity = productData.defaultQuantity,
                unit = productData.unit,
                error = null
            )
            else -> current
        }
    }
}

private fun String.parseDate(): java.time.LocalDate? {
    return try {
        if (isNotBlank()) java.time.LocalDate.parse(this) else null
    } catch (e: Exception) {
        null
    }
}