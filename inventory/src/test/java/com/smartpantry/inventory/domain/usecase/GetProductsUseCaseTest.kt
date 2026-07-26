package com.smartpantry.inventory.domain.usecase

import com.smartpantry.inventory.domain.model.Category
import com.smartpantry.inventory.domain.model.Movement
import com.smartpantry.inventory.domain.model.MovementType
import com.smartpantry.inventory.domain.model.Product
import com.smartpantry.inventory.domain.model.Status
import com.smartpantry.inventory.domain.repository.ProductRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class GetProductsUseCaseTest {

    @Test
    fun `get products returns flow of products`() = runTest {
        val repository = mockk<ProductRepository>()
        val product1 = Product(
            id = 1,
            name = "Tomate",
            category = Category.VEGETABLES,
            quantity = 5,
            unit = "kg",
            location = "Fridge/Vegetables/Drawer",
            status = Status.AVAILABLE
        )
        val product2 = Product(
            id = 2,
            name = "Pollo",
            category = Category.MEAT,
            quantity = 2,
            unit = "kg",
            location = "Freezer/Meat/Drawer",
            status = Status.FROZEN
        )
        coEvery { repository.getAllProducts() } returns flowOf(listOf(product1, product2))

        val useCase = GetProductsUseCase(repository)
        val result = useCase().first()

        assertEquals(2, result.size)
        assertEquals("Tomate", result[0].name)
        assertEquals("Pollo", result[1].name)
    }
}