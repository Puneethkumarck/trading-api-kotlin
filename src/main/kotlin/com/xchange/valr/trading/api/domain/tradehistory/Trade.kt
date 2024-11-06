package com.xchange.valr.trading.api.domain.tradehistory

import java.math.BigDecimal
import java.time.Instant

data class Trade(
    val id: String,
    val currencyPair: String,
    val takerSide: String,
    val quantity: BigDecimal,
    val price: BigDecimal,
    val quoteVolume: BigDecimal,
    val tradedAt: Instant,
    val sequenceId: Long?,
) {
    enum class TakerSide(val side: String) {
        BUY("buy"),
        SELL("sell"),
    }
}
