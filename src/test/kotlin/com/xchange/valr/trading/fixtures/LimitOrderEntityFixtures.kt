package com.xchange.valr.trading.fixtures

import com.xchange.valr.trading.api.infrastructure.limitorder.LimitOrderEntity
import org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric
import java.math.BigDecimal
import java.time.Instant

object LimitOrderEntityFixtures {
    fun createLimitOrderEntity(
        currencyPair: String,
        side: LimitOrderEntity.OrderSide,
        orderId: String = "456",
        customerOrderId: String = randomAlphanumeric(50),
        quantity: BigDecimal = BigDecimal("1.0"),
        price: BigDecimal = BigDecimal("1000000"),
    ) = LimitOrderEntity(
        orderId = orderId,
        customerOrderId = customerOrderId,
        currencyPair = currencyPair,
        side = side,
        quantity = quantity,
        price = price,
        status = LimitOrderEntity.OrderStatus.OPEN,
        createdAt = Instant.now(),
    )
}
