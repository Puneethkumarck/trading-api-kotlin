package com.xchange.valr.trading.api.application.exception

import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.xchange.valr.trading.api.domain.orderbook.OrderBookNotFoundException
import com.xchange.valr.trading.api.model.ApiError
import jakarta.validation.ConstraintViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
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

    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException): ResponseEntity<ApiError> {
        val fieldErrors =
            ex.bindingResult.fieldErrors.associate { fieldError ->
                fieldError.field to listOf(fieldError.defaultMessage ?: "Validation error")
            }

        val error =
            ApiError.toApiError(
                code = "VALIDATION_ERROR",
                status = BAD_REQUEST,
                message = fieldErrors.values.flatten().joinToString(", "),
                details = fieldErrors,
            )

        return ResponseEntity.badRequest().body(error)
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadable(ex: HttpMessageNotReadableException): ResponseEntity<Map<String, String>> {
        val message =
            when (val cause = ex.cause) {
                is MismatchedInputException -> {
                    // Get the field name from the path
                    val fieldName = cause.path.firstOrNull()?.fieldName ?: "field"
                    "$fieldName is required"
                }
                else -> "Invalid request format"
            }

        return ResponseEntity
            .badRequest()
            .body(mapOf("message" to message))
    }
}
