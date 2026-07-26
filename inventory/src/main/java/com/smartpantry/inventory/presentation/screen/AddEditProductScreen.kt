@file:OptIn(ExperimentalMaterial3Api::class)

package com.smartpantry.inventory.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smartpantry.inventory.domain.model.Category
import com.smartpantry.inventory.domain.model.ProductData
import com.smartpantry.inventory.domain.model.Status
import com.smartpantry.inventory.presentation.viewmodel.AddEditProductUiState
import com.smartpantry.inventory.presentation.viewmodel.AddEditProductViewModel

@Composable
fun AddEditProductScreen(
    viewModel: AddEditProductViewModel,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    onBarcodeScan: () -> Unit = {},
    productId: Long? = null,
    barcode: String? = null
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(productId, barcode) {
        if (productId != null) {
            viewModel.loadProductForEdit(productId)
        } else if (barcode != null) {
            viewModel.initializeForNew(barcode)
        }
    }

    when (val state = uiState) {
        is AddEditProductUiState.Editing -> {
            AddEditProductContent(
                state = state,
                viewModel = viewModel,
                onSave = { viewModel.save() },
                onCancel = onCancel,
                onBarcodeScan = onBarcodeScan
            )
        }

        is AddEditProductUiState.Saving -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(Modifier.padding(16.dp))
                    Text("Saving...", fontSize = 16.sp)
                }
            }
        }

        is AddEditProductUiState.Saved -> {
            onSave()
        }

        is AddEditProductUiState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Error: ${state.message}", fontSize = 16.sp, color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.padding(16.dp))
                    Button(onClick = { viewModel.save() }) {
                        Text("Retry")
                    }
                }
            }
        }

        is AddEditProductUiState.BarcodeLookupResult -> {
            AlertDialog(
                onDismissRequest = {},
                title = { Text("Product Found") },
                text = { Text("Auto-fill product details from barcode?") },
                confirmButton = {
                    Button(onClick = {
                        viewModel.applyBarcodeLookupResult(state.productData)
                    }) {
                        Text("Yes, fill it")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {}) {
                        Text("No, enter manually")
                    }
                }
            )
        }

        is AddEditProductUiState.BarcodeLookupError -> {
            AlertDialog(
                onDismissRequest = {},
                title = { Text("Product Not Found") },
                text = { Text(state.message) },
                confirmButton = {
                    Button(onClick = {}) {
                        Text("OK")
                    }
                }
            )
        }
    }
}

