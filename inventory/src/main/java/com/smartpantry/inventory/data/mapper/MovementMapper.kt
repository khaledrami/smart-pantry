package com.smartpantry.inventory.data.mapper

import com.smartpantry.inventory.data.entity.MovementEntity
import com.smartpantry.inventory.domain.model.Movement
import com.smartpantry.inventory.domain.model.MovementType
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object MovementMapper {
    private val json = Json { ignoreUnknownKeys = true }

    fun toDomain(entity: MovementEntity): Movement {
        val type = json.decodeFromString<MovementType>(entity.typePayload)
        return Movement(
            id = if (entity.id == 0L) null else entity.id,
            productId = entity.productId,
            type = type,
            timestamp = java.time.LocalDateTime.ofInstant(
                java.time.Instant.ofEpochMilli(entity.timestamp),
                java.time.ZoneId.systemDefault()
            ),
            userId = entity.userId
        )
    }

    fun toEntity(domain: Movement): MovementEntity {
        val typeName = when (domain.type) {
            is MovementType.Entry -> "Entry"
            is MovementType.Exit -> "Exit"
            is MovementType.Freeze -> "Freeze"
            is MovementType.Thaw -> "Thaw"
            is MovementType.LocationChange -> "LocationChange"
            is MovementType.Correction -> "Correction"
            MovementType.Donation -> "Donation"
            MovementType.Discard -> "Discard"
        }
        return MovementEntity(
            id = domain.id ?: 0L,
            productId = domain.productId,
            type = typeName,
            typePayload = json.encodeToString(domain.type),
            timestamp = java.time.Instant.from(domain.timestamp.atZone(java.time.ZoneId.systemDefault())).toEpochMilli(),
            userId = domain.userId
        )
    }
}