package com.xchange.valr.trading.api.domain.orderbook

class OrderBookNotFoundException(message: String) : RuntimeException(message) {
    companion object {
        fun withCurrencyPair(currencyPair: String): OrderBookNotFoundException =
            OrderBookNotFoundException("Order book not found for currency pair: $currencyPair")
    }
}
