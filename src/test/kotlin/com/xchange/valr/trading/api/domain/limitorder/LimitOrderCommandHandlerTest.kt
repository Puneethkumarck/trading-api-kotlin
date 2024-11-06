package com.xchange.valr.trading.api.domain.limitorder

import com.xchange.valr.trading.api.domain.limitorder.LimitOrderAlreadyExistsException.Companion.withOrderId
import com.xchange.valr.trading.api.domain.orderbook.OrderBook
import com.xchange.valr.trading.api.domain.orderbook.OrderBookRepository
import com.xchange.valr.trading.api.domain.tradehistory.Trade
import com.xchange.valr.trading.api.domain.tradehistory.TradeHistoryRepository
import com.xchange.valr.trading.api.model.CurrencyPair.BTCZAR
import com.xchange.valr.trading.fixtures.LimitOrderCommandFixtures.createLimitOrder
import com.xchange.valr.trading.fixtures.OrderBookFixtures.createOrderBook
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import java.math.BigDecimal

@ExtendWith(MockKExtension::class)
class LimitOrderCommandHandlerTest {
    @MockK
    private lateinit var orderBookRepository: OrderBookRepository

    @MockK
    private lateinit var tradeHistoryRepository: TradeHistoryRepository

    @MockK
    private lateinit var validator: OrderBookIdemPotencyValidator

    @InjectMockKs
    private lateinit var handler: LimitOrderCommandHandler

    private val orderBookCaptor = slot<OrderBook>()

    private val tradeCaptor = slot<Trade>()

    @Test
    fun `should reject duplicate order`() {
        // given
        val command = createLimitOrder()
        every { validator.validate(command) } throws withOrderId(command.customerOrderId)

        // when/then
        assertThrows<LimitOrderAlreadyExistsException> {
            handler.handle(command)
        }.also { exception ->
            assertThat(exception.message).contains(command.customerOrderId)
        }

        verify {
            validator.validate(command)
        }

        verify(exactly = 0) {
            orderBookRepository.findByCurrencyPair(any())
            tradeHistoryRepository.save(any())
        }
    }

    @Test
    fun `should create new order book with correct currency pair`() {
        // given
        val command = createLimitOrder()
        every { validator.validate(command) } returns command
        every { orderBookRepository.findByCurrencyPair(BTCZAR.name) } returns null
        every { orderBookRepository.save(capture(orderBookCaptor)) } answers {
            orderBookCaptor.captured
        }

        // when
        handler.handle(command)

        // then
        verify {
            orderBookRepository.save(any())
        }
        assertThat(orderBookCaptor.captured.currencyPair).isEqualTo(BTCZAR.value)
    }

    @Test
    fun `should process full match buy order`() {
        // given
        val sellPrice = BigDecimal("900000")
        val buyPrice = BigDecimal("900000")
        val quantity = BigDecimal.ONE

        val existingOrderBook =
            createOrderBook(
                price = sellPrice,
                quantity = quantity,
                currencyPair = BTCZAR.value,
                side = OrderBook.OrderBookSide.SELL,
            )

        val buyCommand =
            createLimitOrder(
                price = buyPrice,
                quantity = quantity,
                currencyPair = BTCZAR.value,
                side = LimitOrderCommand.LimitOrder.OrderBookSide.BUY,
            )

        setupMocks(buyCommand, existingOrderBook)

        // when
        handler.handle(buyCommand)

        // then
        verify {
            orderBookRepository.save(capture(orderBookCaptor))
        }
        assertThat(orderBookCaptor.captured.asks).isEmpty()
    }

    @Test
    fun `should process partial match buy order`() {
        // given
        val sellPrice = BigDecimal("900000")
        val buyPrice = BigDecimal("900000")
        val sellQuantity = BigDecimal.ONE
        val buyQuantity = BigDecimal("2")
        val expectedRemainingQuantity = buyQuantity.subtract(sellQuantity)

        val existingOrderBook =
            createOrderBook(
                price = sellPrice,
                quantity = sellQuantity,
                currencyPair = BTCZAR.value,
                side = OrderBook.OrderBookSide.SELL,
            )

        val buyCommand =
            createLimitOrder(
                price = buyPrice,
                quantity = buyQuantity,
                currencyPair = BTCZAR.value,
                side = LimitOrderCommand.LimitOrder.OrderBookSide.BUY,
            )

        setupMocks(buyCommand, existingOrderBook)

        // when
        handler.handle(buyCommand)

        // then
        verify { orderBookRepository.save(capture(orderBookCaptor)) }
        assertThat(orderBookCaptor.captured.bids[buyPrice]?.quantity)
            .isEqualByComparingTo(expectedRemainingQuantity)
    }

    @Test
    fun `should not match buy order when price too low`() {
        // given
        val sellPrice = BigDecimal("900000")
        val buyPrice = BigDecimal("800000")
        val quantity = BigDecimal.ONE

        val existingOrderBook =
            createOrderBook(
                price = sellPrice,
                quantity = quantity,
                currencyPair = BTCZAR.value,
                side = OrderBook.OrderBookSide.SELL,
            )

        val buyCommand =
            createLimitOrder(
                price = buyPrice,
                quantity = quantity,
                currencyPair = BTCZAR.value,
                side = LimitOrderCommand.LimitOrder.OrderBookSide.BUY,
            )

        every { validator.validate(buyCommand) } returns buyCommand
        every { orderBookRepository.findByCurrencyPair(BTCZAR.value) } returns existingOrderBook
        every { orderBookRepository.save(any()) } answers { firstArg() }

        // when
        handler.handle(buyCommand)

        // then
        verify(exactly = 0) {
            tradeHistoryRepository.save(any())
        }
    }

