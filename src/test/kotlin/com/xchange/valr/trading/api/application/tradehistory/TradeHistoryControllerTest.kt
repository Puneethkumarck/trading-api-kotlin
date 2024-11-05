package com.xchange.valr.trading.api.application.tradehistory

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.ninjasquad.springmockk.MockkBean
import com.xchange.valr.trading.api.domain.tradehistory.Trade
import com.xchange.valr.trading.api.domain.tradehistory.TradeHistoryQueryHandler
import com.xchange.valr.trading.api.infrastructure.config.GlobalConfig
import com.xchange.valr.trading.api.model.CurrencyPair.BTCZAR
import com.xchange.valr.trading.api.model.TradeHistoryResponseDto
import com.xchange.valr.trading.fixtures.TradeFixtures.createTrade
import io.mockk.every
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(TradeHistoryController::class)
@Import(GlobalConfig::class, TradeHistoryDtoMapperImpl::class)
class TradeHistoryControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var handler: TradeHistoryQueryHandler

    private val mapper = TradeHistoryDtoMapperImpl()

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    companion object {
        private const val URI = "/api/v1/trades/{currencyPair}/history"
        private const val DEFAULT_PAGE_SIZE = 50
    }

    @Test
    @WithMockUser
    fun `should return trade history with default page size`() {
        // given
        val trades =
            listOf(
                createTrade(BTCZAR.name, Trade.TakerSide.SELL),
                createTrade(BTCZAR.name, Trade.TakerSide.BUY),
            )

        every {
            handler.getTradeHistory(BTCZAR.name, DEFAULT_PAGE_SIZE)
        } returns trades

        // when
        val result =
            mockMvc.perform(get(URI, BTCZAR.name))
                .andExpect(status().isOk)
                .andReturn()
                .response

        val response = objectMapper.readValue<List<TradeHistoryResponseDto>>(result.contentAsString)

        // then
        assertThat(response)
            .usingRecursiveComparison()
            .isEqualTo(trades.map { mapper.toDto(it) })
    }

    @Test
    @WithMockUser
    fun `should return trade history with custom page size`() {
        // given
        val trades =
            listOf(
                createTrade(BTCZAR.name, Trade.TakerSide.SELL),
                createTrade(BTCZAR.name, Trade.TakerSide.BUY),
            )
        val pageSize = 10

        every {
            handler.getTradeHistory(BTCZAR.name, pageSize)
        } returns trades

        // when
        val result =
            mockMvc.perform(get(URI, BTCZAR.name).param("pageSize", pageSize.toString()))
                .andExpect(status().isOk)
                .andReturn()
                .response

        val response = objectMapper.readValue<List<TradeHistoryResponseDto>>(result.contentAsString)

        // then
        assertThat(response)
            .usingRecursiveComparison()
            .isEqualTo(trades.map { mapper.toDto(it) })
    }

    @Test
    @WithMockUser
    fun `should return empty list when no trades found`() {

        every {
            handler.getTradeHistory(BTCZAR.name, DEFAULT_PAGE_SIZE)
        } returns emptyList()

        // when
        val result =
            mockMvc.perform(get(URI, BTCZAR.name))
                .andExpect(status().isOk)
                .andReturn()
                .response

        val response = objectMapper.readValue<List<TradeHistoryResponseDto>>(result.contentAsString)

        // then
        assertThat(response).isEmpty()
    }
}
