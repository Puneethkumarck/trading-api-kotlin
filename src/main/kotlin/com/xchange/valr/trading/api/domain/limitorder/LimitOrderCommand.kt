package com.xchange.valr.trading.api.domain.limitorder

import java.math.BigDecimal

data class LimitOrderCommand(
    val limitOrder: LimitOrder,
    val customerOrderId: String?,
) {
    val currencyPair: String
        get() = limitOrder.currencyPair

    data class LimitOrder(
        val side: OrderBookSide,
        val quantity: BigDecimal,
        val price: BigDecimal,
        val currencyPair: String,
        val status: OrderStatus?,
    ) {
        enum class OrderBookSide(val side: String) {
            SELL("SELL"),
            BUY("BUY"),
        }

        enum class OrderStatus(val status: String) {
            OPEN("OPEN"),
            FILLED("FILLED"),
            CANCELLED("CANCELLED"),
        }
    }
}
