package com.xchange.valr.trading.api.domain.orderbook

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class OrderBookQueryHandler(
    private val orderBookRepository: OrderBookRepository,
) {
    fun getOrderBook(currencyPair: String): OrderBook =
        orderBookRepository.findByCurrencyPair(currencyPair)
            ?.also { logger.info { "${"Found order book for currency pair: {}"} $currencyPair" } }
            ?: throw OrderBookNotFoundException.withCurrencyPair(currencyPair)
}
