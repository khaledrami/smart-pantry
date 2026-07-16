package com.smartpantry.inventory.presentation.viewmodel

import com.smartpantry.inventory.domain.repository.BarcodeScanResult

sealed interface BarcodeScannerUiState {
    data class Scanning(val permissionGranted: Boolean = false) : BarcodeScannerUiState
    data class Result(val scanResult: BarcodeScanResult) : BarcodeScannerUiState
    data class Error(val message: String) : BarcodeScannerUiState
    data class PermissionDenied(val permanentlyDenied: Boolean) : BarcodeScannerUiState
}