package com.xchange.valr.trading.api.infrastructure.limitorder

import com.xchange.valr.trading.api.model.CurrencyPair
import com.xchange.valr.trading.fixtures.LimitOrderEntityFixtures.createLimitOrderEntity
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class InMemoryOrderRepositoryTest {
    private val orderRepository = InMemoryOrderRepository()

    @Test
    fun `should save and retrieve order`() {
        // given
        val order = createLimitOrderEntity(CurrencyPair.BTCZAR.name, LimitOrderEntity.OrderSide.BUY)

        // when
        val savedOrder = orderRepository.save(order)
        val retrievedOrder = orderRepository.findByOrderId(savedOrder.orderId)

        // then
        assertThat(retrievedOrder)
            .usingRecursiveComparison()
            .isEqualTo(savedOrder)
    }

    @Test
    fun `should return null when order not found`() {
        // given
        val orderId = "non-existing-order-id"

        // when
        val retrievedOrder = orderRepository.findByOrderId(orderId)

        // then
        assertThat(retrievedOrder).isNull()
    }
}
