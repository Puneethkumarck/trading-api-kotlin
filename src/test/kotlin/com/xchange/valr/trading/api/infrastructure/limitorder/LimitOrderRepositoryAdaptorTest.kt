package com.xchange.valr.trading.api.infrastructure.limitorder

import com.xchange.valr.trading.fixtures.LimitOrderCommandFixtures.createLimitOrder
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.SpyK
import io.mockk.junit5.MockKExtension
import org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class LimitOrderRepositoryAdaptorTest {
    @InjectMockKs
    private lateinit var adaptor: LimitOrderRepositoryAdaptor

    @MockK
    private lateinit var repository: InMemoryOrderRepository

    @SpyK
    private var mapper = LimitOrderEntityMapper()

    @Test
    fun `should save limit order`() {
        // given
        val command = createLimitOrder()
        val entity = mapper.toEntity(command)

        every { repository.save(ofType(LimitOrderEntity::class)) } returns entity

        // when
        val result = adaptor.save(command)

        // then
        assertThat(result)
            .usingRecursiveComparison()
            .isEqualTo(command)
    }

    @Test
    fun `should find limit order`() {
        // given
        val command = createLimitOrder()
        val entity = mapper.toEntity(command)
        val orderId = command.customerOrderId ?: randomAlphanumeric(50)

        every { repository.findByOrderId(orderId) } returns entity

        // when
        val result = adaptor.findByOrderId(orderId)

        // then
        assertThat(result)
            .usingRecursiveComparison()
            .isEqualTo(command)
    }

    @Test
    fun `should return null when order not found`() {
        // given
        val orderId = "non-existing-order-id"

        every { repository.findByOrderId(orderId) } returns null

        // when
        val result = adaptor.findByOrderId(orderId)

        // then
        assertThat(result).isNull()
    }
}
