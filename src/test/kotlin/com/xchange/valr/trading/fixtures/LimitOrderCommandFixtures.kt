package com.xchange.valr.trading.fixtures

import com.xchange.valr.trading.api.domain.limitorder.LimitOrderCommand
import com.xchange.valr.trading.api.domain.limitorder.LimitOrderCommand.LimitOrder.OrderBookSide.BUY
import com.xchange.valr.trading.api.domain.limitorder.LimitOrderCommand.LimitOrder.OrderStatus.OPEN
import com.xchange.valr.trading.api.model.CurrencyPair.BTCZAR
import org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric
import java.math.BigDecimal

object LimitOrderCommandFixtures {
    fun createLimitOrder(): LimitOrderCommand =
        LimitOrderCommand(
            limitOrder =
                LimitOrderCommand.LimitOrder(
                    side = BUY,
                    quantity = BigDecimal.ONE,
                    price = BigDecimal("900000"),
                    currencyPair = BTCZAR.name,
                    status = OPEN,
                ),
            customerOrderId = randomAlphanumeric(50),
        )

    fun createLimitOrder(
        price: BigDecimal,
        quantity: BigDecimal,
        currencyPair: String,
        side: LimitOrderCommand.LimitOrder.OrderBookSide,
    ): LimitOrderCommand =
        LimitOrderCommand(
            limitOrder =
                LimitOrderCommand.LimitOrder(
                    side = side,
                    quantity = quantity,
                    price = price,
                    currencyPair = currencyPair,
                    status = OPEN,
                ),
            customerOrderId = randomAlphanumeric(50),
        )
}
