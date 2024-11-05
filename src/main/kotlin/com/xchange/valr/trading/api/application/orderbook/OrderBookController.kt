package com.xchange.valr.trading.api.application.orderbook

import com.xchange.valr.trading.api.domain.orderbook.OrderBookQueryHandler
import com.xchange.valr.trading.api.model.OrderBookResponseDto
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/orders")
@Validated
@Tag(name = "Order Book", description = "Operations related to order book")
class OrderBookController(
    private val orderBookQueryHandler: OrderBookQueryHandler,
    private val mapper: OrderBookDtoMapper,
) {
    @GetMapping("/{currencyPair}")
    fun getOrderBook(
        @PathVariable @Valid @NotBlank currencyPair: String,
    ): OrderBookResponseDto {
        return mapper.toDto(orderBookQueryHandler.getOrderBook(currencyPair))
    }
}
