package com.xchange.valr.trading.api.domain.limitorder

import com.xchange.valr.trading.api.domain.orderbook.OrderBook
import java.math.BigDecimal
import java.util.concurrent.ConcurrentNavigableMap

abstract class OrderProcessor {
    abstract fun getMatchingSide(orderBook: OrderBook): ConcurrentNavigableMap<BigDecimal, OrderBook.OrderBookLevel>

    abstract fun getPlacementSide(orderBook: OrderBook): ConcurrentNavigableMap<BigDecimal, OrderBook.OrderBookLevel>

    abstract fun canMatch(
        orderPrice: BigDecimal,
        matchPrice: BigDecimal,
    ): Boolean

    abstract fun getOrderSide(): OrderBook.OrderBookSide

    fun processOrder(
        command: LimitOrderCommand,
        orderBook: OrderBook,
        executor: OrderExecutor,
    ) {
        var remainingQuantity = command.limitOrder.quantity
        val price = command.limitOrder.price

        // Match orders
        remainingQuantity = matchOrders(command, orderBook, remainingQuantity, executor)

        // Place remaining quantity if any
        if (remainingQuantity > BigDecimal.ZERO) {
            placeOrder(command.currencyPair, price, remainingQuantity, getPlacementSide(orderBook))
        }
    }

    private fun matchOrders(
        command: LimitOrderCommand,
        orderBook: OrderBook,
        remainingQuantity: BigDecimal,
        executor: OrderExecutor,
    ): BigDecimal {
        var currentQuantity = remainingQuantity
        val matchingSide = getMatchingSide(orderBook)

        for ((matchPrice, matchLevel) in matchingSide) {
            if (!canMatch(command.limitOrder.price, matchPrice)) {
                break
            }

            if (currentQuantity > BigDecimal.ZERO) {
                val matchedQuantity = currentQuantity.min(matchLevel.quantity)
                currentQuantity = currentQuantity.subtract(matchedQuantity)

                // Execute trade
                executor.executeTrade(command, matchPrice, matchedQuantity)

                // Update match level
                updateMatchLevel(matchingSide, matchPrice, matchLevel, matchedQuantity)
            }
        }

        return currentQuantity
    }

    private fun updateMatchLevel(
        side: ConcurrentNavigableMap<BigDecimal, OrderBook.OrderBookLevel>,
        price: BigDecimal,
        level: OrderBook.OrderBookLevel,
        matchedQuantity: BigDecimal,
    ) {
        val updatedQuantity = level.quantity.subtract(matchedQuantity)
        if (updatedQuantity > BigDecimal.ZERO) {
            side[price] = level.copy(quantity = updatedQuantity)
        } else {
            side.remove(price)
        }
    }

    private fun placeOrder(
        currencyPair: String,
        price: BigDecimal,
        quantity: BigDecimal,
        side: ConcurrentNavigableMap<BigDecimal, OrderBook.OrderBookLevel>,
    ) {
        side[price] = side[price]?.let { existingLevel ->
            existingLevel.copy(
                quantity = existingLevel.quantity.add(quantity),
                orderCount = existingLevel.orderCount + 1,
            )
        } ?: OrderBook.OrderBookLevel(
            side = getOrderSide(),
            price = price,
            quantity = quantity,
            currencyPair = currencyPair,
            orderCount = 1,
        )
    }
}
