package com.xchange.valr.trading.api.application.limitorder

import com.xchange.valr.trading.api.domain.limitorder.LimitOrderCommand
import com.xchange.valr.trading.api.model.CurrencyPair
import com.xchange.valr.trading.api.model.LimitOrderRequestDto
import org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class LimitOrderDtoMapperTest {
    private val mapper = LimitOrderDtoMapperImpl()

    @Test
    fun `should map buy order request to command`() {
        // given
        val customerOrderId = randomAlphanumeric(50)
        val request =
            LimitOrderRequestDto(
                pair = CurrencyPair.BTCZAR.value,
                side = "BUY",
                quantity = BigDecimal.ONE,
                price = BigDecimal("100000"),
                customerOrderId = customerOrderId,
            )

        // when
        val result = mapper.toCommand(request)

        // then
        val expectedLimitOrder =
            LimitOrderCommand.LimitOrder(
                side = LimitOrderCommand.LimitOrder.OrderBookSide.BUY,
                quantity = BigDecimal.ONE,
                price = BigDecimal("100000"),
                currencyPair = CurrencyPair.BTCZAR.value,
                status = null,
            )

        val expected =
            LimitOrderCommand(
                limitOrder = expectedLimitOrder,
                customerOrderId = customerOrderId,
            )

        assertThat(result)
            .usingRecursiveComparison()
            .ignoringFields("lastChange")
            .isEqualTo(expected)
    }

    @Test
    fun `should map sell order request to command`() {
        // given
        val customerOrderId = randomAlphanumeric(50)
        val request =
            LimitOrderRequestDto(
                pair = CurrencyPair.BTCZAR.value,
                side = "SELL",
                quantity = BigDecimal.ONE,
                price = BigDecimal("100000"),
                customerOrderId = customerOrderId,
            )

        // when
        val result = mapper.toCommand(request)

        // then
        val expectedLimitOrder =
            LimitOrderCommand.LimitOrder(
                side = LimitOrderCommand.LimitOrder.OrderBookSide.SELL,
                quantity = BigDecimal.ONE,
                price = BigDecimal("100000"),
                currencyPair = CurrencyPair.BTCZAR.value,
                status = null,
            )

        val expected =
            LimitOrderCommand(
                limitOrder = expectedLimitOrder,
                customerOrderId = customerOrderId,
            )

        assertThat(result)
            .usingRecursiveComparison()
            .ignoringFields("lastChange")
            .isEqualTo(expected)
    }
}
