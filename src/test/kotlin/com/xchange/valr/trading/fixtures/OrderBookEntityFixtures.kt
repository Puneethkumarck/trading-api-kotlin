package com.xchange.valr.trading.fixtures

import com.xchange.valr.trading.api.infrastructure.orderbook.OrderBookEntity
import java.math.BigDecimal
import java.time.Instant
import java.util.TreeMap

object OrderBookEntityFixtures {
    fun createOrderBookEntity(currencyPair: String) =
        OrderBookEntity(
            currencyPair = currencyPair,
            asks =
                TreeMap(
                    mapOf(
                        BigDecimal("1000000") to
                            OrderBookEntity.OrderBookLevelEntity(
                                side = OrderBookEntity.OrderBookSide.SELL,
                                quantity = BigDecimal("1.0"),
                                price = BigDecimal("1000000"),
                                currencyPair = currencyPair,
                                orderCount = 1,
                            ),
                    ),
                ),
            bids =
                TreeMap(
                    mapOf(
                        BigDecimal("900000") to
                            OrderBookEntity.OrderBookLevelEntity(
                                side = OrderBookEntity.OrderBookSide.BUY,
                                price = BigDecimal("900000"),
                                quantity = BigDecimal("1.0"),
                                currencyPair = currencyPair,
                                orderCount = 1,
                            ),
                    ),
                ),
            sequenceNumber = 1L,
            lastChange = Instant.now(),
        )
}
