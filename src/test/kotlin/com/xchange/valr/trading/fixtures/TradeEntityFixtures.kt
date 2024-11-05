package com.xchange.valr.trading.fixtures

import com.xchange.valr.trading.api.infrastructure.tradehistory.TradeEntity
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID.randomUUID

object TradeEntityFixtures {
    fun createTradeEntity(
        currencyPair: String,
        takerSide: String,
    ): TradeEntity =
        TradeEntity(
            id = randomUUID().toString(),
            currencyPair = currencyPair,
            takerSide = takerSide,
            price = BigDecimal("1000000"),
            quantity = BigDecimal("1.0"),
            quoteVolume = BigDecimal("1000000"),
            tradedAt = Instant.now(),
            sequenceId = 0L,
        )

    fun createTradeEntity(
        currencyPair: String,
        takerSide: String,
        quantity: BigDecimal,
        price: BigDecimal,
    ): TradeEntity =
        TradeEntity(
            id = randomUUID().toString(),
            currencyPair = currencyPair,
            takerSide = takerSide,
            quantity = quantity,
            price = price,
            quoteVolume = price.multiply(quantity),
            tradedAt = Instant.now(),
            sequenceId = 0L,
        )
}
