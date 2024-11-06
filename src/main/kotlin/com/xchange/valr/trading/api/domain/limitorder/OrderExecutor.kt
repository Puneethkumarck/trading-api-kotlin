package com.xchange.valr.trading.api.domain.limitorder

import java.math.BigDecimal

fun interface OrderExecutor {
    fun executeTrade(
        command: LimitOrderCommand,
        matchPrice: BigDecimal,
        matchedQuantity: BigDecimal,
    )
}
