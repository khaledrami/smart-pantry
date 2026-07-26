package com.smartpantry.app

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.smartpantry.inventory.data.AppDatabase
import com.smartpantry.inventory.data.dao.ProductDao
import com.smartpantry.inventory.data.entity.ProductEntity
import com.smartpantry.inventory.domain.model.Category
import com.smartpantry.inventory.domain.model.Status
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class InventoryIntegrationTest {

    private lateinit var db: AppDatabase
    private lateinit var productDao: ProductDao

    @Before
    fun setup() {
        val context: Context = ApplicationProvider.getApplicationContext()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        productDao = db.productDao()
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun insert_and_retrieve_product_through_room() = runBlocking {
        val entity = ProductEntity(
            name = "Test Product",
            category = Category.VEGETABLES,
            quantity = 10,
            unit = "kg",
            location = "Fridge/Vegetables/Drawer",
            status = Status.AVAILABLE
        )

        val id = productDao.insertProduct(entity)
        assertNotNull(id)

        val product = productDao.getProduct(id).first()
        assertEquals("Test Product", product.name)
        assertEquals(Category.VEGETABLES, product.category)
        assertEquals(10, product.quantity)
    }
}