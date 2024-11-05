package com.xchange.valr.trading.api.infrastructure.tradehistory

import com.xchange.valr.trading.api.domain.tradehistory.Trade
import org.mapstruct.Mapper

@Mapper
interface TradeHistoryEntityMapper {
    fun toEntity(trade: Trade): TradeEntity

    fun toDomain(tradeEntity: TradeEntity): Trade

    fun toDomainList(tradeEntities: List<TradeEntity>): List<Trade>
}
