package com.smartpantry.inventory.domain.usecase

import com.smartpantry.inventory.domain.model.Category
import com.smartpantry.inventory.domain.model.Movement
import com.smartpantry.inventory.domain.model.MovementType
import com.smartpantry.inventory.domain.model.Product
import com.smartpantry.inventory.domain.model.Status
import com.smartpantry.inventory.domain.repository.ProductRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class AddProductUseCaseTest {

    @Test
    fun `add product returns id`() = runTest {
        val repository = mockk<ProductRepository>()
        val product = Product(
            name = "Tomate",
            category = Category.VEGETABLES,
            quantity = 5,
            unit = "kg",
            location = "Fridge/Vegetables/Drawer",
            status = Status.AVAILABLE
        )
        coEvery { repository.addProduct(product) } returns 1L

        val useCase = AddProductUseCase(repository)
        val result = useCase(product)

        assertEquals(1L, result)
    }
}