package com.xchange.valr.trading.api.domain.limitorder

import com.xchange.valr.trading.api.domain.orderbook.OrderBook.OrderBookSide.SELL
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import java.math.BigDecimal

class SellOrderProcessorTest : OrderProcessorBaseTest() {
    override val processor = SellOrderProcessor()
    override val expectedSide = SELL

    @Test
    fun `should return bids as matching side`() {
        // given
        val orderBook = createOrderBook()

        // when/then
        assertThat(processor.getMatchingSide(orderBook))
            .isSameAs(orderBook.bids)
    }

    @Test
    fun `should return asks as placement side`() {
        // given
        val orderBook = createOrderBook()

        // when/then
        assertThat(processor.getPlacementSide(orderBook))
            .isSameAs(orderBook.asks)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "100, 100, true,  'matches when sell price equals bid price'",
            "90,  100, true,  'matches when sell price lower than bid price'",
            "110, 100, false, 'does not match when sell price higher than bid price'",
        ],
    )
    fun `should correctly determine matching`(
        sellPrice: BigDecimal,
        bidPrice: BigDecimal,
        expectedResult: Boolean,
        testDescription: String,
    ) {
        assertThat(processor.canMatch(sellPrice, bidPrice))
            .withFailMessage(testDescription)
            .isEqualTo(expectedResult)
    }
}
