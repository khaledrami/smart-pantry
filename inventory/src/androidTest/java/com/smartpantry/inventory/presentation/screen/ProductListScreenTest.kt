package com.smartpantry.inventory.presentation.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNode
import androidx.compose.ui.test.onAllNodes
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import com.smartpantry.inventory.domain.model.Category
import com.smartpantry.inventory.domain.model.Product
import com.smartpantry.inventory.domain.model.Status
import com.smartpantry.inventory.presentation.viewmodel.ProductListUiState
import com.smartpantry.inventory.presentation.viewmodel.ProductListViewModel
import io.mockk.mockk
import io.mockk.every
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ProductListScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `ProductListScreen renders empty state when no products`() {
        val viewModel = mockk<ProductListViewModel>()
        every { viewModel.uiState } returns flowOf(ProductListUiState.Empty)

        composeRule.setContent {
            ProductListScreen(
                viewModel = viewModel,
                onAddProduct = {},
                onProductClick = {}
            )
        }

        composeRule.onNodeWithText("Your pantry is empty").assertExists()
        composeRule.onNodeWithText("Tap + to add your first item").assertExists()
    }

    @Test
    fun `ProductListScreen renders product list grouped by location`() {
        val viewModel = mockk<ProductListViewModel>()
        val products = listOf(
            Product(id = 1, name = "Tomato", category = Category.VEGETABLES, quantity = 5, unit = "kg", location = "Fridge/Vegetables/Drawer", status = Status.AVAILABLE),
            Product(id = 2, name = "Chicken", category = Category.MEAT, quantity = 2, unit = "kg", location = "Freezer/Meat/Drawer", status = Status.FROZEN)
        )
        val grouped = products.groupBy { it.location }
        every { viewModel.uiState } returns flowOf(ProductListUiState.Success(products, grouped))

        composeRule.setContent {
            ProductListScreen(
                viewModel = viewModel,
                onAddProduct = {},
                onProductClick = {}
            )
        }

        composeRule.onNodeWithText("Fridge/Vegetables/Drawer").assertExists()
        composeRule.onNodeWithText("Freezer/Meat/Drawer").assertExists()
        composeRule.onNodeWithText("Tomato").assertExists()
        composeRule.onNodeWithText("Chicken").assertExists()
    }

    @Test
    fun `ProductListScreen shows loading state initially`() {
        val viewModel = mockk<ProductListViewModel>()
        every { viewModel.uiState } returns flowOf(ProductListUiState.Loading())

        composeRule.setContent {
            ProductListScreen(
                viewModel = viewModel,
                onAddProduct = {},
                onProductClick = {}
            )
        }

        composeRule.onNodeWithContentDescription("Loading").assertExists()
    }

    @Test
    fun `ProductListScreen shows error state`() {
        val viewModel = mockk<ProductListViewModel>()
        every { viewModel.uiState } returns flowOf(ProductListUiState.Error("Database error"))

        composeRule.setContent {
            ProductListScreen(
                viewModel = viewModel,
                onAddProduct = {},
                onProductClick = {}
            )
        }

        composeRule.onNodeWithText("Error: Database error").assertExists()
        composeRule.onNodeWithText("Retry").assertExists()
    }
}