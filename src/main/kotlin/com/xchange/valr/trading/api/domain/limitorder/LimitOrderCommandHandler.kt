package com.xchange.valr.trading.api.domain.limitorder

import com.xchange.valr.trading.api.domain.limitorder.LimitOrderCommand.LimitOrder.OrderBookSide.BUY
import com.xchange.valr.trading.api.domain.limitorder.LimitOrderCommand.LimitOrder.OrderBookSide.SELL
import com.xchange.valr.trading.api.domain.orderbook.OrderBook
import com.xchange.valr.trading.api.domain.orderbook.OrderBookRepository
import com.xchange.valr.trading.api.domain.tradehistory.Trade
import com.xchange.valr.trading.api.domain.tradehistory.TradeHistoryRepository
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID.randomUUID
import java.util.concurrent.ConcurrentSkipListMap

@Component
class LimitOrderCommandHandler(
    private val orderBookRepository: OrderBookRepository,
    private val tradeHistoryRepository: TradeHistoryRepository,
    private val idemPotencyValidator: OrderBookIdemPotencyValidator,
) {
    private val processors =
        mapOf(
            BUY to BuyOrderProcessor(),
            SELL to SellOrderProcessor(),
        )

    fun handle(command: LimitOrderCommand): String? =
        processOrder(idemPotencyValidator.validate(command))
            .customerOrderId

    private fun processOrder(command: LimitOrderCommand): LimitOrderCommand {
        val orderBook = getOrCreateOrderBook(command.currencyPair)
        val processor =
            processors[command.limitOrder.side]
                ?: throw IllegalStateException("No processor found for side: ${command.limitOrder.side}")

        processor.processOrder(command, orderBook) { cmd, price, quantity ->
            executeTrade(cmd, price, quantity)
        }

        orderBookRepository.save(orderBook)
        return command
    }

    private fun getOrCreateOrderBook(currencyPair: String): OrderBook =
        orderBookRepository.findByCurrencyPair(currencyPair) ?: OrderBook(
            currencyPair = currencyPair,
            asks = ConcurrentSkipListMap(),
            bids = ConcurrentSkipListMap(),
        )

    private fun executeTrade(
        command: LimitOrderCommand,
        price: BigDecimal,
        quantity: BigDecimal,
    ) {
        val trade =
            Trade(
                id = randomUUID().toString(),
                currencyPair = command.currencyPair,
                price = price,
                quantity = quantity,
                quoteVolume = price.multiply(quantity),
                takerSide = command.limitOrder.side.name,
                tradedAt = Instant.now(),
                sequenceId = null,
            )

        tradeHistoryRepository.save(trade)
    }
}
