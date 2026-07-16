package com.smartpantry.inventory.data.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.smartpantry.inventory.data.AppDatabase
import com.smartpantry.inventory.data.entity.MovementEntity
import com.smartpantry.inventory.data.entity.ProductEntity
import com.smartpantry.inventory.domain.model.Category
import com.smartpantry.inventory.domain.model.Status
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class MovementDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var productDao: ProductDao
    private lateinit var movementDao: MovementDao

    @BeforeEach
    fun setup() {
        val context = ApplicationProvider.getApplicationContext()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        productDao = db.productDao()
        movementDao = db.movementDao()
    }

    @AfterEach
    fun teardown() {
        db.close()
    }

    @Test
    fun `insert and get movements by product`() = runTest {
        val productEntity = ProductEntity(
            name = "Tomate",
            category = Category.VEGETABLES,
            quantity = 5,
            unit = "kg",
            location = "Fridge/Vegetables/Drawer",
            status = Status.AVAILABLE
        )
        val productId = productDao.insertProduct(productEntity)

        val movement = MovementEntity(
            productId = productId,
            type = "Entry",
            typePayload = """{"type":"Entry","oldQuantity":null,"newQuantity":5}"""
        )

        val movementId = movementDao.insertMovement(movement)
        assertNotNull(movementId)
        assertEquals(1L, movementId)

        val movements = movementDao.getMovements(productId).first()
        assertEquals(1, movements.size)
        assertEquals("Entry", movements[0].type)
    }
}