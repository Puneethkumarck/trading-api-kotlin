package com.xchange.valr.trading.api.application.tradehistory

import com.xchange.valr.trading.api.domain.tradehistory.Trade
import com.xchange.valr.trading.api.model.TradeHistoryResponseDto
import org.mapstruct.Mapper

@Mapper
interface TradeHistoryDtoMapper {
    fun toDto(trade: Trade): TradeHistoryResponseDto

    fun toDtoList(trades: List<Trade>?): List<TradeHistoryResponseDto> {
        return trades?.map { toDto(it) } ?: emptyList()
    }
}
