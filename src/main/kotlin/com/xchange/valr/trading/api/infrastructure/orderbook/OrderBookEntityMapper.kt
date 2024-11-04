package com.xchange.valr.trading.api.infrastructure.orderbook

import com.xchange.valr.trading.api.domain.orderbook.OrderBook
import org.mapstruct.Mapper

@Mapper
interface OrderBookEntityMapper {
    fun toEntity(orderBook: OrderBook): OrderBookEntity

    fun toDomain(entity: OrderBookEntity): OrderBook
}
