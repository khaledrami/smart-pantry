@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package com.smartpantry.inventory.presentation.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smartpantry.inventory.R
import com.smartpantry.inventory.domain.model.Category
import com.smartpantry.inventory.domain.model.Product
import com.smartpantry.inventory.domain.model.Status
import coil.compose.AsyncImage
import com.smartpantry.inventory.presentation.util.calculateDaysLeft
import com.smartpantry.inventory.presentation.util.getCategoryColor
import com.smartpantry.inventory.presentation.util.getStatusColor
import com.smartpantry.inventory.presentation.util.translateLocation
import com.smartpantry.inventory.presentation.viewmodel.ProductListUiState
import com.smartpantry.inventory.presentation.viewmodel.ProductListViewModel

@Composable
fun ProductListScreen(
    viewModel: ProductListViewModel,
    onAddProduct: () -> Unit,
    onProductClick: (Long) -> Unit,
    onScanBarcode: () -> Unit = {},
    onVirtualFridge: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                    IconButton(onClick = onVirtualFridge) {
                        Icon(Icons.Default.Kitchen, contentDescription = stringResource(R.string.virtual_fridge_title))
                    }
                    IconButton(onClick = onScanBarcode) {
                        Icon(Icons.Default.QrCodeScanner, contentDescription = stringResource(R.string.scan_barcode))
                    }
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Default.Refresh, contentDescription = stringResource(R.string.refresh))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddProduct) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_product))
            }
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = MaterialTheme.colorScheme.background
        ) {
            when (val state = uiState) {
                is ProductListUiState.Loading -> {
                    if (state.isRefreshing) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    } else {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator()
                                Spacer(Modifier.padding(16.dp))
                                Text(stringResource(R.string.loading_pantry), fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
                is ProductListUiState.Empty -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Inventory2, contentDescription = "", tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f), modifier = Modifier.size(64.dp))
                            Spacer(Modifier.padding(16.dp))
                            Text(stringResource(R.string.pantry_empty), fontSize = 18.sp, fontWeight = FontWeight.Medium)
                            Spacer(Modifier.padding(8.dp))
                            Text(stringResource(R.string.tap_to_add), fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
                is ProductListUiState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        state.groupedByLocation.forEach { (location, products) ->
                            stickyHeader {
                                LocationHeader(location = location, count = products.size)
                            }
                            items(products) { product ->
                                ProductCard(
                                    product = product,
                                    onClick = { onProductClick(product.id!!) }
                                )
                            }
                        }
                    }
                }
                is ProductListUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(stringResource(R.string.error_format, state.message), fontSize = 16.sp, color = MaterialTheme.colorScheme.error)
                            Spacer(Modifier.padding(16.dp))
                            androidx.compose.material3.Button(onClick = { viewModel.refresh() }) {
                                Text(stringResource(R.string.retry))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LocationHeader(location: String, count: Int) {
    val locationName = translateLocation(location).split("/").last().trim()
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(locationName, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            Text(stringResource(R.string.count_items, count), fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun ProductCard(
    product: Product,
    onClick: () -> Unit
) {
    val categoryColor = getCategoryColor(product.category)
    val statusColor = getStatusColor(product.status)
    val daysLeft = product.expiryDate?.let { calculateDaysLeft(it) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (product.photoUri != null) {
                AsyncImage(
                    model = product.photoUri,
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .background(categoryColor, androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(categoryColor, androidx.compose.foundation.shape.RoundedCornerShape(6.dp))
                )
            }
            Spacer(Modifier.padding(12.dp))

            Column(Modifier.weight(1f)) {
                Text(product.name, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                Spacer(Modifier.padding(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("${product.quantity} ${product.unit}", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    if (product.brand.isNotBlank()) {
                        Text("·", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(product.brand, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                AssistChip(
                    onClick = {},
                    label = { Text(product.status.name, fontSize = 10.sp) },
                    colors = AssistChipDefaults.assistChipColors(containerColor = statusColor.copy(alpha = 0.15f))
                )
                daysLeft?.let { days ->
                    if (days in 0..7) {
                        Spacer(Modifier.padding(top = 4.dp))
                        Text(
                            when (days) {
                                0L -> stringResource(R.string.expires_today)
                                1L -> stringResource(R.string.expires_tomorrow)
                                else -> stringResource(R.string.expires_in_days, days)
                            },
                            fontSize = 12.sp,
                            color = if (days <= 2) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

