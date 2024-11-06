package com.xchange.valr.trading.api.infrastructure.limitorder

import com.xchange.valr.trading.api.domain.limitorder.LimitOrderCommand
import com.xchange.valr.trading.api.domain.limitorder.LimitOrderRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component

private val logger = KotlinLogging.logger {}

@Component
class LimitOrderRepositoryAdaptor(
    private val repository: InMemoryOrderRepository,
    private val mapper: LimitOrderEntityMapper,
) : LimitOrderRepository {
    override fun save(order: LimitOrderCommand): LimitOrderCommand {
        logger.debug { "Saving order: $order" }
        return repository.save(mapper.toEntity(order))
            .let(mapper::toDomain)
    }

    override fun findByOrderId(orderId: String): LimitOrderCommand? {
        logger.debug { "Finding order by order id: $orderId" }
        return repository.findByOrderId(orderId)?.let(mapper::toDomain)
    }
}
