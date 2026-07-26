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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.smartpantry.inventory.R
import com.smartpantry.inventory.domain.model.Category
import com.smartpantry.inventory.domain.model.ProductData
import com.smartpantry.inventory.domain.model.Status
import com.smartpantry.inventory.presentation.util.translateLocation
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
            if (barcode.isNotBlank()) {
                viewModel.lookupBarcode(barcode)
            }
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
                    Text(stringResource(R.string.saving), fontSize = 16.sp)
                }
            }
        }

        is AddEditProductUiState.Saved -> {
            onSave()
        }

        is AddEditProductUiState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(stringResource(R.string.error_format, state.message), fontSize = 16.sp, color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.padding(16.dp))
                    Button(onClick = { viewModel.save() }) {
                        Text(stringResource(R.string.retry))
                    }
                }
            }
        }

        is AddEditProductUiState.BarcodeLookupResult -> {
            AlertDialog(
                onDismissRequest = {},
                title = { Text(stringResource(R.string.product_found)) },
                text = { Text(stringResource(R.string.auto_fill_barcode)) },
                confirmButton = {
                    Button(onClick = {
                        viewModel.applyBarcodeLookupResult(state.productData)
                    }) {
                        Text(stringResource(R.string.yes_fill_it))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.dismissBarcodeDialog() }) {
                        Text(stringResource(R.string.no_enter_manually))
                    }
                }
            )
        }

        is AddEditProductUiState.BarcodeLookupError -> {
            AlertDialog(
                onDismissRequest = {},
                title = { Text(stringResource(R.string.product_not_found)) },
                text = { Text(state.message) },
                confirmButton = {
                    Button(onClick = { viewModel.dismissBarcodeDialog() }) {
                        Text(stringResource(R.string.ok))
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
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            viewModel.updatePhotoUri(uri.toString())
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.edit_product)) },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(Icons.Default.Close, contentDescription = stringResource(R.string.cancel))
                    }
                },
                actions = {
                    Button(onClick = onSave) {
                        if (state.isSaving) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp))
                        } else {
                            Text(stringResource(R.string.save))
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = state.barcode,
                    onValueChange = { viewModel.updateBarcode(it) },
                    label = { Text(stringResource(R.string.barcode)) },
                    placeholder = { Text(stringResource(R.string.scan_or_enter_barcode)) },
                    leadingIcon = {
                        IconButton(onClick = onBarcodeScan) {
                            Icon(Icons.Default.CameraAlt, contentDescription = stringResource(R.string.scan_barcode))
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            // Photo section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clickable {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                colors = CardDefaults.cardColors(
                    containerColor = if (state.photoUri != null)
                        MaterialTheme.colorScheme.surfaceContainerLow
                    else
                        MaterialTheme.colorScheme.surfaceContainerHighest
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (state.photoUri != null) {
                        Image(
                            painter = rememberAsyncImagePainter(state.photoUri),
                            contentDescription = stringResource(R.string.photo_picker_title),
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.PhotoCamera,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                            Spacer(Modifier.padding(8.dp))
                            Text(
                                stringResource(R.string.add_photo),
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }

            OutlinedTextField(
                value = state.name,
                onValueChange = { viewModel.updateName(it) },
                label = { Text(stringResource(R.string.name)) },
                isError = state.error != null && state.name.isBlank(),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.description,
                onValueChange = { viewModel.updateDescription(it) },
                label = { Text(stringResource(R.string.description)) },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.brand,
                onValueChange = { viewModel.updateBrand(it) },
                label = { Text(stringResource(R.string.brand)) },
                modifier = Modifier.fillMaxWidth()
            )

            var categoryExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = { categoryExpanded = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = stringResource(state.category.labelRes),
                    onValueChange = {},
                    label = { Text(stringResource(R.string.category_label)) },
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
                            text = { Text(stringResource(cat.labelRes)) },
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
                    value = stringResource(state.status.labelRes),
                    onValueChange = {},
                    label = { Text(stringResource(R.string.status_label)) },
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
                            text = { Text(stringResource(status.labelRes)) },
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
                    value = if (state.location.isBlank()) stringResource(R.string.select_location) else translateLocation(state.location),
                    onValueChange = {},
                    label = { Text(stringResource(R.string.location_label)) },
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
                            text = { Text(translateLocation(loc)) },
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
                    label = { Text(stringResource(R.string.quantity_label)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = state.unit,
                    onValueChange = { viewModel.updateUnit(it) },
                    label = { Text(stringResource(R.string.unit)) },
                    modifier = Modifier.weight(1f)
                )
            }

            OutlinedTextField(
                value = state.price.toString(),
                onValueChange = { viewModel.updatePrice(it.toDoubleOrNull() ?: 0.0) },
                label = { Text(stringResource(R.string.price_label)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.purchaseDate,
                onValueChange = { viewModel.updatePurchaseDate(it) },
                label = { Text(stringResource(R.string.purchase_date)) },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.expiryDate,
                onValueChange = { viewModel.updateExpiryDate(it) },
                label = { Text(stringResource(R.string.expiry_date)) },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.notes,
                onValueChange = { viewModel.updateNotes(it) },
                label = { Text(stringResource(R.string.notes_label)) },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.tags,
                onValueChange = { viewModel.updateTags(it) },
                label = { Text(stringResource(R.string.tags_label)) },
                modifier = Modifier.fillMaxWidth()
            )

            if (state.error != null) {
                Text(state.error!!, color = MaterialTheme.colorScheme.error, fontSize = 14.sp)
            }
        }
    }
}
