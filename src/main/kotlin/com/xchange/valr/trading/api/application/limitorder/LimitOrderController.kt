package com.xchange.valr.trading.api.application.limitorder

import com.xchange.valr.trading.api.domain.limitorder.LimitOrderCommandHandler
import com.xchange.valr.trading.api.model.LimitOrderRequestDto
import com.xchange.valr.trading.api.model.LimitOrderResponseDto
import io.github.oshai.kotlinlogging.KotlinLogging
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

private val log = KotlinLogging.logger {}

@RestController
@RequestMapping("/api/v1/orders")
@Tag(
    name = "Limit Orders",
    description = "Operations related to limit orders",
)
class LimitOrderController(
    private val handler: LimitOrderCommandHandler,
    private val mapper: LimitOrderDtoMapper,
) {
    @PostMapping
    fun placeLimitOrder(
        @Valid @RequestBody request: LimitOrderRequestDto,
    ): ResponseEntity<LimitOrderResponseDto> {
        log.info { "Received request to place limit order for pair: ${request.pair}" }

        return ok(handler.handle(mapper.toCommand(request))?.let { LimitOrderResponseDto(orderId = it) })
    }
}
