package com.xchange.valr.trading.api.model

import org.springframework.http.HttpStatus

data class ApiError(
    val code: String,
    val status: String,
    val message: String,
    val details: Detail?,
) {
    data class Detail(
        val errors: Map<String, List<String>>,
    )

    companion object {
        fun toApiError(
            code: String,
            status: HttpStatus,
            message: String,
            details: Map<String, List<String>>?,
        ): ApiError =
            ApiError(
                code = code,
                status = status.reasonPhrase,
                message = message,
                details = details?.let { Detail(it) },
            )
    }
}
