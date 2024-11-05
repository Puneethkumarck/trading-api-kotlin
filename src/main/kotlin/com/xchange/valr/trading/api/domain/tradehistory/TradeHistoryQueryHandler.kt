package com.xchange.valr.trading.api.domain.tradehistory

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service

private val log = KotlinLogging.logger {}

@Service
class TradeHistoryQueryHandler(
    private val tradeHistoryRepository: TradeHistoryRepository,
) {
    fun getTradeHistory(
        currencyPair: String,
        limit: Int,
    ): List<Trade> {
        log.info { "Finding recent trades for currency pair: $currencyPair with limit: $limit" }
        return tradeHistoryRepository.findRecentTradesByCurrencyPair(currencyPair, limit)
    }
}
