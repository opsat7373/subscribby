package com.opsat.subscribity.domain.model

@JvmInline
value class CurrencyCode(val code: String) {
    init {
        require(code.length == 3 && code.all { it.isUpperCase() }) {
            "code must be a 3-letter ISO 4217 code, was '$code'"
        }
    }
}
