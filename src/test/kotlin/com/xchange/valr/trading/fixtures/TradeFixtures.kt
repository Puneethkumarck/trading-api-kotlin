package com.xchange.valr.trading.fixtures

import com.xchange.valr.trading.api.domain.tradehistory.Trade
import java.math.BigDecimal
import java.math.BigDecimal.ONE
import java.time.Instant
import java.util.UUID

object TradeFixtures {
    fun createTrade(
        currencyPair: String,
        takerSide: Trade.TakerSide,
    ): Trade =
        Trade(
            id = UUID.randomUUID().toString(),
            currencyPair = currencyPair,
            takerSide = takerSide.side,
            quantity = ONE,
            price = BigDecimal("1000000"),
            quoteVolume = BigDecimal("1000000"),
            tradedAt = Instant.now(),
            sequenceId = 1300890000000000001L,
        )
}
