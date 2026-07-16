package com.smartpantry.inventory.presentation.screen

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp

@Composable
fun ProductListScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Product List Screen - Placeholder", fontSize = 24.sp)
    }
}

@Composable
fun ProductDetailScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Product Detail Screen - Placeholder", fontSize = 24.sp)
    }
}

@Composable
fun AddEditProductScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Add/Edit Product Screen - Placeholder", fontSize = 24.sp)
    }
}

@Composable
fun BarcodeScannerScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Barcode Scanner Screen - Placeholder", fontSize = 24.sp)
    }
}