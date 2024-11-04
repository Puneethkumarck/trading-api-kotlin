package com.xchange.valr.trading.api.infrastructure.orderbook

import com.xchange.valr.trading.api.model.CurrencyPair.BTCZAR
import com.xchange.valr.trading.fixtures.OrderBookEntityFixtures.createOrderBookEntity
import com.xchange.valr.trading.fixtures.OrderBookFixtures.orderBook
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class OrderBookEntityMapperTest {
    private val mapper = OrderBookEntityMapperImpl()

    @Test
    fun toDomain() {
        // given
        val orderBookEntity = createOrderBookEntity(BTCZAR.name)

        // when
        val result = mapper.toDomain(orderBookEntity)
        val expected = orderBook(BTCZAR.name)

        // then
        assertThat(result)
            .usingRecursiveComparison()
            .ignoringFields("lastChange")
            .isEqualTo(expected)
    }

    @Test
    fun toEntity() {
        // given
        val orderBook = orderBook(BTCZAR.name)

        // when
        val result = mapper.toEntity(orderBook)
        val expected = createOrderBookEntity(BTCZAR.name)

        // then
        assertThat(result)
            .usingRecursiveComparison()
            .ignoringFields("lastChange")
            .isEqualTo(expected)
    }
}
