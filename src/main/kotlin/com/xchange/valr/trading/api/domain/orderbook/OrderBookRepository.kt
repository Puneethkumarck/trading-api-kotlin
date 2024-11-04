package com.xchange.valr.trading.api.domain.orderbook

interface OrderBookRepository {
    fun findByCurrencyPair(currencyPair: String): OrderBook?

    fun save(orderBook: OrderBook): OrderBook?
}
