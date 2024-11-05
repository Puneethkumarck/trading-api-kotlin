package com.xchange.valr.trading.api.domain.orderbook

import com.xchange.valr.trading.api.model.CurrencyPair.BTCZAR
import com.xchange.valr.trading.fixtures.OrderBookFixtures.orderBook
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class OrderBookQueryHandlerTest {
    @MockK
    private lateinit var orderBookRepository: OrderBookRepository

    @InjectMockKs
    private lateinit var handler: OrderBookQueryHandler

    @Test
    fun `should return order book for valid currency pair`() {
        // given
        val orderBook = orderBook(BTCZAR.name)
        every { orderBookRepository.findByCurrencyPair(BTCZAR.name) } returns orderBook

        // when
        val result = handler.getOrderBook(BTCZAR.name)

        // then
        assertThat(result)
            .usingRecursiveComparison()
            .isEqualTo(orderBook)
    }

    @Test
    fun `should throw exception when order book not found`() {
        // given
        every { orderBookRepository.findByCurrencyPair(BTCZAR.name) } returns null

        // when
        val exception =
            assertThrows<OrderBookNotFoundException> {
                handler.getOrderBook(BTCZAR.name)
            }

        // then
        assertThat(exception.message)
            .isEqualTo("Order book not found for currency pair: ${BTCZAR.name}")
    }
}
