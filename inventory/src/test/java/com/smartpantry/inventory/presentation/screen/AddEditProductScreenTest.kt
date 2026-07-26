package com.smartpantry.inventory.presentation.screen

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardType
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
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.smartpantry.inventory.domain.model.Category
import com.smartpantry.inventory.domain.model.Status
import com.smartpantry.inventory.presentation.viewmodel.AddEditProductUiState
import com.smartpantry.inventory.presentation.viewmodel.AddEditProductViewModel
import io.mockk.mockk
import io.mockk.every
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AddEditProductScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `AddEditProductScreen renders form fields for new product`() {
        val viewModel = mockk<AddEditProductViewModel>()
        every { viewModel.uiState } returns flowOf(
            AddEditProductUiState.Editing()
        )

        composeRule.setContent {
            AddEditProductScreen(
                viewModel = viewModel,
                onSave = {},
                onCancel = {},
                onBarcodeScan = {}
            )
        }

        composeRule.onNodeWithText("Name *").assertExists()
        composeRule.onNodeWithText("Description").assertExists()
        composeRule.onNodeWithText("Brand").assertExists()
        composeRule.onNodeWithText("Category *").assertExists()
        composeRule.onNodeWithText("Barcode").assertExists()
        composeRule.onNodeWithText("Quantity *").assertExists()
        composeRule.onNodeWithText("Unit *").assertExists()
        composeRule.onNodeWithText("Price").assertExists()
        composeRule.onNodeWithText("Purchase Date (YYYY-MM-DD)").assertExists()
        composeRule.onNodeWithText("Expiry Date (YYYY-MM-DD)").assertExists()
        composeRule.onNodeWithText("Location *").assertExists()
        composeRule.onNodeWithText("Status *").assertExists()
        composeRule.onNodeWithText("Notes").assertExists()
        composeRule.onNodeWithText("Tags (comma separated)").assertExists()
        composeRule.onNodeWithText("Save").assertExists()
        composeRule.onNodeWithContentDescription("Cancel").assertExists()
    }

    @Test
    fun `AddEditProductScreen shows validation error when name is empty`() {
        val viewModel = mockk<AddEditProductViewModel>()
        every { viewModel.uiState } returns flowOf(
            AddEditProductUiState.Editing(name = "", error = "Name is required")
        )

        composeRule.setContent {
            AddEditProductScreen(
                viewModel = viewModel,
                onSave = {},
                onCancel = {},
                onBarcodeScan = {}
            )
        }

        composeRule.onNodeWithText("Name is required").assertExists()
    }

    @Test
    fun `AddEditProductScreen shows category dropdown`() {
        val viewModel = mockk<AddEditProductViewModel>()
        every { viewModel.uiState } returns flowOf(
            AddEditProductUiState.Editing()
        )

        composeRule.setContent {
            AddEditProductScreen(
                viewModel = viewModel,
                onSave = {},
                onCancel = {},
                onBarcodeScan = {}
            )
        }

        composeRule.onNodeWithText("Category *").performClick()
        composeRule.onNodeWithText("Vegetables").assertExists()
        composeRule.onNodeWithText("Meat").assertExists()
        composeRule.onNodeWithText("Fruits").assertExists()
    }

    @Test
    fun `AddEditProductScreen shows status dropdown`() {
        val viewModel = mockk<AddEditProductViewModel>()
        every { viewModel.uiState } returns flowOf(
            AddEditProductUiState.Editing()
        )

        composeRule.setContent {
            AddEditProductScreen(
                viewModel = viewModel,
                onSave = {},
                onCancel = {},
                onBarcodeScan = {}
            )
        }

        composeRule.onNodeWithText("Status *").performClick()
        composeRule.onNodeWithText("Available").assertExists()
        composeRule.onNodeWithText("Opened").assertExists()
        composeRule.onNodeWithText("Frozen").assertExists()
        composeRule.onNodeWithText("Consumed").assertExists()
        composeRule.onNodeWithText("Expired").assertExists()
        composeRule.onNodeWithText("Donated").assertExists()
        composeRule.onNodeWithText("Discarded").assertExists()
    }
}