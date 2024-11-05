package com.xchange.valr.trading.api.application.exception

import com.xchange.valr.trading.api.domain.orderbook.OrderBookNotFoundException
import com.xchange.valr.trading.api.model.ApiError
import jakarta.validation.ConstraintViolationException
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(OrderBookNotFoundException::class)
    fun handleOrderBookNotFoundException(ex: OrderBookNotFoundException): ResponseEntity<ApiError> {
        val error =
            ApiError.toApiError(
                code = "ORDER_BOOK_NOT_FOUND",
                status = NOT_FOUND,
                message = ex.message ?: "OrderBookNotFoundException exception occurred",
                details = null,
            )
        return ResponseEntity.status(NOT_FOUND).body(error)
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleValidationExceptions(ex: ConstraintViolationException): ResponseEntity<ApiError> {
        val details =
            ex.constraintViolations
                .groupBy(
                    keySelector = { it.propertyPath.toString() },
                    valueTransform = { it.message },
                )

        return ResponseEntity
            .status(BAD_REQUEST)
            .body(
                ApiError.toApiError(
                    code = "VALIDATION_ERROR",
                    status = BAD_REQUEST,
                    message = "Validation failed",
                    details = details,
                ),
            )
    }
}
