package com.xchange.valr.trading.api.application.limitorder

import com.xchange.valr.trading.api.domain.limitorder.LimitOrderCommand
import com.xchange.valr.trading.api.model.LimitOrderRequestDto
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Named

@Mapper
interface LimitOrderDtoMapper {
    @Mapping(
        target = "limitOrder.side",
        source = "side",
        qualifiedByName = ["mapOrderSide"],
    )
    @Mapping(
        target = "limitOrder.quantity",
        source = "quantity",
    )
    @Mapping(
        target = "limitOrder.price",
        source = "price",
    )
    @Mapping(
        target = "limitOrder.currencyPair",
        source = "pair",
    )
    fun toCommand(request: LimitOrderRequestDto): LimitOrderCommand

    @Named("mapOrderSide")
    fun mapOrderSide(side: String): LimitOrderCommand.LimitOrder.OrderBookSide = LimitOrderCommand.LimitOrder.OrderBookSide.valueOf(side)
}
