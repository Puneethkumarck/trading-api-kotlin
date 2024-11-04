package com.xchange.valr.trading.api.model

import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import java.math.BigDecimal

data class LimitOrderRequestDto(
    @field:NotNull(message = "side is required")
    @field:Pattern(regexp = "^(BUY|SELL)$", message = "side must be either 'BUY' or 'SELL'")
    val side: String,
    @field:NotNull(message = "quantity is required")
    @field:DecimalMin(value = "0.0", inclusive = false, message = "quantity must be greater than 0")
    val quantity: BigDecimal,
    @field:NotNull(message = "price is required")
    @field:DecimalMin(value = "0.0", inclusive = false, message = "price must be greater than 0")
    val price: BigDecimal,
    @field:NotBlank(message = "currency pair is required")
    val pair: String,
    val postOnly: Boolean = false,
    @field:Size(max = 50, message = "Customer order ID must not exceed 50 characters")
    @field:Pattern(regexp = "^[a-zA-Z0-9-]*$", message = "Customer order ID must be alphanumeric")
    val customerOrderId: String? = null,
    @field:Pattern(regexp = "^(GTC|IOC|FOK)$", message = "timeInForce must be either GTC, IOC or FOK")
    val timeInForce: String = "GTC",
)
