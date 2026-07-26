package com.smartpantry.inventory.data.repository

import com.smartpantry.inventory.data.dao.MovementDao
import com.smartpantry.inventory.data.dao.ProductDao
import com.smartpantry.inventory.data.mapper.MovementMapper
import com.smartpantry.inventory.data.mapper.ProductMapper
import com.smartpantry.inventory.domain.model.Movement
import com.smartpantry.inventory.domain.model.Product
import com.smartpantry.inventory.domain.model.Status
import com.smartpantry.inventory.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepositoryImpl @Inject constructor(
    private val productDao: ProductDao,
    private val movementDao: MovementDao
) : ProductRepository {

    override fun getAllProducts(): Flow<List<Product>> =
        productDao.getAllProducts().map { entities -> entities.map(ProductMapper::toDomain) }

    override fun getProduct(id: Long): Flow<Product> =
        productDao.getProduct(id).map(ProductMapper::toDomain)

    override suspend fun addProduct(product: Product): Long {
        val entity = ProductMapper.toEntity(product.copy(createdAt = java.time.LocalDateTime.now(), updatedAt = java.time.LocalDateTime.now()))
        val id = productDao.insertProduct(entity)
        movementDao.insertMovement(MovementMapper.toEntity(
            Movement(productId = id, type = com.smartpantry.inventory.domain.model.MovementType.Entry(oldQuantity = null, newQuantity = product.quantity))
        ))
        return id
    }

    override suspend fun updateProduct(product: Product) {
        val entity = ProductMapper.toEntity(product.copy(updatedAt = java.time.LocalDateTime.now()))
        productDao.updateProduct(entity)
    }

    override suspend fun deleteProduct(id: Long) {
        productDao.softDelete(id)
        movementDao.insertMovement(MovementMapper.toEntity(
            Movement(productId = id, type = com.smartpantry.inventory.domain.model.MovementType.Exit(oldQuantity = 0, newQuantity = null))
        ))
    }

    override suspend fun moveProduct(id: Long, newLocation: String) {
        val product = getProduct(id).first()
        val oldLocation = product.location
        val updatedProduct = product.copy(location = newLocation, updatedAt = java.time.LocalDateTime.now())
        updateProduct(updatedProduct)
        movementDao.insertMovement(MovementMapper.toEntity(
            Movement(productId = id, type = com.smartpantry.inventory.domain.model.MovementType.LocationChange(from = oldLocation, to = newLocation))
        ))
    }

    override suspend fun updateQuantity(id: Long, newQuantity: Int): Movement {
        val product = getProduct(id).first()
        val oldQuantity = product.quantity
        val updatedProduct = product.copy(quantity = newQuantity, updatedAt = java.time.LocalDateTime.now())
        updateProduct(updatedProduct)
        val movement = Movement(
            productId = id,
            type = com.smartpantry.inventory.domain.model.MovementType.Correction(field = "quantity", oldValue = oldQuantity.toString(), newValue = newQuantity.toString())
        )
        movementDao.insertMovement(MovementMapper.toEntity(movement))
        return movement
    }

    override suspend fun updateStatus(id: Long, newStatus: Status): Movement {
        val product = getProduct(id).first()
        val oldStatus = product.status
        val updatedProduct = product.copy(status = newStatus, updatedAt = java.time.LocalDateTime.now())
        updateProduct(updatedProduct)
        val movement = Movement(
            productId = id,
            type = com.smartpantry.inventory.domain.model.MovementType.Correction(field = "status", oldValue = oldStatus.name, newValue = newStatus.name)
        )
        movementDao.insertMovement(MovementMapper.toEntity(movement))
        return movement
    }
}
