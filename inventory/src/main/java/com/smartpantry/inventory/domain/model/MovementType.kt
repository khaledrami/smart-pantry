package com.smartpantry.inventory.domain.model

import kotlinx.serialization.Serializable

@Serializable
sealed interface MovementType {
    @Serializable
    data class Entry(val oldQuantity: Int?, val newQuantity: Int) : MovementType
    @Serializable
    data class Exit(val oldQuantity: Int, val newQuantity: Int?) : MovementType
    @Serializable
    data class Freeze(val locationBefore: String, val locationAfter: String) : MovementType
    @Serializable
    data class Thaw(val locationBefore: String, val locationAfter: String) : MovementType
    @Serializable
    data class LocationChange(val from: String, val to: String) : MovementType
    @Serializable
    data class Correction(val field: String, val oldValue: String, val newValue: String) : MovementType
    @Serializable
    object Donation : MovementType
    @Serializable
    object Discard : MovementType
}