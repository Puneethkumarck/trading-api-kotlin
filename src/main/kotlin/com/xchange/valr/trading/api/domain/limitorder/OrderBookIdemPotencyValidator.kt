package com.xchange.valr.trading.api.domain.limitorder

import org.springframework.stereotype.Component

@Component
class OrderBookIdemPotencyValidator(
    private val orderRepository: LimitOrderRepository,
) {
    fun validate(command: LimitOrderCommand): LimitOrderCommand = saveOrder(validateOrderIdempotency(command))

    private fun validateOrderIdempotency(command: LimitOrderCommand): LimitOrderCommand {
        if (command.customerOrderId?.let { orderRepository.findByOrderId(it) } != null) {
            throw LimitOrderAlreadyExistsException.withOrderId(command.customerOrderId)
        }
        return command
    }

    private fun saveOrder(command: LimitOrderCommand): LimitOrderCommand = orderRepository.save(command)
}
