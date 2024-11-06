package com.xchange.valr.trading.fixtures

import com.xchange.valr.trading.api.model.CurrencyPair.BTCZAR
import com.xchange.valr.trading.api.model.LimitOrderRequestDto
import java.math.BigDecimal

object LimitOrderRequestDtoFixtures {
    fun limitOrderRequestDto() =
        LimitOrderRequestDto(
            pair = BTCZAR.value,
            side = "SELL",
            quantity = BigDecimal("1.0"),
            price = BigDecimal("1000000"),
            customerOrderId = "123",
        )
}
