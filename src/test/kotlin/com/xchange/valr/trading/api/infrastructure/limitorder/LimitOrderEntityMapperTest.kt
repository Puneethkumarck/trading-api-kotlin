package com.xchange.valr.trading.api.infrastructure.limitorder

import com.xchange.valr.trading.api.domain.limitorder.LimitOrderCommand
import com.xchange.valr.trading.api.domain.limitorder.LimitOrderCommand.LimitOrder.OrderBookSide.BUY
import com.xchange.valr.trading.api.domain.limitorder.LimitOrderCommand.LimitOrder.OrderBookSide.SELL
import com.xchange.valr.trading.api.domain.limitorder.LimitOrderCommand.LimitOrder.OrderStatus.OPEN
import com.xchange.valr.trading.api.model.CurrencyPair.BTCZAR
import com.xchange.valr.trading.fixtures.LimitOrderCommandFixtures.createLimitOrder
import com.xchange.valr.trading.fixtures.LimitOrderEntityFixtures.createLimitOrderEntity
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class LimitOrderEntityMapperTest {
    private val mapper = LimitOrderEntityMapper()

    @Test
    fun `should map domain to entity BUY`() {
        // given
        val command = createLimitOrder(BigDecimal("100000"), BigDecimal("1.0"), BTCZAR.name, BUY)

        // when
        val result = mapper.toEntity(command)

        // then
        val expected =
            LimitOrderEntity(
                orderId = command.customerOrderId ?: "orderid",
                customerOrderId = command.customerOrderId ?: "orderid",
                currencyPair = command.limitOrder.currencyPair,
                side = LimitOrderEntity.OrderSide.BUY,
                quantity = command.limitOrder.quantity,
                price = command.limitOrder.price,
                status = LimitOrderEntity.OrderStatus.OPEN,
                createdAt = result.createdAt,
            )

        assertThat(result)
            .usingRecursiveComparison()
            .isEqualTo(expected)
    }

    @Test
    fun `should map domain to entity SELL`() {
        // given
        val command =
            createLimitOrder(
                BigDecimal("100000"),
                BigDecimal("1.0"),
                BTCZAR.name,
                SELL,
            )

        // when
        val result = mapper.toEntity(command)

        // then
        val expected =
            LimitOrderEntity(
                orderId = command.customerOrderId ?: "orderid",
                customerOrderId = command.customerOrderId ?: "orderid",
                currencyPair = command.limitOrder.currencyPair,
                side = LimitOrderEntity.OrderSide.SELL,
                quantity = command.limitOrder.quantity,
                price = command.limitOrder.price,
                status = LimitOrderEntity.OrderStatus.OPEN,
                createdAt = result.createdAt,
            )

        assertThat(result)
            .usingRecursiveComparison()
            .isEqualTo(expected)
    }

    @Test
    fun `should map entity to domain BUY`() {
        // given
        val entity = createLimitOrderEntity(BTCZAR.name, LimitOrderEntity.OrderSide.BUY)

        // when
        val result = mapper.toDomain(entity)

        // then
        val expected =
            LimitOrderCommand(
                limitOrder =
                    LimitOrderCommand.LimitOrder(
                        side = BUY,
                        quantity = entity.quantity,
                        price = entity.price,
                        currencyPair = entity.currencyPair,
                        status = OPEN,
                    ),
                customerOrderId = entity.customerOrderId,
            )

        assertThat(result)
            .usingRecursiveComparison()
            .isEqualTo(expected)
    }

    @Test
    fun `should map entity to domain SELL`() {
        // given
        val entity = createLimitOrderEntity(BTCZAR.name, LimitOrderEntity.OrderSide.SELL)

        // when
        val result = mapper.toDomain(entity)

        // then
        val expected =
            LimitOrderCommand(
                limitOrder =
                    LimitOrderCommand.LimitOrder(
                        side = SELL,
                        quantity = entity.quantity,
                        price = entity.price,
                        currencyPair = entity.currencyPair,
                        status = OPEN,
                    ),
                customerOrderId = entity.customerOrderId,
            )

        assertThat(result)
            .usingRecursiveComparison()
            .isEqualTo(expected)
    }
}
