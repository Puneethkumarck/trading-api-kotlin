package com.xchange.valr.trading.api.infrastructure.tradehistory

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

private val logger = KotlinLogging.logger {}

@Repository
class InMemoryTradeRepository {
    private val trades = ConcurrentHashMap<String, MutableList<TradeEntity>>()
    private val sequenceGenerator = AtomicLong(1300890000000000000L)

    fun save(trade: TradeEntity): TradeEntity {
        logger.info { "Saving trade: ${trade.id} for currency pair: ${trade.currencyPair}" }

        val updatedTrade =
            trade.copy(
                sequenceId = sequenceGenerator.incrementAndGet(),
                tradedAt = Instant.now(),
            )

        trades.computeIfAbsent(trade.currencyPair.uppercase()) { ArrayList() }
            .add(updatedTrade)

        logger.debug { "Saved trade: ${trade.id} for currency pair: ${trade.currencyPair}" }
        return updatedTrade
    }

    fun findRecentTradeByCurrencyPair(
        currencyPair: String,
        limit: Int,
    ): List<TradeEntity> {
        return trades.getOrDefault(currencyPair.uppercase(), ArrayList())
            .sortedByDescending { it.tradedAt }
            .take(limit)
    }

    fun findTradeByOrderId(orderId: String): TradeEntity? {
        return trades.values
            .asSequence()
            .flatten()
            .find { it.id == orderId }
    }
}
