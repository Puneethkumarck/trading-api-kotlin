package com.xchange.valr.trading.api.infrastructure.config

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered.HIGHEST_PRECEDENCE
import org.springframework.core.annotation.Order

@Configuration
class GlobalConfig {
    @Bean
    @Order(HIGHEST_PRECEDENCE)
    fun objectMapperBuilderCustomizer() =
        Jackson2ObjectMapperBuilderCustomizer { builder ->
            builder.modules(
                JavaTimeModule(),
                KotlinModule.Builder().build(),
            ).featuresToDisable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        }
}
