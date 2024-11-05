package com.xchange.valr.trading.api.application.exception

import com.xchange.valr.trading.api.domain.orderbook.OrderBookNotFoundException.Companion.withCurrencyPair
import com.xchange.valr.trading.api.model.ApiError
import io.mockk.every
import io.mockk.mockk
import jakarta.validation.ConstraintViolation
import jakarta.validation.ConstraintViolationException
import jakarta.validation.Path
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.NOT_FOUND

class GlobalExceptionHandlerTest {
    private val handler = GlobalExceptionHandler()

    @Test
    fun `should handle OrderBookNotFoundException`() {
        // given
        val currencyPair = "BTCZAR"
        val exception = withCurrencyPair(currencyPair)

        // when
        val response = handler.handleOrderBookNotFoundException(exception)

        // then
        val expected =
            ApiError.toApiError(
                code = "ORDER_BOOK_NOT_FOUND",
                status = NOT_FOUND,
                message = "Order book not found for currency pair: $currencyPair",
                details = null,
            )

        assertThat(response.body)
            .usingRecursiveComparison()
            .isEqualTo(expected)
    }

    @Test
    fun `should handle ConstraintViolationException`() {
        // given
        val violation1 = mockk<ConstraintViolation<Any>>()
        val violation2 = mockk<ConstraintViolation<Any>>()
        val path = mockk<Path>()

        every { path.toString() } returns "getOrderBook.currencyPair"
        every { violation1.propertyPath } returns path
        every { violation1.message } returns "must not be blank"
        every { violation2.propertyPath } returns path
        every { violation2.message } returns "must not be empty"

        val violations = setOf(violation1, violation2)
        val exception = ConstraintViolationException("Validation failed", violations)

        // when
        val response = handler.handleValidationExceptions(exception)

        val sortedResponse =
            response.body?.copy(
                details =
                    response.body?.details?.errors?.let {
                        response.body!!.details?.copy(
                            errors = it.mapValues { it.value.sorted() },
                        )
                    },
            )

        // then
        val expected =
            ApiError.toApiError(
                code = "VALIDATION_ERROR",
                status = BAD_REQUEST,
                message = "Validation failed",
                details =
                    mapOf(
                        "getOrderBook.currencyPair" to listOf("must not be blank", "must not be empty"),
                    ),
            )

        assertThat(sortedResponse)
            .usingRecursiveComparison()
            .isEqualTo(expected)
    }
}
