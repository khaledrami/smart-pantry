@file:OptIn(ExperimentalMaterial3Api::class)

package com.smartpantry.inventory.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smartpantry.inventory.R
import com.smartpantry.inventory.domain.model.Category
import com.smartpantry.inventory.domain.model.Movement
import com.smartpantry.inventory.domain.model.Product
import com.smartpantry.inventory.domain.model.Status
import com.smartpantry.inventory.presentation.viewmodel.ProductDetailUiState
import com.smartpantry.inventory.presentation.viewmodel.ProductDetailViewModel

@Composable
fun ProductDetailScreen(
    viewModel: ProductDetailViewModel,
    productId: Long,
    onBack: () -> Unit,
    onEdit: (Product) -> Unit,
    onDelete: (Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(productId) {
        viewModel.loadProduct(productId)
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(Modifier.fillMaxSize()) {
            TopAppBar(
                title = { Text(stringResource(R.string.product_details)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val state = uiState
                        if (state is ProductDetailUiState.Success) {
                            onEdit(state.product)
                        }
                    }) {
                        Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.edit))
                    }
                    IconButton(onClick = { onDelete(productId) }) {
                        Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.delete))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )

            when (val state = uiState) {
                is ProductDetailUiState.Loading -> {
                    if (!state.isRefreshing) {
                        Box(Modifier.fillMaxSize(), Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    } else {
                        ProductDetailContent(
                            uiState = ProductDetailUiState.Success(
                                product = Product(
                                    id = productId, name = "", category = Category.OTHER, quantity = 0, unit = "", location = "", status = Status.AVAILABLE
                                ),
                                movements = emptyList()
                            ),
                            onEdit = onEdit,
                            onDelete = onDelete
                        )
                    }
                }
                is ProductDetailUiState.Success -> {
                    ProductDetailContent(uiState = state, onEdit = onEdit, onDelete = onDelete)
                }
                is ProductDetailUiState.Error -> {
                    Box(Modifier.fillMaxSize(), Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = state.message, color = MaterialTheme.colorScheme.error)
                            androidx.compose.material3.Button(onClick = { viewModel.refresh(productId) }) {
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
fun ProductDetailContent(
    uiState: ProductDetailUiState.Success,
    onEdit: (Product) -> Unit,
    onDelete: (Long) -> Unit
) {
    val product = uiState.product
    val movements = uiState.movements

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
        ) {
            Column(Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(64.dp).background(getCategoryColor(product.category)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(product.category.name.first().toString(), fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    Column(Modifier.padding(start = 16.dp).weight(1f)) {
                        Text(product.name, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        Text(product.brand, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        if (product.barcode.isNotBlank()) {
                            Text(stringResource(R.string.barcode_format, product.barcode), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
                        }
                    }

                    StatusChip(status = product.status.name)
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
        ) {
            Column(Modifier.padding(16.dp)) {
                DetailRow(stringResource(R.string.quantity), "${product.quantity} ${product.unit}")
                DetailRow(stringResource(R.string.price), String.format("%.2f EUR", product.price))
                DetailRow(stringResource(R.string.location), product.location)
                DetailRow(stringResource(R.string.status), stringResource(product.status.labelRes))
                if (product.purchaseDate != null) DetailRow(stringResource(R.string.purchased), product.purchaseDate.toString())
                if (product.openDate != null) DetailRow(stringResource(R.string.opened), product.openDate.toString())
                if (product.freezeDate != null) DetailRow(stringResource(R.string.frozen), product.freezeDate.toString())
                if (product.bestBeforeDate != null) DetailRow(stringResource(R.string.best_before), product.bestBeforeDate.toString())
                if (product.expiryDate != null) {
                    val daysLeft = calculateDaysLeft(product.expiryDate!!)
                    DetailRow(stringResource(R.string.expires), stringResource(R.string.expires_days_format, product.expiryDate.toString(), daysLeft))
                }
                if (product.notes.isNotBlank()) DetailRow(stringResource(R.string.notes), product.notes)
                if (product.tags.isNotEmpty()) DetailRow(stringResource(R.string.tags), product.tags.joinToString(", "))
            }
        }

        if (movements.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(stringResource(R.string.movement_history), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    HorizontalDivider(Modifier.padding(vertical = 8.dp))
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(movements) { movement ->
                            MovementItem(movement = movement)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.End)
    }
}

@Composable
fun MovementItem(movement: Movement) {
    val (icon, color, description) = when (movement.type) {
        is com.smartpantry.inventory.domain.model.MovementType.Entry ->
            Triple(
                Icons.Default.AddCircle,
                MaterialTheme.colorScheme.primary,
                when {
                    movement.type.oldQuantity != null ->
                        stringResource(R.string.movement_added_was, movement.type.newQuantity, movement.type.oldQuantity!!)
                    else ->
                        stringResource(R.string.movement_added, movement.type.newQuantity)
                }
            )
        is com.smartpantry.inventory.domain.model.MovementType.Exit ->
            Triple(
                Icons.Default.RemoveCircle,
                MaterialTheme.colorScheme.error,
                when {
                    movement.type.newQuantity != null ->
                        stringResource(R.string.movement_removed_now, movement.type.oldQuantity, movement.type.newQuantity!!)
                    else ->
                        stringResource(R.string.movement_removed, movement.type.oldQuantity)
                }
            )
        is com.smartpantry.inventory.domain.model.MovementType.Freeze ->
            Triple(
                Icons.Default.Edit,
                MaterialTheme.colorScheme.tertiary,
                stringResource(R.string.movement_freeze, movement.type.locationBefore, movement.type.locationAfter)
            )
        is com.smartpantry.inventory.domain.model.MovementType.Thaw ->
            Triple(
                Icons.Default.Edit,
                MaterialTheme.colorScheme.secondary,
                stringResource(R.string.movement_thaw, movement.type.locationBefore, movement.type.locationAfter)
            )
        is com.smartpantry.inventory.domain.model.MovementType.LocationChange ->
            Triple(
                Icons.Default.Edit,
                MaterialTheme.colorScheme.outline,
                stringResource(R.string.movement_location, movement.type.from, movement.type.to)
            )
        is com.smartpantry.inventory.domain.model.MovementType.Correction ->
            Triple(
                Icons.Default.Edit,
                Color(0xFFFFA000),
                stringResource(R.string.movement_correction, movement.type.field, movement.type.oldValue, movement.type.newValue)
            )
        com.smartpantry.inventory.domain.model.MovementType.Donation ->
            Triple(
                Icons.Default.Edit,
                MaterialTheme.colorScheme.primary,
                stringResource(R.string.movement_donation)
            )
        com.smartpantry.inventory.domain.model.MovementType.Discard ->
            Triple(
                Icons.Default.Delete,
                MaterialTheme.colorScheme.error,
                stringResource(R.string.movement_discard)
            )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = "", tint = color)
            Spacer(Modifier.padding(12.dp))
            Column(Modifier.weight(1f)) {
                Text(description, fontSize = 14.sp)
                Text(movement.timestamp.toString(), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
            }
        }
    }
}

@Composable
fun StatusChip(status: String) {
    val (color, label) = when (status) {
        "AVAILABLE" -> MaterialTheme.colorScheme.primary to stringResource(R.string.status_available)
        "OPENED" -> MaterialTheme.colorScheme.secondary to stringResource(R.string.status_opened)
        "FROZEN" -> MaterialTheme.colorScheme.tertiary to stringResource(R.string.status_frozen)
        "CONSUMED" -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f) to stringResource(R.string.status_consumed)
        "EXPIRED" -> MaterialTheme.colorScheme.error to stringResource(R.string.status_expired)
        "DONATED" -> MaterialTheme.colorScheme.outline to stringResource(R.string.status_donated)
        "DISCARDED" -> MaterialTheme.colorScheme.errorContainer to stringResource(R.string.status_discarded)
        else -> MaterialTheme.colorScheme.onSurfaceVariant to status
    }

    AssistChip(
        modifier = Modifier.padding(top = 8.dp),
        onClick = {},
        label = { Text(label, fontSize = 12.sp) },
        colors = AssistChipDefaults.assistChipColors(containerColor = color.copy(alpha = 0.15f))
    )
}
