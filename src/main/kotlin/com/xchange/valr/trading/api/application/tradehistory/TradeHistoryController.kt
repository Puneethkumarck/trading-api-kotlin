package com.xchange.valr.trading.api.application.tradehistory

import com.xchange.valr.trading.api.domain.tradehistory.TradeHistoryQueryHandler
import com.xchange.valr.trading.api.model.TradeHistoryResponseDto
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/trades")
class TradeHistoryController(
    private val handler: TradeHistoryQueryHandler,
    private val mapper: TradeHistoryDtoMapper,
) {
    @GetMapping("/{currencyPair}/history")
    private fun getTradeHistory(
        @PathVariable
        @NotBlank
        currencyPair: String,
        @RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE_STR)
        @Min(value = 1, message = "Page size must be greater than 0")
        @Max(value = MAX_PAGE_SIZE.toLong(), message = "Page size must not exceed $MAX_PAGE_SIZE")
        pageSize: Int,
    ): ResponseEntity<List<TradeHistoryResponseDto>> {
        val trades = handler.getTradeHistory(currencyPair, pageSize)
        return ok(mapper.toDtoList(trades))
    }

    companion object {
        private const val DEFAULT_PAGE_SIZE = 50
        private const val MAX_PAGE_SIZE = 100
        private const val DEFAULT_PAGE_SIZE_STR = "$DEFAULT_PAGE_SIZE"
    }
}
