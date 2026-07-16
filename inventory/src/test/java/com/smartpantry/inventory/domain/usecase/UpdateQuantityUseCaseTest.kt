package com.smartpantry.inventory.domain.usecase

import com.smartpantry.inventory.domain.model.Movement
import com.smartpantry.inventory.domain.model.MovementType
import com.smartpantry.inventory.domain.repository.ProductRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class UpdateQuantityUseCaseTest {

    @Test
    fun `update quantity returns movement with correction type`() = runTest {
        val repository = mockk<ProductRepository>()
        val movement = Movement(
            productId = 1,
            type = MovementType.Correction(field = "quantity", oldValue = "5", newValue = "3")
        )
        coEvery { repository.updateQuantity(1L, 3) } returns movement

        val useCase = UpdateQuantityUseCase(repository)
        val result = useCase(1L, 3)

        assertEquals(MovementType.Correction::class, result.type::class)
    }
}