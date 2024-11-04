package com.xchange.valr.trading.api.infrastructure.orderbook

import com.xchange.valr.trading.api.domain.orderbook.OrderBook
import com.xchange.valr.trading.api.domain.orderbook.OrderBookRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component

private val logger = KotlinLogging.logger {}

@Component
class OrderBookRepositoryAdaptor(
    private val inMemoryOrderBookRepository: InMemoryOrderBookRepository,
    private val mapper: OrderBookEntityMapper,
) : OrderBookRepository {
    override fun findByCurrencyPair(currencyPair: String): OrderBook? {
        logger.debug { "Finding order book for currency pair: $currencyPair" }
        return inMemoryOrderBookRepository.findByCurrencyPair(currencyPair)?.let { mapper.toDomain(it) }
    }

    override fun save(orderBook: OrderBook): OrderBook? {
        logger.debug { "Saving order book for currency pair: ${orderBook.currencyPair}" }
        return inMemoryOrderBookRepository.save(mapper.toEntity(orderBook)).let { mapper.toDomain(it) }
    }
}
