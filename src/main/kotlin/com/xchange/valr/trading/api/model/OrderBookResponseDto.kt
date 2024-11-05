package com.xchange.valr.trading.api.model

import java.math.BigDecimal
import java.time.Instant

data class OrderBookResponseDto(
    val asks: List<OrderBookEntryDto>,
    val bids: List<OrderBookEntryDto>,
    val lastChange: Instant,
    val sequenceNumber: Long,
) {
    data class OrderBookEntryDto(
        val side: String,
        val quantity: BigDecimal,
        val price: BigDecimal,
        val currencyPair: String,
        val orderCount: Int,
    )
}
