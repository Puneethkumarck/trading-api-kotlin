package com.xchange.valr.trading.api.infrastructure.limitorder

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Repository
import java.util.concurrent.ConcurrentHashMap

private val log = KotlinLogging.logger {}

@Repository
class InMemoryOrderRepository {
    private val limitOrders = ConcurrentHashMap<String, LimitOrderEntity>()

    fun save(order: LimitOrderEntity): LimitOrderEntity {
        log.info { "Saving order: $order" }
        limitOrders[order.orderId] = order
        return order
    }

    fun findByOrderId(orderId: String): LimitOrderEntity? {
        log.debug { "Finding order by order id: $orderId" }
        return limitOrders[orderId]
    }
}
