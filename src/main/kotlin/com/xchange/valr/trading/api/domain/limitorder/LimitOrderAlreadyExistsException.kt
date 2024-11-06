package com.xchange.valr.trading.api.domain.limitorder

class LimitOrderAlreadyExistsException(message: String) : RuntimeException(message) {
    companion object {
        fun withOrderId(orderId: String) = LimitOrderAlreadyExistsException("Limit order with orderId $orderId already exists")
    }
}
