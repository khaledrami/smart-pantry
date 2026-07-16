package com.smartpantry.inventory.data.mapper

import com.smartpantry.inventory.data.entity.ProductEntity
import com.smartpantry.inventory.domain.model.Category
import com.smartpantry.inventory.domain.model.Product
import com.smartpantry.inventory.domain.model.Status
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

object ProductMapper {
    fun toDomain(entity: ProductEntity): Product {
        return Product(
            id = entity.id,
            name = entity.name,
            description = entity.description,
            brand = entity.brand,
            category = entity.category,
            barcode = entity.barcode,
            quantity = entity.quantity,
            unit = entity.unit,
            price = entity.price,
            purchaseDate = entity.purchaseDate?.let { toLocalDate(it) },
            openDate = entity.openDate?.let { toLocalDate(it) },
            freezeDate = entity.freezeDate?.let { toLocalDate(it) },
            bestBeforeDate = entity.bestBeforeDate?.let { toLocalDate(it) },
            expiryDate = entity.expiryDate?.let { toLocalDate(it) },
            location = entity.location,
            status = entity.status,
            notes = entity.notes,
            tags = entity.tags.split(",").filter { it.isNotBlank() },
            createdAt = toLocalDateTime(entity.createdAt),
            updatedAt = toLocalDateTime(entity.updatedAt)
        )
    }

    fun toEntity(domain: Product): ProductEntity {
        return ProductEntity(
            id = domain.id ?: 0L,
            name = domain.name,
            description = domain.description,
            brand = domain.brand,
            category = domain.category,
            barcode = domain.barcode,
            quantity = domain.quantity,
            unit = domain.unit,
            price = domain.price,
            purchaseDate = domain.purchaseDate?.let { toEpochMillis(it) },
            openDate = domain.openDate?.let { toEpochMillis(it) },
            freezeDate = domain.freezeDate?.let { toEpochMillis(it) },
            bestBeforeDate = domain.bestBeforeDate?.let { toEpochMillis(it) },
            expiryDate = domain.expiryDate?.let { toEpochMillis(it) },
            location = domain.location,
            status = domain.status,
            notes = domain.notes,
            tags = domain.tags.joinToString(","),
            createdAt = toEpochMillis(domain.createdAt),
            updatedAt = toEpochMillis(domain.updatedAt)
        )
    }

    private fun toLocalDate(epochMillis: Long): LocalDate {
        return LocalDate.ofInstant(java.time.Instant.ofEpochMilli(epochMillis), ZoneId.systemDefault())
    }

    private fun toLocalDateTime(epochMillis: Long): LocalDateTime {
        return LocalDateTime.ofInstant(java.time.Instant.ofEpochMilli(epochMillis), ZoneId.systemDefault())
    }

    private fun toEpochMillis(localDate: LocalDate): Long {
        return localDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    private fun toEpochMillis(localDateTime: LocalDateTime): Long {
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }
}