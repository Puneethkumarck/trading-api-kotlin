package com.xchange.valr.trading.api.application.orderbook

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.ninjasquad.springmockk.MockkBean
import com.xchange.valr.trading.api.domain.orderbook.OrderBookNotFoundException
import com.xchange.valr.trading.api.domain.orderbook.OrderBookQueryHandler
import com.xchange.valr.trading.api.infrastructure.config.GlobalConfig
import com.xchange.valr.trading.api.infrastructure.config.SecurityConfig
import com.xchange.valr.trading.api.model.ApiError
import com.xchange.valr.trading.api.model.CurrencyPair.BTCZAR
import com.xchange.valr.trading.api.model.OrderBookResponseDto
import com.xchange.valr.trading.fixtures.OrderBookFixtures.orderBook
import io.mockk.every
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(OrderBookController::class)
@Import(OrderBookDtoMapperImpl::class, GlobalConfig::class, SecurityConfig::class)
class OrderBookControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockkBean
    private lateinit var orderBookQueryHandler: OrderBookQueryHandler

    private val mapper = OrderBookDtoMapperImpl()

    companion object {
        private const val URI = "/api/v1/orders/{currencyPair}"
        private const val ERROR_CODE = "ORDER_BOOK_NOT_FOUND"
    }

    @Test
    @WithMockUser
    fun `should return order book for valid currency pair`() {
        // given
        every { orderBookQueryHandler.getOrderBook(BTCZAR.name) } returns orderBook(BTCZAR.name)

        // when
        val result =
            mockMvc.perform(get(URI, BTCZAR.name))
                .andExpect(status().isOk)
                .andReturn()

        // then
        val response =
            objectMapper.readValue(
                result.response.contentAsString,
                OrderBookResponseDto::class.java,
            )

        val expected = mapper.toDto(orderBook(BTCZAR.name))

        assertThat(response)
            .usingRecursiveComparison()
            .ignoringFields("lastChange")
            .isEqualTo(expected)
    }

    @Test
    @WithMockUser
    fun `should return order book for currency insensitive pair`() {
        // given
        val currencyPair = BTCZAR.name
        val inputCurrencyPair = "btczar"
        val orderBook = orderBook(currencyPair)

        every { orderBookQueryHandler.getOrderBook(inputCurrencyPair) } returns orderBook

        // when/then
        val result =
            mockMvc.perform(
                get("/api/v1/orders/$inputCurrencyPair"),
            )
                .andExpect(status().isOk)
                .andReturn().response

        val response = objectMapper.readValue<OrderBookResponseDto>(result.contentAsString)

        val expected = mapper.toDto(orderBook(BTCZAR.name))

        assertThat(response)
            .usingRecursiveComparison()
            .ignoringFields("lastChange")
            .isEqualTo(expected)
    }

    @Test
    @WithMockUser
    fun `should return 404 when order book not found`() {
        // given
        val currencyPair = "BTCBBB"
        every { orderBookQueryHandler.getOrderBook(currencyPair) } throws OrderBookNotFoundException.withCurrencyPair(currencyPair)

        // when/then
        val result =
            mockMvc.perform(get(URI, currencyPair))
                .andExpect(status().isNotFound)
                .andReturn().response

        val expected =
            ApiError.toApiError(
                code = ERROR_CODE,
                status = NOT_FOUND,
                message = "Order book not found for currency pair: $currencyPair",
                details = null,
            )

        val response = objectMapper.readValue<ApiError>(result.contentAsString)

        assertThat(response)
            .usingRecursiveComparison()
            .ignoringFields("lastChange")
            .isEqualTo(expected)
    }

    @ParameterizedTest
    @ValueSource(strings = [" ", "  "])
    @WithMockUser
    fun `should return bad request for empty currency pair`(currencyPair: String) {
        // when
        val result =
            mockMvc.perform(
                get(URI, currencyPair)
                    .contentType(MediaType.APPLICATION_JSON),
            )
                .andExpect(status().isBadRequest)
                .andReturn()

        // then
        val response = objectMapper.readValue<ApiError>(result.response.contentAsString)

        val expected =
            ApiError.toApiError(
                code = "VALIDATION_ERROR",
                status = HttpStatus.BAD_REQUEST,
                message = "Validation failed",
                details =
                    mapOf(
                        "getOrderBook.currencyPair" to listOf("must not be blank"),
                    ),
            )

        assertThat(response)
            .usingRecursiveComparison()
            .isEqualTo(expected)
    }
}
