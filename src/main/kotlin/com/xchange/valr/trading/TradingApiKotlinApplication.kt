package com.xchange.valr.trading

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class TradingApiKotlinApplication

fun main(args: Array<String>) {
    SpringApplication.run(TradingApiKotlinApplication::class.java, *args)
}
