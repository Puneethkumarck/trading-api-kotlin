package com.xchange.valr.trading.api.infrastructure.orderbook

import java.math.BigDecimal
import java.time.Instant
import java.util.TreeMap

data class OrderBookEntity(
    val currencyPair: String,
    val asks: TreeMap<BigDecimal, OrderBookLevelEntity>,
    val bids: TreeMap<BigDecimal, OrderBookLevelEntity>,
    val sequenceNumber: Long = 0,
    val lastChange: Instant? = null,
) {
    data class OrderBookLevelEntity(
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
