package com.xchange.valr.trading.api.domain.limitorder

import com.xchange.valr.trading.api.domain.orderbook.OrderBook
import java.math.BigDecimal
import java.util.concurrent.ConcurrentNavigableMap

class SellOrderProcessor : OrderProcessor() {
    override fun getMatchingSide(orderBook: OrderBook): ConcurrentNavigableMap<BigDecimal, OrderBook.OrderBookLevel> = orderBook.bids

    override fun getPlacementSide(orderBook: OrderBook): ConcurrentNavigableMap<BigDecimal, OrderBook.OrderBookLevel> = orderBook.asks

    override fun canMatch(
        orderPrice: BigDecimal,
        matchPrice: BigDecimal,
    ): Boolean = orderPrice <= matchPrice

    override fun getOrderSide(): OrderBook.OrderBookSide = OrderBook.OrderBookSide.SELL
}
