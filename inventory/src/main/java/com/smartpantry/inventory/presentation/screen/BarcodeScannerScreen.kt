package com.smartpantry.inventory.presentation.screen

import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.smartpantry.inventory.R
import com.smartpantry.inventory.presentation.viewmodel.BarcodeScannerViewModel
import com.smartpantry.inventory.presentation.viewmodel.BarcodeScannerUiState

@Composable
fun BarcodeScannerScreen(
    viewModel: BarcodeScannerViewModel = viewModel(),
    onClose: () -> Unit,
    onScanResult: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }
    var flashEnabled by remember { mutableStateOf(false) }

    val scanner = remember {
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_EAN_13,
                Barcode.FORMAT_EAN_8,
                Barcode.FORMAT_UPC_A,
                Barcode.FORMAT_UPC_E,
                Barcode.FORMAT_QR_CODE,
                Barcode.FORMAT_CODE_128
            )
            .build()
        BarcodeScanning.getClient(options)
    }

    LaunchedEffect(Unit) {
        viewModel.startScanning()
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        val cameraProvider = cameraProviderFuture.get()

        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }

        val imageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build().also {
                it.setAnalyzer(ContextCompat.getMainExecutor(context)) { imageProxy ->
                    processBarcode(imageProxy, scanner) { barcode ->
                        viewModel.onBarcodeDetected(
                            com.smartpantry.inventory.domain.model.BarcodeScanResult(
                                barcode = barcode,
                                format = "UNKNOWN"
                            )
                        )
                    }
                }
            }

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview,
                imageAnalysis
            )
        } catch (e: Exception) {
            viewModel.onError("Camera initialization failed: ${e.message}")
        }
    }

    LaunchedEffect(uiState) {
        if (uiState is BarcodeScannerUiState.Result) {
            val result = (uiState as BarcodeScannerUiState.Result).scanResult
            onScanResult(result.barcode)
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = Color.Black) {
        Box(modifier = Modifier.fillMaxSize()) {
            AndroidView(
                factory = { previewView },
                modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .size(280.dp, 180.dp)
                        .border(2.dp, MaterialTheme.colorScheme.primary, androidx.compose.foundation.shape.RoundedCornerShape(8.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize())
                }
            }

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.padding(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, contentDescription = stringResource(R.string.close), tint = Color.White)
                    }
                    IconButton(onClick = { flashEnabled = !flashEnabled }) {
                        Icon(Icons.Default.FlashOn, contentDescription = stringResource(R.string.toggle_flash), tint = Color.White)
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.BottomCenter),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                when (val state = uiState) {
                    is BarcodeScannerUiState.Scanning -> {
                        Text(stringResource(R.string.position_barcode), color = Color.White, fontSize = 16.sp)
                        Text(stringResource(R.string.auto_detects), color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                    }
                    is BarcodeScannerUiState.Error -> {
                        Text(stringResource(R.string.error_format, state.message), color = MaterialTheme.colorScheme.error, fontSize = 14.sp)
                        Spacer(Modifier.padding(8.dp))
                        androidx.compose.material3.Button(onClick = { viewModel.retry() }) {
                            Text(stringResource(R.string.retry))
                        }
                    }
                    is BarcodeScannerUiState.PermissionDenied -> {
                        Text(stringResource(R.string.camera_permission_required), color = Color.White, fontSize = 16.sp)
                        Text(stringResource(R.string.grant_permission), color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                    }
                    is BarcodeScannerUiState.Result -> {
                        CircularProgressIndicator(color = Color.White)
                        Text(stringResource(R.string.barcode_detected), color = Color.White, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

private fun processBarcode(
    imageProxy: ImageProxy,
    scanner: com.google.mlkit.vision.barcode.BarcodeScanner,
    onBarcodeDetected: (String) -> Unit
) {
    val mediaImage = imageProxy.image ?: run {
        imageProxy.close()
        return
    }
    val imageRotation = imageProxy.imageInfo.rotationDegrees
    val inputImage = InputImage.fromMediaImage(mediaImage, imageRotation)

    scanner.process(inputImage)
        .addOnSuccessListener { barcodes ->
            if (barcodes.isNotEmpty()) {
                val barcode = barcodes.first()
                barcode.rawValue?.let { onBarcodeDetected(it) }
            }
        }
        .addOnCompleteListener {
            imageProxy.close()
        }
}
