package com.xchange.valr.trading.api.domain.orderbook

import java.math.BigDecimal
import java.time.Instant
import java.util.concurrent.ConcurrentNavigableMap

data class OrderBook(
    val currencyPair: String,
    val asks: ConcurrentNavigableMap<BigDecimal, OrderBookLevel>,
    val bids: ConcurrentNavigableMap<BigDecimal, OrderBookLevel>,
    val lastChange: Instant? = null,
    val sequenceNumber: Long? = null,
) {
    data class OrderBookLevel(
        val side: OrderBookSide,
        val quantity: BigDecimal,
        val price: BigDecimal,
        val currencyPair: String,
        val orderCount: Int,
    )

    enum class OrderBookSide(val side: String) {
        SELL("SELL"),
        BUY("BUY"),
    }
}
