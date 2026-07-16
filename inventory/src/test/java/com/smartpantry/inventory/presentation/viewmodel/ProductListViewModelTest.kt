package com.smartpantry.inventory.presentation.viewmodel

import com.smartpantry.inventory.domain.model.Category
import com.smartpantry.inventory.domain.model.Product
import com.smartpantry.inventory.domain.model.Status
import com.smartpantry.inventory.domain.repository.ProductRepository
import com.smartpantry.inventory.domain.usecase.GetProductsUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ProductListViewModelTest {

    @Test
    fun `loadProducts emits Loading then Success with grouped products`() = runTest {
        val repository = mockk<ProductRepository>()
        val product1 = Product(id = 1, name = "Tomato", category = Category.VEGETABLES, quantity = 5, unit = "kg", location = "Fridge/Vegetables/Drawer", status = Status.AVAILABLE)
        val product2 = Product(id = 2, name = "Chicken", category = Category.MEAT, quantity = 2, unit = "kg", location = "Freezer/Meat/Drawer", status = Status.FROZEN)
        coEvery { repository.getAllProducts() } returns flowOf(listOf(product1, product2))

        val useCase = GetProductsUseCase(repository)
        val viewModel = ProductListViewModel(useCase)

        val states = viewModel.uiState.toList()

        assertEquals(2, states.size)
        assertTrue(states[0] is ProductListUiState.Loading)
        assertTrue(states[1] is ProductListUiState.Success)
        val success = states[1] as ProductListUiState.Success
        assertEquals(2, success.products.size)
        assertEquals(2, success.groupedByLocation.size)
    }

    @Test
    fun `loadProducts emits Empty when no products`() = runTest {
        val repository = mockk<ProductRepository>()
        coEvery { repository.getAllProducts() } returns flowOf(emptyList())

        val useCase = GetProductsUseCase(repository)
        val viewModel = ProductListViewModel(useCase)

        val states = viewModel.uiState.toList()

        assertEquals(2, states.size)
        assertTrue(states[0] is ProductListUiState.Loading)
        assertTrue(states[1] is ProductListUiState.Empty)
    }

    @Test
    fun `refresh triggers new load`() = runTest {
        val repository = mockk<ProductRepository>()
        val product = Product(id = 1, name = "Tomato", category = Category.VEGETABLES, quantity = 5, unit = "kg", location = "Fridge/Vegetables/Drawer", status = Status.AVAILABLE)
        coEvery { repository.getAllProducts() } returns flowOf(listOf(product))

        val useCase = GetProductsUseCase(repository)
        val viewModel = ProductListViewModel(useCase)

        // Initial load
        var states = viewModel.uiState.toList()
        assertTrue(states.last() is ProductListUiState.Success)

        // Refresh
        viewModel.refresh()
        states = viewModel.uiState.toList()
        assertTrue(states[states.size - 2] is ProductListUiState.Loading)
        assertTrue(states.last() is ProductListUiState.Success)
    }
}