@Composable
private fun AddEditProductContent(
    state: AddEditProductUiState.Editing,
    viewModel: AddEditProductViewModel,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    onBarcodeScan: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Product") },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(Icons.Default.Close, contentDescription = "Cancel")
                    }
                },
                actions = {
                    Button(onClick = onSave) {
                        if (state.isSaving) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp))
                        } else {
                            Text("Save")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = state.barcode,
                    onValueChange = { viewModel.updateBarcode(it) },
                    label = { Text("Barcode") },
                    placeholder = { Text("Scan or enter barcode") },
                    leadingIcon = {
                        IconButton(onClick = onBarcodeScan) {
                            Icon(Icons.Default.CameraAlt, contentDescription = "Scan barcode")
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            OutlinedTextField(
                value = state.name,
                onValueChange = { viewModel.updateName(it) },
                label = { Text("Name *") },
                isError = state.error != null && state.name.isBlank(),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.description,
                onValueChange = { viewModel.updateDescription(it) },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.brand,
                onValueChange = { viewModel.updateBrand(it) },
                label = { Text("Brand") },
                modifier = Modifier.fillMaxWidth()
            )

            var categoryExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = { categoryExpanded = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = state.category.displayName,
                    onValueChange = {},
                    label = { Text("Category *") },
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                )
                ExposedDropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false }
                ) {
                    Category.entries.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat.displayName) },
                            onClick = {
                                viewModel.updateCategory(cat)
                                categoryExpanded = false
                            }
                        )
                    }
                }
            }

            var statusExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = statusExpanded,
                onExpandedChange = { statusExpanded = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = state.status.displayName,
                    onValueChange = {},
                    label = { Text("Status *") },
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                )
                ExposedDropdownMenu(
                    expanded = statusExpanded,
                    onDismissRequest = { statusExpanded = false }
                ) {
                    Status.entries.forEach { status ->
                        DropdownMenuItem(
                            text = { Text(status.displayName) },
                            onClick = {
                                viewModel.updateStatus(status)
                                statusExpanded = false
                            }
                        )
                    }
                }
            }

            val locations = remember {
                listOf(
                    "Freezer/Upper Drawer/Left Slot",
                    "Freezer/Upper Drawer/Right Slot",
                    "Freezer/Middle Drawer/Left Slot",
                    "Freezer/Middle Drawer/Right Slot",
                    "Freezer/Lower Drawer/Left Slot",
                    "Freezer/Lower Drawer/Right Slot",
                    "Freezer/Door/Top",
                    "Freezer/Door/Bottom",
                    "Fridge/Door/Top",
                    "Fridge/Door/Middle",
                    "Fridge/Door/Bottom",
                    "Fridge/Upper Shelf/Left",
                    "Fridge/Upper Shelf/Right",
                    "Fridge/Middle Shelf/Left",
                    "Fridge/Middle Shelf/Right",
                    "Fridge/Lower Shelf/Left",
                    "Fridge/Lower Shelf/Right",
                    "Fridge/Veggie Drawer/Left",
                    "Fridge/Veggie Drawer/Right",
                    "Pantry/Top Shelf/Left",
                    "Pantry/Top Shelf/Right",
                    "Pantry/Middle Shelf/Left",
                    "Pantry/Middle Shelf/Right",
                    "Pantry/Bottom Shelf/Left",
                    "Pantry/Bottom Shelf/Right",
                    "Pantry/Basket/Left",
                    "Pantry/Basket/Right"
                )
            }
            var locationExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = locationExpanded,
                onExpandedChange = { locationExpanded = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = if (state.location.isBlank()) "Select location" else state.location,
                    onValueChange = {},
                    label = { Text("Location *") },
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = locationExpanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                )
                ExposedDropdownMenu(
                    expanded = locationExpanded,
                    onDismissRequest = { locationExpanded = false }
                ) {
                    locations.forEach { loc ->
                        DropdownMenuItem(
                            text = { Text(loc) },
                            onClick = {
                                viewModel.updateLocation(loc)
                                locationExpanded = false
                            }
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = state.quantity.toString(),
                    onValueChange = { viewModel.updateQuantity(it.toIntOrNull() ?: 1) },
                    label = { Text("Quantity *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = state.unit,
                    onValueChange = { viewModel.updateUnit(it) },
                    label = { Text("Unit *") },
                    modifier = Modifier.weight(1f)
                )
            }

            OutlinedTextField(
                value = state.price.toString(),
                onValueChange = { viewModel.updatePrice(it.toDoubleOrNull() ?: 0.0) },
                label = { Text("Price") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.purchaseDate,
                onValueChange = { viewModel.updatePurchaseDate(it) },
                label = { Text("Purchase Date (YYYY-MM-DD)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.expiryDate,
                onValueChange = { viewModel.updateExpiryDate(it) },
                label = { Text("Expiry Date (YYYY-MM-DD)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.notes,
                onValueChange = { viewModel.updateNotes(it) },
                label = { Text("Notes") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.tags,
                onValueChange = { viewModel.updateTags(it) },
                label = { Text("Tags (comma separated)") },
                modifier = Modifier.fillMaxWidth()
            )

            if (state.error != null) {
                Text(state.error!!, color = MaterialTheme.colorScheme.error, fontSize = 14.sp)
            }
        }
    }
}
