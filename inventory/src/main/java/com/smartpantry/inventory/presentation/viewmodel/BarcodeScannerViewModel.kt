package com.smartpantry.inventory.presentation.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import com.smartpantry.inventory.domain.model.BarcodeScanResult
import com.smartpantry.inventory.domain.usecase.ScanBarcodeUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class BarcodeScannerViewModel @Inject constructor(
    private val scanBarcodeUseCase: ScanBarcodeUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<BarcodeScannerUiState>(BarcodeScannerUiState.Scanning())
    val uiState: StateFlow<BarcodeScannerUiState> = _uiState

    private var isScanning = false

    fun startScanning() {
        if (isScanning) return
        isScanning = true
        _uiState.value = BarcodeScannerUiState.Scanning(permissionGranted = true)
    }

    fun stopScanning() {
        isScanning = false
    }

    fun onBarcodeDetected(result: BarcodeScanResult) {
        if (!isScanning) return
        isScanning = false
        _uiState.value = BarcodeScannerUiState.Result(result)
    }

    fun onError(message: String) {
        isScanning = false
        _uiState.value = BarcodeScannerUiState.Error(message)
    }

    fun onPermissionDenied(permanentlyDenied: Boolean) {
        isScanning = false
        _uiState.value = BarcodeScannerUiState.PermissionDenied(permanentlyDenied)
    }

    fun retry() {
        startScanning()
    }

    fun cancel() {
        stopScanning()
        _uiState.value = BarcodeScannerUiState.Scanning()
    }
}
