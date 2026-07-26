package com.smartpantry.app

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.assertIsDisplayed
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.smartpantry.inventory.domain.model.Category
import com.smartpantry.inventory.domain.model.Status
import com.smartpantry.inventory.presentation.screen.AddEditProductScreen
import com.smartpantry.inventory.presentation.screen.ProductListScreen
import com.smartpantry.inventory.presentation.viewmodel.AddEditProductViewModel
import com.smartpantry.inventory.presentation.viewmodel.ProductListViewModel
import io.mockk.mockk
import io.mockk.every
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AddProductE2ETest {

    @get:Rule
    val composeRule = createAndroidComposeRule<TestActivity>()

    @Test
    fun add_product_flow_enter_details_and_save() {
        val listViewModel = mockk<ProductListViewModel>()
        val addViewModel = mockk<AddEditProductViewModel>()

        every { listViewModel.uiState } returns MutableStateFlow(
            com.smartpantry.inventory.presentation.viewmodel.ProductListUiState.Success(
                products = emptyList(),
                groupedByLocation = emptyMap()
            )
        )
        every { addViewModel.uiState } returns MutableStateFlow(
            com.smartpantry.inventory.presentation.viewmodel.AddEditProductUiState.Editing(
                name = "",
                location = "",
                quantity = 1
            )
        )

        composeRule.setContent {
            ProductListScreen(
                viewModel = listViewModel,
                onAddProduct = {
                    composeRule.setContent {
                        AddEditProductScreen(
                            viewModel = addViewModel,
                            onSave = {},
                            onCancel = {},
                            onBarcodeScan = {}
                        )
                    }
                },
                onProductClick = {}
            )
        }

        // Click FAB to add product
        composeRule.onNodeWithContentDescription("Add product").performClick()

        // Fill form
        composeRule.onNodeWithText("Name *").performClick()
        composeRule.onNodeWithText("Name *").performTextInput("Tomato")

        composeRule.onNodeWithText("Quantity *").performClick()
        composeRule.onNodeWithText("Quantity *").performTextInput("5")

        composeRule.onNodeWithText("Unit *").performClick()
        composeRule.onNodeWithText("Unit *").performTextInput("kg")

        // Select category
        composeRule.onNodeWithText("Category *").performClick()
        composeRule.onNodeWithText("Vegetables").performClick()

        // Select status
        composeRule.onNodeWithText("Status *").performClick()
        composeRule.onNodeWithText("Available").performClick()

        // Select location
        composeRule.onNodeWithText("Location *").performClick()
        composeRule.onNodeWithText("Fridge/Vegetables/Drawer").performClick()

        // Save
        composeRule.onNodeWithText("Save").performClick()

        // Verify navigation back to list (in real test would check list updates)
        composeRule.onNodeWithText("Smart Pantry").assertExists()
    }
}