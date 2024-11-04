package com.xchange.valr.trading.api.infrastructure.orderbook

import com.xchange.valr.trading.api.model.CurrencyPair.BTCZAR
import com.xchange.valr.trading.fixtures.OrderBookEntityFixtures.createOrderBookEntity
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class InMemoryOrderBookRepositoryTest {
    private val repository = InMemoryOrderBookRepository()

    @Test
    fun `should save and retrieve order book`() {
        // given
        val entity = createOrderBookEntity(BTCZAR.name)

        // when
        val savedEntity = repository.save(entity)
        val retrievedEntity = repository.findByCurrencyPair("BTCZAR")

        // then
        assertThat(retrievedEntity)
            .usingRecursiveComparison()
            .isEqualTo(savedEntity)
    }

    @Test
    fun `should return null when order book not found`() {
        // when
        val retrievedEntity = repository.findByCurrencyPair(BTCZAR.name)

        // then
        assertThat(retrievedEntity).isNull()
    }
}
