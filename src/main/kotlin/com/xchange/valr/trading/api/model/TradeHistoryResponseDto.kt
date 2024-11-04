package com.xchange.valr.trading.api.model

import java.math.BigDecimal
import java.time.Instant

data class TradeHistoryResponseDto(
    val price: BigDecimal,
    val quantity: BigDecimal,
    val currencyPair: String,
    val tradedAt: Instant,
    val takerSide: String,
    val sequenceId: Long,
    val id: String,
    val quoteVolume: BigDecimal,
)
