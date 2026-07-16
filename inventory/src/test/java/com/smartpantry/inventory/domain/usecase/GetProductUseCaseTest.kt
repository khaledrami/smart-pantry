package com.smartpantry.inventory.domain.usecase

import com.smartpantry.inventory.domain.model.Category
import com.smartpantry.inventory.domain.model.Movement
import com.smartpantry.inventory.domain.model.MovementType
import com.smartpantry.inventory.domain.model.Product
import com.smartpantry.inventory.domain.model.Status
import com.smartpantry.inventory.domain.repository.ProductRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class GetProductUseCaseTest {

    @Test
    fun `get product returns flow of single product`() = runTest {
        val repository = mockk<ProductRepository>()
        val product = Product(
            id = 1,
            name = "Tomate",
            category = Category.VEGETABLES,
            quantity = 5,
            unit = "kg",
            location = "Fridge/Vegetables/Drawer",
            status = Status.AVAILABLE
        )
        coEvery { repository.getProduct(1L) } returns flowOf(product)

        val useCase = GetProductUseCase(repository)
        val result = useCase(1L).first()

        assertEquals("Tomate", result.name)
        assertEquals(Category.VEGETABLES, result.category)
    }
}