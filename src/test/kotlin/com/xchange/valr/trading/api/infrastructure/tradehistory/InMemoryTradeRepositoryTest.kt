package com.xchange.valr.trading.api.infrastructure.tradehistory

import com.xchange.valr.trading.api.model.CurrencyPair.BTCZAR
import com.xchange.valr.trading.fixtures.TradeEntityFixtures.createTradeEntity
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.Awaitility.await
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit
import java.util.function.Consumer

class InMemoryTradeRepositoryTest {
    private val repository = InMemoryTradeRepository()

    @Test
    fun `should save and retrieve trade`() {
        // given
        val trade = createTradeEntity(BTCZAR.name, "buy")

        // when
        val savedTrade = repository.save(trade)
        val retrievedTrade = repository.findRecentTradeByCurrencyPair(BTCZAR.name, 1)

        // then
        assertThat(retrievedTrade)
            .hasSize(1)
            .first()
            .usingRecursiveComparison()
            .isEqualTo(savedTrade)
    }

    @Test
    fun `should generate sequential sequence ids`() {
        // given
        val trade1 = createTradeEntity(BTCZAR.name, "buy")
        val trade2 = createTradeEntity(BTCZAR.name, "sell")

        // when
        repository.save(trade1)
        repository.save(trade2)

        // then
        val retrievedTrades = repository.findRecentTradeByCurrencyPair(BTCZAR.name, 2)
        assertThat(retrievedTrades)
            .hasSize(2)
            .satisfies(
                Consumer { tradeList ->
                    val firstSequenceId = tradeList[0].sequenceId
                    val secondSequenceId = tradeList[1].sequenceId
                    assertThat(firstSequenceId).isGreaterThan(secondSequenceId)
                },
            )
    }

    @Test
    fun `should return empty list when trade not found`() {
        // given
        val trade = createTradeEntity(BTCZAR.name, "buy")

        // when
        repository.save(trade)
        val retrievedTrade = repository.findRecentTradeByCurrencyPair("ETHZAR", 1)

        // then
        assertThat(retrievedTrade).isEmpty()
    }

    @Test
    fun `should return trade by order id`() {
        // given
        val tradeId = "trade-id"
        val trade = createTradeEntity(BTCZAR.name, "buy").copy(id = tradeId)

        // when
        val savedTrade = repository.save(trade)
        val retrievedTrade = repository.findTradeByOrderId(savedTrade.id)

        // then
        assertThat(retrievedTrade)
            .usingRecursiveComparison()
            .isEqualTo(savedTrade)
    }

    @Test
    fun `should return trades in descending order by traded at`() {
        // given
        val oldestTrade = createTradeEntity(BTCZAR.name, "buy")
        val middleTrade = createTradeEntity(BTCZAR.name, "sell")
        val newestTrade = createTradeEntity(BTCZAR.name, "buy")

        // when
        val savedOldest = repository.save(oldestTrade)
        await().atMost(1, TimeUnit.SECONDS)

        val savedMiddle = repository.save(middleTrade)
        await().atMost(1, TimeUnit.SECONDS)

        val savedNewest = repository.save(newestTrade)

        // then
        await()
            .atMost(2, TimeUnit.SECONDS)
            .untilAsserted {
                val retrievedTrades = repository.findRecentTradeByCurrencyPair(BTCZAR.name, 3)

                assertThat(retrievedTrades)
                    .hasSize(3)
                    .satisfies(
                        Consumer { tradeList ->
                            assertThat(tradeList[0].tradedAt)
                                .isAfterOrEqualTo(tradeList[1].tradedAt)
                            assertThat(tradeList[1].tradedAt)
                                .isAfterOrEqualTo(tradeList[2].tradedAt)

                            assertThat(tradeList[0].id)
                                .isEqualTo(savedNewest.id)
                            assertThat(tradeList[1].id)
                                .isEqualTo(savedMiddle.id)
                            assertThat(tradeList[2].id)
                                .isEqualTo(savedOldest.id)
                        },
                    )
            }
    }
}
