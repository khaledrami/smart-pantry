package com.smartpantry.inventory.data.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.smartpantry.inventory.data.AppDatabase
import com.smartpantry.inventory.data.entity.ProductEntity
import com.smartpantry.inventory.domain.model.Category
import com.smartpantry.inventory.domain.model.Status
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ProductDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: ProductDao

    @BeforeEach
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<android.app.Application>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.productDao()
    }

    @AfterEach
    fun teardown() {
        db.close()
    }

    @Test
    fun `insert and get product`() = runTest {
        val entity = ProductEntity(
            name = "Tomate",
            category = Category.VEGETABLES,
            quantity = 5,
            unit = "kg",
            location = "Fridge/Vegetables/Drawer",
            status = Status.AVAILABLE
        )

        val id = dao.insertProduct(entity)
        assertNotNull(id)
        assertEquals(1L, id)

        val product = dao.getProduct(id).first()
        assertEquals("Tomate", product.name)
        assertEquals(Category.VEGETABLES, product.category)
        assertEquals(5, product.quantity)
    }

    @Test
    fun `get all products returns flow`() = runTest {
        val entity1 = ProductEntity(
            name = "Tomate",
            category = Category.VEGETABLES,
            quantity = 5,
            unit = "kg",
            location = "Fridge/Vegetables/Drawer",
            status = Status.AVAILABLE
        )
        val entity2 = ProductEntity(
            name = "Pollo",
            category = Category.MEAT,
            quantity = 2,
            unit = "kg",
            location = "Freezer/Meat/Drawer",
            status = Status.FROZEN
        )

        dao.insertProduct(entity1)
        dao.insertProduct(entity2)

        val products = dao.getAllProducts().first()
        assertEquals(2, products.size)
    }

    @Test
    fun `soft delete updates status to consumed`() = runTest {
        val entity = ProductEntity(
            name = "Tomate",
            category = Category.VEGETABLES,
            quantity = 5,
            unit = "kg",
            location = "Fridge/Vegetables/Drawer",
            status = Status.AVAILABLE
        )

        val id = dao.insertProduct(entity)
        dao.softDelete(id)

        val product = dao.getProduct(id).first()
        assertEquals(Status.CONSUMED, product.status)
    }
}