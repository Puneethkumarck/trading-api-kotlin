package com.xchange.valr.trading.api.infrastructure.tradehistory

import java.math.BigDecimal
import java.time.Instant

data class TradeEntity(
    val id: String,
    val currencyPair: String,
    val takerSide: String,
    val quantity: BigDecimal,
    val price: BigDecimal,
    val quoteVolume: BigDecimal,
    val tradedAt: Instant,
    val sequenceId: Long,
)
