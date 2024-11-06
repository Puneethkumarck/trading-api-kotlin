package com.xchange.valr.trading.api.infrastructure.limitorder

import java.math.BigDecimal
import java.time.Instant

data class LimitOrderEntity(
    val orderId: String,
    val customerOrderId: String,
    val currencyPair: String,
    val side: OrderSide,
    val quantity: BigDecimal,
    val price: BigDecimal,
    val status: OrderStatus,
    val createdAt: Instant,
) {
    enum class OrderSide(val side: String) {
        SELL("SELL"),
        BUY("BUY"),
    }

    enum class OrderStatus(val status: String) {
        OPEN("OPEN"),
        FILLED("FILLED"),
        CANCELLED("CANCELLED"),
    }
}
