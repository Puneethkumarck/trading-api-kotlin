package com.xchange.valr.trading.api.infrastructure.orderbook

import com.xchange.valr.trading.api.domain.tradehistory.Trade
import com.xchange.valr.trading.api.domain.tradehistory.TradeHistoryRepository
import com.xchange.valr.trading.api.infrastructure.tradehistory.InMemoryTradeRepository
import com.xchange.valr.trading.api.infrastructure.tradehistory.TradeHistoryEntityMapper
import org.springframework.stereotype.Component

@Component
class TradeHistoryRepositoryAdapter(
    private val repository: InMemoryTradeRepository,
    private val mapper: TradeHistoryEntityMapper,
) : TradeHistoryRepository {
    override fun save(trade: Trade): Trade {
        return repository.save(mapper.toEntity(trade)).let { mapper.toDomain(it) }
    }

    override fun findRecentTradesByCurrencyPair(
        currencyPair: String,
        limit: Int,
    ): List<Trade> = repository.findRecentTradeByCurrencyPair(currencyPair, limit).map { mapper.toDomain(it) }

    override fun findTradeByOrderId(orderId: String): Trade? {
        return repository.findTradeByOrderId(orderId)?.let { mapper.toDomain(it) }
    }
}
