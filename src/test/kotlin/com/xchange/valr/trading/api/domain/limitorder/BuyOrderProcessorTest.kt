package com.xchange.valr.trading.api.domain.limitorder

import com.xchange.valr.trading.api.domain.orderbook.OrderBook.OrderBookSide.BUY
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import java.math.BigDecimal

class BuyOrderProcessorTest : OrderProcessorBaseTest() {
    override val processor = BuyOrderProcessor()
    override val expectedSide = BUY

    @Test
    fun `should return asks as matching side`() {
        // given
        val orderBook = createOrderBook()

        // when/then
        assertThat(processor.getMatchingSide(orderBook))
            .isEqualTo(orderBook.asks)
    }

    @Test
    fun `should return bids as placement side`() {
        // given
        val orderBook = createOrderBook()

        // when/then
        assertThat(processor.getPlacementSide(orderBook))
            .isEqualTo(orderBook.bids)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "100, 100, true,  'matches when buy price equals ask price'",
            "110, 100, true,  'matches when buy price higher than ask price'",
            "90,  100, false, 'does not match when buy price lower than ask price'",
        ],
    )
    fun `should correctly determine matching`(
        buyPrice: BigDecimal,
        askPrice: BigDecimal,
        expectedResult: Boolean,
        testDescription: String,
    ) {
        assertThat(processor.canMatch(buyPrice, askPrice))
            .withFailMessage(testDescription)
            .isEqualTo(expectedResult)
    }
}
