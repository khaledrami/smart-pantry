package com.smartpantry.inventory.domain.model

import java.time.LocalDateTime

data class Movement(
    val id: Long? = null,
    val productId: Long,
    val type: MovementType,
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val userId: String = "current_user"
)