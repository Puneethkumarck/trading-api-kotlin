package com.xchange.valr.trading.api.domain.limitorder

import com.xchange.valr.trading.api.domain.orderbook.OrderBook
import com.xchange.valr.trading.api.model.CurrencyPair
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.concurrent.ConcurrentSkipListMap

abstract class OrderProcessorBaseTest {
    abstract val processor: OrderProcessor
    abstract val expectedSide: OrderBook.OrderBookSide

    @Test
    fun `should return correct order side`() {
        assertThat(processor.getOrderSide())
            .isEqualTo(expectedSide)
    }

    protected fun createOrderBook() =
        OrderBook(
            currencyPair = CurrencyPair.BTCZAR.value,
            asks = ConcurrentSkipListMap(),
            bids = ConcurrentSkipListMap(),
        )
}
