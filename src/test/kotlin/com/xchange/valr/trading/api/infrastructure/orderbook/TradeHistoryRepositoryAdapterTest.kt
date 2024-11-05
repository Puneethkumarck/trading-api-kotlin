package com.xchange.valr.trading.api.infrastructure.orderbook

import com.xchange.valr.trading.api.domain.tradehistory.Trade
import com.xchange.valr.trading.api.infrastructure.tradehistory.InMemoryTradeRepository
import com.xchange.valr.trading.api.infrastructure.tradehistory.TradeEntity
import com.xchange.valr.trading.api.infrastructure.tradehistory.TradeHistoryEntityMapper
import com.xchange.valr.trading.api.infrastructure.tradehistory.TradeHistoryEntityMapperImpl
import com.xchange.valr.trading.api.model.CurrencyPair.BTCZAR
import com.xchange.valr.trading.fixtures.TradeFixtures.createTrade
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.SpyK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class TradeHistoryRepositoryAdapterTest {

    @MockK
    private lateinit var repository: InMemoryTradeRepository

    @SpyK
    private var mapper: TradeHistoryEntityMapper = TradeHistoryEntityMapperImpl()

    @InjectMockKs
    private lateinit var adapter: TradeHistoryRepositoryAdapter

    @Test
    fun `should save trade`() {
        // given
        val trade = createTrade(BTCZAR.name, Trade.TakerSide.BUY)

        val entity =
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

        every { repository.save(entity) } returns entity

        // when
        val result = adapter.save(trade)

        // then
        assertThat(result)
            .usingRecursiveComparison()
            .isEqualTo(entity)
    }

    @Test
    fun `should find recent trade`() {
        // given
        val trade = createTrade(BTCZAR.name, Trade.TakerSide.BUY)
        val entity =
            TradeEntity(
                id = trade.id,
                currencyPair = trade.currencyPair,
                takerSide = trade.takerSide,
                price = trade.price,
                quantity = trade.quantity,
                quoteVolume = trade.quoteVolume,
                sequenceId = trade.sequenceId,
                tradedAt = trade.tradedAt,
            )
        every { repository.findRecentTradeByCurrencyPair(BTCZAR.name, 1) } returns listOf(entity)

        // when
        val result = adapter.findRecentTradesByCurrencyPair(BTCZAR.name, 1)

        // then
        assertThat(result)
            .hasSize(1)
            .first()
            .usingRecursiveComparison()
            .isEqualTo(trade)
    }

    @Test
    fun `should find trade by order id`() {
        // given
        val trade = createTrade(BTCZAR.name, Trade.TakerSide.BUY)
        val entity =
            TradeEntity(
                id = trade.id,
                currencyPair = trade.currencyPair,
                takerSide = trade.takerSide,
                price = trade.price,
                quantity = trade.quantity,
                quoteVolume = trade.quoteVolume,
                sequenceId = trade.sequenceId,
                tradedAt = trade.tradedAt,
            )
        every { repository.findTradeByOrderId(trade.id) } returns entity

        // when
        val result = adapter.findTradeByOrderId(trade.id)

        // then
        assertThat(result)
            .usingRecursiveComparison()
            .isEqualTo(trade)
    }
}
