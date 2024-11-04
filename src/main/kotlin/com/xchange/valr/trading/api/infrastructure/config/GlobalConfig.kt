package com.xchange.valr.trading.api.infrastructure.config

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered.HIGHEST_PRECEDENCE
import org.springframework.core.annotation.Order
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder

@Configuration
class GlobalConfig {
    @Bean
    @Order(HIGHEST_PRECEDENCE)
    fun objectMapperBuilderCustomizer(): Jackson2ObjectMapperBuilderCustomizer =
        Jackson2ObjectMapperBuilderCustomizer { builder: Jackson2ObjectMapperBuilder ->
            builder.apply {
                modules(JavaTimeModule())
                featuresToDisable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            }
        }
}
