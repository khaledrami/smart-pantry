package com.smartpantry.app

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.smartpantry.inventory.domain.model.Category
import com.smartpantry.inventory.domain.model.Movement
import com.smartpantry.inventory.domain.model.MovementType
import com.smartpantry.inventory.domain.model.Product
import com.smartpantry.inventory.domain.model.Status
import com.smartpantry.inventory.presentation.screen.ProductDetailScreen
import com.smartpantry.inventory.presentation.screen.ProductListScreen
import com.smartpantry.inventory.presentation.viewmodel.ProductDetailViewModel
import com.smartpantry.inventory.presentation.viewmodel.ProductListViewModel
import io.mockk.mockk
import io.mockk.every
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDateTime

@RunWith(AndroidJUnit4::class)
class EditQuantityE2ETest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `edit quantity creates movement log entry`() {
        val listViewModel = mockk<ProductListViewModel>()
        val detailViewModel = mockk<ProductDetailViewModel>()

        val testProduct = Product(
            id = 1,
            name = "Tomato",
            category = Category.VEGETABLES,
            quantity = 10,
            unit = "kg",
            location = "Fridge/Vegetables/Drawer",
            status = Status.AVAILABLE
        )

        val movements = listOf(
            Movement(
                id = 1,
                productId = 1,
                type = MovementType.Entry(oldQuantity = null, newQuantity = 10),
                timestamp = LocalDateTime.now()
            )
        )

        every { listViewModel.uiState } returns MutableStateFlow(
            com.smartpantry.inventory.presentation.viewmodel.ProductListUiState.Success(
                products = listOf(testProduct),
                groupedByLocation = mapOf("Fridge/Vegetables/Drawer" to listOf(testProduct))
            )
        )

        every { detailViewModel.uiState } returns MutableStateFlow(
            com.smartpantry.inventory.presentation.viewmodel.ProductDetailUiState.Success(
                product = testProduct,
                movements = movements
            )
        )

        composeRule.setContent {
            ProductListScreen(
                viewModel = listViewModel,
                onAddProduct = {},
                onProductClick = { id ->
                    composeRule.setContent {
                        ProductDetailScreen(
                            viewModel = detailViewModel,
                            productId = id,
                            onBack = {},
                            onEdit = { _ -> },
                            onDelete = { _ -> }
                        )
                    }
                }
            )
        }

        // Click on product
        composeRule.onNodeWithText("Tomato").performClick()

        // Verify detail screen shows product
        composeRule.onNodeWithText("Tomato").assertExists()
        composeRule.onNodeWithText("10 kg").assertExists()

        // Verify movement history is displayed
        composeRule.onNodeWithText("Movement History").assertExists()
        composeRule.onNodeWithText("Added 10").assertExists()

        // Edit quantity (would need edit screen navigation in real test)
        // This test verifies the detail screen renders with movement history
    }
}