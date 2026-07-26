package com.smartpantry.inventory.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class BarcodeScanResult(
    val barcode: String,
    val format: String
)
