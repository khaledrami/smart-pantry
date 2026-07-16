package com.smartpantry.inventory.presentation.viewmodel

import com.smartpantry.inventory.domain.model.Category
import com.smartpantry.inventory.domain.model.Product
import com.smartpantry.inventory.domain.model.ProductData
import com.smartpantry.inventory.domain.model.Status
import com.smartpantry.inventory.domain.repository.ProductLookupRepository
import com.smartpantry.inventory.domain.usecase.AddProductUseCase
import com.smartpantry.inventory.domain.usecase.LookupProductUseCase
import com.smartpantry.inventory.domain.usecase.UpdateProductUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class AddEditProductViewModelTest {

    @Test
    fun `initializeForNew sets empty editing state`() = runTest {
        val addUseCase = mockk<AddProductUseCase>()
        val updateUseCase = mockk<UpdateProductUseCase>()
        val lookupUseCase = mockk<LookupProductUseCase>()

        val viewModel = AddEditProductViewModel(addUseCase, updateUseCase, lookupUseCase)
        viewModel.initializeForNew()

        val state = viewModel.uiState.first()
        assertTrue(state is AddEditProductUiState.Editing)
        val editing = state as AddEditProductUiState.Editing
        assertEquals("", editing.name)
        assertEquals(Category.OTHER, editing.category)
        assertEquals(Status.AVAILABLE, editing.status)
        assertEquals(1, editing.quantity)
        assertEquals("units", editing.unit)
    }

    @Test
    fun `initializeForNew with barcode sets barcode field`() = runTest {
        val addUseCase = mockk<AddProductUseCase>()
        val updateUseCase = mockk<UpdateProductUseCase>()
        val lookupUseCase = mockk<LookupProductUseCase>()

        val viewModel = AddEditProductViewModel(addUseCase, updateUseCase, lookupUseCase)
        viewModel.initializeForNew(barcode = "123456789")

        val state = viewModel.uiState.first()
        assertTrue(state is AddEditProductUiState.Editing)
        val editing = state as AddEditProductUiState.Editing
        assertEquals("123456789", editing.barcode)
    }

    @Test
    fun `initializeForEdit populates fields from product`() = runTest {
        val addUseCase = mockk<AddProductUseCase>()
        val updateUseCase = mockk<UpdateProductUseCase>()
        val lookupUseCase = mockk<LookupProductUseCase>()
        val product = Product(
            id = 1,
            name = "Tomato",
            description = "Fresh tomatoes",
            brand = "Local",
            category = Category.VEGETABLES.name,
            barcode = "123456789",
            quantity = 5,
            unit = "kg",
            price = 2.99,
            location = "Fridge/Vegetables/Drawer",
            status = Status.AVAILABLE.name,
            notes = "Organic",
            tags = listOf("fresh", "organic")
        )

        val viewModel = AddEditProductViewModel(addUseCase, updateUseCase, lookupUseCase)
        viewModel.initializeForEdit(product)

        val state = viewModel.uiState.first()
        assertTrue(state is AddEditProductUiState.Editing)
        val editing = state as AddEditProductUiState.Editing
        assertEquals("Tomato", editing.name)
        assertEquals("Fresh tomatoes", editing.description)
        assertEquals("Local", editing.brand)
        assertEquals(Category.VEGETABLES, editing.category)
        assertEquals("123456789", editing.barcode)
        assertEquals(5, editing.quantity)
        assertEquals("kg", editing.unit)
        assertEquals(2.99, editing.price)
        assertEquals("Fridge/Vegetables/Drawer", editing.location)
        assertEquals(Status.AVAILABLE, editing.status)
        assertEquals("Organic", editing.notes)
        assertEquals("fresh, organic", editing.tags)
    }

    @Test
    fun `save validates required fields`() = runTest {
        val addUseCase = mockk<AddProductUseCase>()
        val updateUseCase = mockk<UpdateProductUseCase>()
        val lookupUseCase = mockk<LookupProductUseCase>()

        val viewModel = AddEditProductViewModel(addUseCase, updateUseCase, lookupUseCase)
        viewModel.initializeForNew()
        viewModel.updateName("")  // Empty name
        viewModel.updateLocation("")  // Empty location
        viewModel.updateQuantity(0)  // Invalid quantity
        viewModel.save()

        val state = viewModel.uiState.first()
        assertTrue(state is AddEditProductUiState.Editing)
        val editing = state as AddEditProductUiState.Editing
        assertTrue(editing.error != null)
        assertTrue(editing.error!!.contains("required") || editing.error!!.contains("positive"))
    }

    @Test
    fun `save calls addProductUseCase for new product`() = runTest {
        val addUseCase = mockk<AddProductUseCase>()
        val updateUseCase = mockk<UpdateProductUseCase>()
        val lookupUseCase = mockk<LookupProductUseCase>()
        coEvery { addUseCase(any()) } returns 1L

        val viewModel = AddEditProductViewModel(addUseCase, updateUseCase, lookupUseCase)
        viewModel.initializeForNew()
        viewModel.updateName("Tomato")
        viewModel.updateLocation("Fridge/Vegetables/Drawer")
        viewModel.updateQuantity(5)
        viewModel.save()

        val state = viewModel.uiState.first()
        assertTrue(state is AddEditProductUiState.Saved)
    }

    @Test
    fun `save calls updateProductUseCase for existing product`() = runTest {
        val addUseCase = mockk<AddProductUseCase>()
        val updateUseCase = mockk<UpdateProductUseCase>()
        val lookupUseCase = mockk<LookupProductUseCase>()
        val product = Product(id = 1, name = "Tomato", category = Category.VEGETABLES.name, quantity = 5, unit = "kg", location = "Fridge/Vegetables/Drawer", status = Status.AVAILABLE.name)

        val viewModel = AddEditProductViewModel(addUseCase, updateUseCase, lookupUseCase)
        viewModel.initializeForEdit(product)
        viewModel.updateName("Tomato Updated")
        viewModel.save()

        val state = viewModel.uiState.first()
        assertTrue(state is AddEditProductUiState.Saved)
    }

    @Test
    fun `lookupBarcode emits BarcodeLookupResult on success`() = runTest {
        val addUseCase = mockk<AddProductUseCase>()
        val updateUseCase = mockk<UpdateProductUseCase>()
        val lookupUseCase = mockk<LookupProductUseCase>()
        val productData = ProductData(name = "Tomate Frito", brand = "Orlando", category = Category.SAUCES, defaultQuantity = 350, unit = "g")
        coEvery { lookupUseCase("123456789") } returns Result.success(productData)

        val viewModel = AddEditProductViewModel(addUseCase, updateUseCase, lookupUseCase)
        viewModel.initializeForNew()
        viewModel.lookupBarcode("123456789")

        val state = viewModel.uiState.first()
        assertTrue(state is AddEditProductUiState.BarcodeLookupResult)
        val result = state as AddEditProductUiState.BarcodeLookupResult
        assertEquals("Tomate Frito", result.productData.name)
        assertEquals("Orlando", result.productData.brand)
    }

    @Test
    fun `lookupBarcode emits BarcodeLookupError on failure`() = runTest {
        val addUseCase = mockk<AddProductUseCase>()
        val updateUseCase = mockk<UpdateProductUseCase>()
        val lookupUseCase = mockk<LookupProductUseCase>()
        coEvery { lookupUseCase("123456789") } returns Result.failure(IllegalStateException("Not found"))

        val viewModel = AddEditProductViewModel(addUseCase, updateUseCase, lookupUseCase)
        viewModel.initializeForNew()
        viewModel.lookupBarcode("123456789")

        val state = viewModel.uiState.first()
        assertTrue(state is AddEditProductUiState.BarcodeLookupError)
        val error = state as AddEditProductUiState.BarcodeLookupError
        assertTrue(error.message.contains("Not found"))
    }

    @Test
    fun `applyBarcodeLookupResult populates fields`() = runTest {
        val addUseCase = mockk<AddProductUseCase>()
        val updateUseCase = mockk<UpdateProductUseCase>()
        val lookupUseCase = mockk<LookupProductUseCase>()
        val productData = ProductData(name = "Tomate Frito", brand = "Orlando", category = Category.SAUCES, defaultQuantity = 350, unit = "g")

        val viewModel = AddEditProductViewModel(addUseCase, updateUseCase, lookupUseCase)
        viewModel.initializeForNew()
        viewModel.applyBarcodeLookupResult(productData)

        val state = viewModel.uiState.first()
        assertTrue(state is AddEditProductUiState.Editing)
        val editing = state as AddEditProductUiState.Editing
        assertEquals("Tomate Frito", editing.name)
        assertEquals("Orlando", editing.brand)
        assertEquals(Category.SAUCES, editing.category)
        assertEquals(350, editing.quantity)
        assertEquals("g", editing.unit)
    }
}