package com.xchange.valr.trading.api.application.tradehistory

import com.xchange.valr.trading.api.domain.tradehistory.Trade
import com.xchange.valr.trading.api.model.CurrencyPair.BTCZAR
import com.xchange.valr.trading.api.model.TradeHistoryResponseDto
import com.xchange.valr.trading.fixtures.TradeFixtures.createTrade
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class TradeHistoryDtoMapperTest {
    private val mapper = TradeHistoryDtoMapperImpl()

    @Test
    fun `should map trade to dto`() {
        // given
        val trade = createTrade(BTCZAR.name, Trade.TakerSide.BUY)

        // when
        val result = mapper.toDto(trade)

        val expected =
            TradeHistoryResponseDto(
                id = trade.id,
                currencyPair = trade.currencyPair,
                takerSide = trade.takerSide,
                quantity = trade.quantity,
                price = trade.price,
                quoteVolume = trade.quoteVolume,
                tradedAt = trade.tradedAt,
                sequenceId = trade.sequenceId,
            )

        // then
        assertThat(result)
            .usingRecursiveComparison()
            .isEqualTo(expected)
    }

    @Test
    fun `should map trade list to dto list`() {
        // given
        val trades =
            listOf(
                createTrade(BTCZAR.name, Trade.TakerSide.BUY),
                createTrade(BTCZAR.name, Trade.TakerSide.SELL),
            )

        // when
        val result = mapper.toDtoList(trades)

        // then
        assertThat(result)
            .usingRecursiveComparison()
            .isEqualTo(trades.map { it.toExpectedDto() })
    }

    private fun Trade.toExpectedDto() =
        TradeHistoryResponseDto(
            id = id,
            currencyPair = currencyPair,
            takerSide = takerSide,
            quantity = quantity,
            price = price,
            quoteVolume = quoteVolume,
            tradedAt = tradedAt,
            sequenceId = sequenceId,
        )
}
