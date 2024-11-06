package com.xchange.valr.trading.api.domain.limitorder

interface LimitOrderRepository {
    fun save(order: LimitOrderCommand): LimitOrderCommand

    fun findByOrderId(orderId: String): LimitOrderCommand?
}
