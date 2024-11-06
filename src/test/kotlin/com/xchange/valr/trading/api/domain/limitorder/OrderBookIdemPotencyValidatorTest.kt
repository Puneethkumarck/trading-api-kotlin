package com.xchange.valr.trading.api.domain.limitorder

import com.xchange.valr.trading.fixtures.LimitOrderCommandFixtures.createLimitOrder
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

@ExtendWith(MockKExtension::class)
class OrderBookIdemPotencyValidatorTest {
    @MockK
    private lateinit var orderRepository: LimitOrderRepository

    @InjectMockKs
    private lateinit var validator: OrderBookIdemPotencyValidator

    @Test
    fun `should save new order`() {
        // given
        val command = createLimitOrder()
        every { orderRepository.findByOrderId(command.customerOrderId) } returns null
        every { orderRepository.save(command) } returns command

        // when
        val result = validator.validate(command)

        // then
        assertThat(result).isEqualTo(command)
    }

    @Test
    fun `should throw exception when duplicate order found`() {
        // given
        val command = createLimitOrder()
        every { orderRepository.findByOrderId(command.customerOrderId) } returns command

        // when
        val exception =
            assertThrows<LimitOrderAlreadyExistsException> {
                validator.validate(command)
            }

        // then
        assertThat(exception.message).isEqualTo("Limit order with orderId ${command.customerOrderId} already exists")
    }

    @ParameterizedTest
    @ValueSource(strings = ["", " "])
    fun `should return limit order command when orderId is blank or null`(orderId: String) {
        // given
        val command = createLimitOrder().copy(customerOrderId = orderId)
        every { orderRepository.save(command) } returns command

        // when
        val result = validator.validate(command)

        // then
        assertThat(result).isEqualTo(command)
    }
}
