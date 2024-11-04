package com.xchange.valr.trading.api.infrastructure.orderbook

import org.springframework.stereotype.Repository
import java.time.Instant.now
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

@Repository
class InMemoryOrderBookRepository {
    private val orderBooks = ConcurrentHashMap<String, OrderBookEntity>()
    private val sequenceNumber = AtomicLong(0)

    fun findByCurrencyPair(currencyPair: String): OrderBookEntity? {
        return orderBooks[currencyPair]
    }

    fun save(orderBookEntity: OrderBookEntity): OrderBookEntity {
        val updatedEntity =
            orderBookEntity.copy(
                sequenceNumber = sequenceNumber.incrementAndGet(),
                lastChange = now(),
            )
        orderBooks[orderBookEntity.currencyPair] = updatedEntity
        return updatedEntity
    }
}
