package com.smartpantry.inventory.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class Status(val displayName: String) {
    AVAILABLE("Disponible"),
    OPENED("Abierto"),
    FROZEN("Congelado"),
    CONSUMED("Consumido"),
    EXPIRED("Caducado"),
    DONATED("Donado"),
    DISCARDED("Desechado")
}