    @Test
    fun `should process full match sell order`() {
        // given
        val bidPrice = BigDecimal("900000")
        val sellPrice = BigDecimal("900000")
        val quantity = BigDecimal.ONE

        val existingOrderBook =
            createOrderBook(
                price = bidPrice,
                quantity = quantity,
                currencyPair = BTCZAR.value,
                side = OrderBook.OrderBookSide.BUY,
            )

        val sellCommand =
            createLimitOrder(
                price = sellPrice,
                quantity = quantity,
                currencyPair = BTCZAR.value,
                side = LimitOrderCommand.LimitOrder.OrderBookSide.SELL,
            )

        setupMocks(sellCommand, existingOrderBook)

        // when
        handler.handle(sellCommand)

        // then
        verify { orderBookRepository.save(capture(orderBookCaptor)) }
        assertThat(orderBookCaptor.captured.bids).isEmpty()
    }

    @Test
    fun `should process partial match sell order`() {
        // given
        val bidPrice = BigDecimal("900000")
        val sellPrice = BigDecimal("900000")
        val bidQuantity = BigDecimal.ONE
        val sellQuantity = BigDecimal("2")
        val expectedRemainingQuantity = sellQuantity.subtract(bidQuantity)

        val existingOrderBook =
            createOrderBook(
                price = bidPrice,
                quantity = bidQuantity,
                currencyPair = BTCZAR.value,
                side = OrderBook.OrderBookSide.BUY,
            )

        val sellCommand =
            createLimitOrder(
                price = sellPrice,
                quantity = sellQuantity,
                currencyPair = BTCZAR.value,
                side = LimitOrderCommand.LimitOrder.OrderBookSide.SELL,
            )

        setupMocks(sellCommand, existingOrderBook)

        // when
        handler.handle(sellCommand)

        // then
        verify { orderBookRepository.save(capture(orderBookCaptor)) }
        assertThat(orderBookCaptor.captured.asks[sellPrice]?.quantity)
            .isEqualByComparingTo(expectedRemainingQuantity)
    }

    @Test
    fun `should aggregate buy orders at same price`() {
        // given
        val price = BigDecimal("900000")
        val quantity1 = BigDecimal.ONE
        val quantity2 = BigDecimal("2")
        val expectedTotalQuantity = quantity1.add(quantity2)

        val existingOrderBook =
            createOrderBook(
                price = price,
                quantity = quantity1,
                currencyPair = BTCZAR.value,
                side = OrderBook.OrderBookSide.BUY,
            )

        val buyCommand =
            createLimitOrder(
                price = price,
                quantity = quantity2,
                currencyPair = BTCZAR.value,
                side = LimitOrderCommand.LimitOrder.OrderBookSide.BUY,
            )

        every { validator.validate(buyCommand) } returns buyCommand
        every { orderBookRepository.findByCurrencyPair(BTCZAR.value) } returns existingOrderBook
        every { orderBookRepository.save(any()) } answers { firstArg() }

        // when
        handler.handle(buyCommand)

        // then
        verify { orderBookRepository.save(capture(orderBookCaptor)) }
        val savedLevel = orderBookCaptor.captured.bids[price]
        assertThat(savedLevel?.quantity).isEqualByComparingTo(expectedTotalQuantity)
    }

    @Test
    fun `should aggregate sell orders at same price`() {
        // given
        val price = BigDecimal("900000")
        val quantity1 = BigDecimal.ONE
        val quantity2 = BigDecimal.ONE
        val expectedTotalQuantity = quantity1.add(quantity2)
        val expectedOrderCount = 2

        val existingOrderBook =
            createOrderBook(
                price = price,
                quantity = quantity1,
                currencyPair = BTCZAR.value,
                side = OrderBook.OrderBookSide.SELL,
            )

        val sellCommand =
            createLimitOrder(
                price = price,
                quantity = quantity1,
                currencyPair = BTCZAR.value,
                side = LimitOrderCommand.LimitOrder.OrderBookSide.SELL,
            )

        every { validator.validate(sellCommand) } returns sellCommand
        every { orderBookRepository.findByCurrencyPair(BTCZAR.value) } returns existingOrderBook
        every { orderBookRepository.save(any()) } answers { firstArg() }

        // when
        handler.handle(sellCommand)

        // then
        verify { orderBookRepository.save(capture(orderBookCaptor)) }
        val savedLevel = orderBookCaptor.captured.asks[price]
        assertThat(savedLevel?.quantity).isEqualByComparingTo(expectedTotalQuantity)
        assertThat(savedLevel?.orderCount).isEqualTo(expectedOrderCount)
    }

    private fun setupMocks(
        command: LimitOrderCommand,
        orderBook: OrderBook,
    ) {
        every { validator.validate(command) } returns command
        every { orderBookRepository.findByCurrencyPair(BTCZAR.value) } returns orderBook
        every { orderBookRepository.save(any()) } answers { firstArg() }
        every { tradeHistoryRepository.save(capture(tradeCaptor)) } answers { tradeCaptor.captured }
    }
}
