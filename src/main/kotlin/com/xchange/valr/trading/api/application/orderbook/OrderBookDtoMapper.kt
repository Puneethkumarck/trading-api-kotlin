package com.xchange.valr.trading.api.application.orderbook

import com.xchange.valr.trading.api.domain.orderbook.OrderBook
import com.xchange.valr.trading.api.model.OrderBookResponseDto
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Named
import java.math.BigDecimal
import java.util.concurrent.ConcurrentNavigableMap

@Mapper
abstract class OrderBookDtoMapper {
    @Mapping(target = "asks", source = "asks", qualifiedByName = ["mapOrderBookLevels"])
    @Mapping(target = "bids", source = "bids", qualifiedByName = ["mapOrderBookLevels"])
    abstract fun toDto(orderBook: OrderBook): OrderBookResponseDto

    @Named("mapOrderBookLevels")
    fun mapOrderBookLevels(
        levels: ConcurrentNavigableMap<BigDecimal, OrderBook.OrderBookLevel>,
    ): List<OrderBookResponseDto.OrderBookEntryDto> =
        levels.values.map { level ->
            OrderBookResponseDto.OrderBookEntryDto(
                side = level.side.name,
                quantity = level.quantity,
                price = level.price,
                currencyPair = level.currencyPair,
                orderCount = level.orderCount,
            )
        }
}
