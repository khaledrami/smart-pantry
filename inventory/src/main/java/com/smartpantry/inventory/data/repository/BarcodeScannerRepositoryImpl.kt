package com.smartpantry.inventory.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.smartpantry.inventory.domain.model.BarcodeScanResult
import com.smartpantry.inventory.domain.model.ProductData
import com.smartpantry.inventory.domain.repository.BarcodeScannerRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BarcodeScannerRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : BarcodeScannerRepository, DefaultLifecycleObserver {

    private val scanner: BarcodeScanner by lazy {
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

    private var pendingScan: CompletableDeferred<Result<BarcodeScanResult>>? = null

    override suspend fun scan(): Result<BarcodeScanResult> {
        val deferred = CompletableDeferred<Result<BarcodeScanResult>>()
        pendingScan = deferred
        return deferred.await()
    }

    fun processImageProxy(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image ?: return
        val imageRotation = imageProxy.imageInfo.rotationDegrees
        val inputImage = InputImage.fromMediaImage(mediaImage, imageRotation)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val barcodes = scanner.process(inputImage).await()
                if (barcodes.isNotEmpty()) {
                    val barcode = barcodes.first()
                    pendingScan?.complete(Result.success(BarcodeScanResult(
                        barcode = barcode.rawValue ?: "",
                        format = barcode.format.toString()
                    )))
                } else {
                    // No barcode detected, keep scanning
                }
            } catch (e: Exception) {
                pendingScan?.complete(Result.failure(e))
            } finally {
                imageProxy.close()
            }
        }
    }

    override suspend fun lookupProduct(barcode: String): Result<ProductData> {
        return Result.failure(NotImplementedError("Product lookup not implemented in scanner repository"))
    }

    override fun onDestroy(owner: LifecycleOwner) {
        scanner.close()
    }
}