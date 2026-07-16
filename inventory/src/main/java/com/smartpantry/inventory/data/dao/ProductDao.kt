package com.smartpantry.inventory.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.smartpantry.inventory.data.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM products ORDER BY location, expiryDate")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE id = :id")
    fun getProduct(id: Long): Flow<ProductEntity>

    @Query("SELECT * FROM products WHERE location LIKE :locationPrefix")
    fun getProductsByLocationPrefix(locationPrefix: String): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE category = :category")
    fun getProductsByCategory(category: String): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE status = :status")
    fun getProductsByStatus(status: String): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE expiryDate IS NOT NULL AND expiryDate <= :date ORDER BY expiryDate")
    fun getProductsExpiringByDate(date: String): Flow<List<ProductEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(entity: ProductEntity): Long

    @Update
    suspend fun updateProduct(entity: ProductEntity)

    @Query("UPDATE products SET status = 'CONSUMED' WHERE id = :id")
    suspend fun softDelete(id: Long)

    @Query("SELECT COUNT(*) FROM products")
    fun getProductCount(): Flow<Int>
}