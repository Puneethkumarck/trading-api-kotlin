package com.xchange.valr.trading.api.domain.tradehistory

interface TradeHistoryRepository {
    fun save(trade: Trade): Trade?

    fun findRecentTradesByCurrencyPair(
        currencyPair: String,
        limit: Int,
    ): List<Trade>

    fun findTradeByOrderId(orderId: String): Trade?
}
