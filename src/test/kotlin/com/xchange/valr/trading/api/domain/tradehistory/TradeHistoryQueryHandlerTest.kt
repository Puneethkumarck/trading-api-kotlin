package com.xchange.valr.trading.api.domain.tradehistory

import com.xchange.valr.trading.api.domain.tradehistory.Trade.TakerSide.BUY
import com.xchange.valr.trading.api.domain.tradehistory.Trade.TakerSide.SELL
import com.xchange.valr.trading.api.model.CurrencyPair.BTCZAR
import com.xchange.valr.trading.fixtures.TradeFixtures.createTrade
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class TradeHistoryQueryHandlerTest {
    @InjectMockKs
    private lateinit var handler: TradeHistoryQueryHandler

    @MockK
    private lateinit var repository: TradeHistoryRepository

    @Test
    fun `should get trade history`() {
        // given
        val trades =
            listOf(
                createTrade("BTCZAR", SELL),
                createTrade("BTCZAR", BUY),
            )

        every {
            repository.findRecentTradesByCurrencyPair(BTCZAR.name, 10)
        } returns trades

        // when
        val result = handler.getTradeHistory(BTCZAR.name, 10)

        // then
        assertThat(result)
            .usingRecursiveAssertion()
            .isEqualTo(trades)
    }

    @Test
    fun `should return empty list when no trades found`() {
        // given
        every {
            repository.findRecentTradesByCurrencyPair(BTCZAR.name, 10)
        } returns emptyList()

        // when
        val result = handler.getTradeHistory(BTCZAR.name, 10)

        // then
        assertThat(result).isEmpty()
    }
}
