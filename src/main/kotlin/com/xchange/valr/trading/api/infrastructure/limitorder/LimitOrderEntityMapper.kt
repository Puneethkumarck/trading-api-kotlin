package com.xchange.valr.trading.api.infrastructure.limitorder

import com.xchange.valr.trading.api.domain.limitorder.LimitOrderCommand
import org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class LimitOrderEntityMapper {
    fun toEntity(command: LimitOrderCommand): LimitOrderEntity =
        LimitOrderEntity(
            orderId = command.customerOrderId ?: randomAlphanumeric(10),
            customerOrderId = command.customerOrderId ?: randomAlphanumeric(10),
            currencyPair = command.limitOrder.currencyPair,
            side = command.limitOrder.side.toEntityOrderSide(),
            quantity = command.limitOrder.quantity,
            price = command.limitOrder.price,
            status = LimitOrderEntity.OrderStatus.OPEN,
            createdAt = Instant.now(),
        )

    fun toDomain(entity: LimitOrderEntity): LimitOrderCommand =
        LimitOrderCommand(
            limitOrder = entity.toLimitOrder(),
            customerOrderId = entity.customerOrderId,
        )

    fun LimitOrderCommand.LimitOrder.OrderBookSide.toEntityOrderSide(): LimitOrderEntity.OrderSide =
        LimitOrderEntity.OrderSide.valueOf(this.name)

    fun LimitOrderEntity.OrderSide.toDomainOrderSide(): LimitOrderCommand.LimitOrder.OrderBookSide =
        LimitOrderCommand.LimitOrder.OrderBookSide.valueOf(this.name)

    fun LimitOrderEntity.OrderStatus.toDomainOrderStatus(): LimitOrderCommand.LimitOrder.OrderStatus =
        LimitOrderCommand.LimitOrder.OrderStatus.valueOf(this.name)

    private fun LimitOrderEntity.toLimitOrder(): LimitOrderCommand.LimitOrder =
        LimitOrderCommand.LimitOrder(
            side = side.toDomainOrderSide(),
            quantity = quantity,
            price = price,
            currencyPair = currencyPair,
            status = status.toDomainOrderStatus(),
        )
}
