package com.xchange.valr.trading.api.application.limitorder

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.ninjasquad.springmockk.MockkBean
import com.xchange.valr.trading.api.domain.limitorder.LimitOrderCommandHandler
import com.xchange.valr.trading.api.infrastructure.config.GlobalConfig
import com.xchange.valr.trading.api.infrastructure.config.SecurityConfig
import com.xchange.valr.trading.api.model.LimitOrderResponseDto
import com.xchange.valr.trading.fixtures.LimitOrderRequestDtoFixtures.limitOrderRequestDto
import io.mockk.every
import org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.CoreMatchers.containsString
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@WebMvcTest(LimitOrderController::class)
@Import(LimitOrderDtoMapperImpl::class, GlobalConfig::class, SecurityConfig::class)
class LimitOrderControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var handler: LimitOrderCommandHandler

    private val mapper = LimitOrderDtoMapperImpl()

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private companion object {
        const val URI = "/api/v1/orders"
    }

    @Test
    @WithMockUser
    fun `should place limit order successfully`() {
        // given
        val request = limitOrderRequestDto()
        val expectedOrderId = randomAlphanumeric(50)
        val expected = LimitOrderResponseDto(orderId = expectedOrderId)
        val command = mapper.toCommand(request)

        every { handler.handle(command) } returns expectedOrderId

        // when
        val result =
            mockMvc.post(URI) {
                contentType = APPLICATION_JSON
                content = objectMapper.writeValueAsString(request)
            }
                .andExpect { status { isOk() } }
                .andReturn()
                .response

        // then
        val response = objectMapper.readValue<LimitOrderResponseDto>(result.contentAsString)

        assertThat(response).usingRecursiveComparison().isEqualTo(expected)
    }

    @Test
    @WithMockUser
    fun `should return bad request when currency pair is null`() {
        // given
        val request =
            """
            {
                "side": "BUY",
                "quantity": "1.0",
                "price": "50000.0",
                "pair": null,
                "postOnly": false,
                "timeInForce": "GTC"
            }
            """.trimIndent()

        // when/then
        mockMvc.post(URI) {
            contentType = APPLICATION_JSON
            content = request
        }
            .andExpect {
                status { isBadRequest() }
                jsonPath("$.message", containsString("pair is required"))
            }
    }

    @Test
    @WithMockUser
    fun `should return bad request when quantity is null`() {
        // given
        val request =
            """
            {
                "side": "BUY",
                "quantity": null,
                "price": "50000.0",
                "pair": "BTCZAR",
                "postOnly": false,
                "timeInForce": "GTC"
            }
            """.trimIndent()

        // when/then
        mockMvc.post(URI) {
            contentType = APPLICATION_JSON
            content = request
        }
            .andExpect {
                status { isBadRequest() }
                jsonPath("$.message", containsString("quantity is required"))
            }
    }

    @Test
    @WithMockUser
    fun `should return bad request when price is null`() {
        // given
        val request =
            """
            {
                "side": "BUY",
                "quantity": "1.0",
                "price": null,
                "pair": "BTCZAR",
                "postOnly": false,
                "timeInForce": "GTC"
            }
            """.trimIndent()

        // when/then
        mockMvc.post(URI) {
            contentType = APPLICATION_JSON
            content = request
        }
            .andExpect {
                status { isBadRequest() }
                jsonPath("$.message", containsString("price is required"))
            }
    }

    @Test
    @WithMockUser
    fun `should return bad request when side is null`() {
        // given
        val request =
            """
            {
                "side": null,
                "quantity": "1.0",
                "price": "50000.0",
                "pair": "BTCZAR",
                "postOnly": false,
                "timeInForce": "GTC"
            }
            """.trimIndent()

        // when/then
        mockMvc.post(URI) {
            contentType = APPLICATION_JSON
            content = request
        }
            .andExpect {
                status { isBadRequest() }
                jsonPath("$.message", containsString("side is required"))
            }
    }

    @Test
    @WithMockUser
    fun `should return bad request when side is invalid`() {
        // given
        val request =
            """
            {
                "side": "INVALID",
                "quantity": "1.0",
                "price": "50000.0",
                "pair": "BTCZAR",
                "postOnly": false,
                "timeInForce": "GTC"
            }
            """.trimIndent()

        // when/then
        mockMvc.post(URI) {
            contentType = APPLICATION_JSON
            content = request
        }
            .andExpect {
                status { isBadRequest() }
                jsonPath("$.message", containsString("side must be either 'BUY' or 'SELL'"))
            }
    }

    @Test
    @WithMockUser
    fun `should return bad request when price is less than zero`() {
        // given
        val request =
            """
            {
                "side": "BUY",
                "quantity": "1.0",
                "price": "-50000.0",
                "pair": "BTCZAR",
                "postOnly": false,
                "timeInForce": "GTC"
            }
            """.trimIndent()

        // when/then
        mockMvc.post(URI) {
            contentType = APPLICATION_JSON
            content = request
        }
            .andExpect {
                status { isBadRequest() }
                jsonPath("$.message", containsString("price must be greater than 0"))
            }
    }

    @Test
    @WithMockUser
    fun `should return bad request when customer id is invalid`() {
        // given
        val request =
            """
            {
                "side": "BUY",
                "quantity": "1.0",
                "price": "50000.0",
                "pair": "BTCZAR",
                "postOnly": false,
                "timeInForce": "GTC",
                "customerOrderId": "inv_alid"
            }
            """.trimIndent()

        // when/then
        mockMvc.post(URI) {
            contentType = APPLICATION_JSON
            content = request
        }
            .andExpect {
                status { isBadRequest() }
                jsonPath("$.message", containsString("Customer order ID must be alphanumeric"))
            }
    }

    @Test
    @WithMockUser
    fun `should return bad request when time in force is invalid`() {
        // given
        val request =
            """
            {
                "side": "BUY",
                "quantity": "1.0",
                "price": "50000.0",
                "pair": "BTCZAR",
                "postOnly": false,
                "timeInForce": "INVALID"
            }
            """.trimIndent()

        // when/then
        mockMvc.post(URI) {
            contentType = APPLICATION_JSON
            content = request
        }
            .andExpect {
                status { isBadRequest() }
                jsonPath("$.message", containsString("timeInForce must be either GTC, IOC or FOK"))
            }
    }
}
