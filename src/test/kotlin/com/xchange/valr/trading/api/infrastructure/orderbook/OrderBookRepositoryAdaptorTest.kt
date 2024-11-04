package com.xchange.valr.trading.api.infrastructure.orderbook

import com.xchange.valr.trading.api.model.CurrencyPair.BTCZAR
import com.xchange.valr.trading.fixtures.OrderBookEntityFixtures.createOrderBookEntity
import com.xchange.valr.trading.fixtures.OrderBookFixtures
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class OrderBookRepositoryAdaptorTest {

    @MockK(relaxed = true)
    private lateinit var inMemoryOrderBookRepository: InMemoryOrderBookRepository

    @InjectMockKs
    private lateinit var adaptor: OrderBookRepositoryAdaptor

    private val mapper = OrderBookEntityMapperImpl()

    @Test
    fun `should get order book for given currency pair`() {
        // given
        val orderBookEntity = createOrderBookEntity(BTCZAR.name)
        val orderBook = OrderBookFixtures.orderBook(BTCZAR.name)

        every { inMemoryOrderBookRepository.findByCurrencyPair(BTCZAR.name) } returns orderBookEntity

        // when
        val result = adaptor.findByCurrencyPair(BTCZAR.name)

        // then
        assertThat(result)
            .usingRecursiveComparison()
            .ignoringFields("lastChange")
            .isEqualTo(orderBook)
    }

    @Test
    fun `should return null when order book not found for given currency pair`() {
        // given
        every { inMemoryOrderBookRepository.findByCurrencyPair(BTCZAR.name) } returns null

        // when
        val result = adaptor.findByCurrencyPair(BTCZAR.name)

        // then
        assertThat(result).isNull()
    }
}
