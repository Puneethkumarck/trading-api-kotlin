package com.xchange.valr.trading.api.infrastructure.tradehistory

import com.xchange.valr.trading.api.domain.tradehistory.Trade
import com.xchange.valr.trading.api.model.CurrencyPair
import com.xchange.valr.trading.fixtures.TradeEntityFixtures.createTradeEntity
import com.xchange.valr.trading.fixtures.TradeFixtures.createTrade
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class TradeHistoryEntityMapperTest {
    private val mapper = TradeHistoryEntityMapperImpl()

    @Test
    fun `should map domain to entity`() {
        // given
        val trade = createTrade(CurrencyPair.BTCZAR.name, Trade.TakerSide.BUY)

        // when
        val result = mapper.toEntity(trade)

        // then
        val expected =
            TradeEntity(
                id = trade.id,
                currencyPair = trade.currencyPair,
                takerSide = trade.takerSide,
                quantity = trade.quantity,
                price = trade.price,
                quoteVolume = trade.quoteVolume,
                tradedAt = trade.tradedAt,
                sequenceId = trade.sequenceId,
            )

        assertThat(result)
            .usingRecursiveComparison()
            .isEqualTo(expected)
    }

    @Test
    fun `should map entity to domain`() {
        // given
        val entity = createTradeEntity(CurrencyPair.BTCZAR.name, "buy")

        // when
        val result = mapper.toDomain(entity)

        // then
        val expected =
            Trade(
                id = entity.id,
                currencyPair = entity.currencyPair,
                takerSide = entity.takerSide,
                quantity = entity.quantity,
                price = entity.price,
                quoteVolume = entity.quoteVolume,
                tradedAt = entity.tradedAt,
                sequenceId = entity.sequenceId,
            )

        assertThat(result)
            .usingRecursiveComparison()
            .isEqualTo(expected)
    }

    @Test
    fun `should map entity list to domain list`() {
        // given
        val entityList =
            listOf(
                createTradeEntity(CurrencyPair.BTCZAR.name, "buy"),
                createTradeEntity(CurrencyPair.BTCZAR.name, "sell"),
            )

        // when
        val result = mapper.toDomainList(entityList)

        // then
        val expected =
            entityList.map {
                Trade(
                    id = it.id,
                    currencyPair = it.currencyPair,
                    takerSide = it.takerSide,
                    quantity = it.quantity,
                    price = it.price,
                    quoteVolume = it.quoteVolume,
                    tradedAt = it.tradedAt,
                    sequenceId = it.sequenceId,
                )
            }

        assertThat(result)
            .usingRecursiveComparison()
            .isEqualTo(expected)
    }
}
