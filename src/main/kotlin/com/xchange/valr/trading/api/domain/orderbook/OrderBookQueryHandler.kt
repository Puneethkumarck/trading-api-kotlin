package com.xchange.valr.trading.api.domain.orderbook

import com.xchange.valr.trading.api.domain.orderbook.OrderBookNotFoundException.Companion.withCurrencyPair
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class OrderBookQueryHandler(
    private val orderBookRepository: OrderBookRepository,
) {
    fun getOrderBook(currencyPair: String): OrderBook {
        logger.debug { "Finding order book for currency pair: $currencyPair" }
        return orderBookRepository.findByCurrencyPair(currencyPair)
            ?.let { orderBook ->
                orderBook.copy(
                    bids = orderBook.bids.descendingMap(),
                )
            }
            ?: throw withCurrencyPair(currencyPair)
    }
}
