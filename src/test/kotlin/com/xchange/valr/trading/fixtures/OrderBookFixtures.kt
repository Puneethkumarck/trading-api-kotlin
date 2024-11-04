package com.xchange.valr.trading.fixtures

import com.xchange.valr.trading.api.domain.orderbook.OrderBook
import java.math.BigDecimal
import java.time.Instant
import java.util.concurrent.ConcurrentSkipListMap

object OrderBookFixtures {
    fun orderBook(currencyPair: String): OrderBook =
        OrderBook(
            currencyPair = currencyPair,
            asks =
                ConcurrentSkipListMap(
                    mapOf(
                        BigDecimal("1000000") to
                            OrderBook.OrderBookLevel(
                                side = OrderBook.OrderBookSide.SELL,
                                price = BigDecimal("1000000"),
                                quantity = BigDecimal("1.0"),
                                currencyPair = currencyPair,
                                orderCount = 1,
                            ),
                    ),
                ),
            bids =
                ConcurrentSkipListMap(
                    mapOf(
                        BigDecimal("900000") to
                            OrderBook.OrderBookLevel(
                                side = OrderBook.OrderBookSide.BUY,
                                price = BigDecimal("900000"),
                                quantity = BigDecimal("1.0"),
                                currencyPair = currencyPair,
                                orderCount = 1,
                            ),
                    ),
                ),
            lastChange = Instant.now(),
            sequenceNumber = 1L,
        )

    fun createOrderBook(
        price: BigDecimal,
        quantity: BigDecimal,
        currencyPair: String,
        side: OrderBook.OrderBookSide,
    ): OrderBook {
        val orderBookLevel =
            OrderBook.OrderBookLevel(
                side = side,
                price = price,
                quantity = quantity,
                currencyPair = currencyPair,
                orderCount = 1,
            )

        return OrderBook(
            currencyPair = currencyPair,
            asks =
                if (side == OrderBook.OrderBookSide.SELL) {
                    ConcurrentSkipListMap(mapOf(price to orderBookLevel))
                } else {
                    ConcurrentSkipListMap()
                },
            bids =
                if (side == OrderBook.OrderBookSide.BUY) {
                    ConcurrentSkipListMap(mapOf(price to orderBookLevel))
                } else {
                    ConcurrentSkipListMap()
                },
        )
    }
}
