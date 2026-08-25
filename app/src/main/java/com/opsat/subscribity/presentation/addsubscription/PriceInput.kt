package com.opsat.subscribity.presentation.addsubscription

/** Keeps digits and at most one decimal point, truncated to 2 digits after it. */
fun sanitizePriceInput(raw: String): String {
    val firstDotIndex = raw.indexOf('.')
    val sb = StringBuilder()
    var seenDot = false
    var decimals = 0
    for ((index, char) in raw.withIndex()) {
        when {
            char.isDigit() && (!seenDot || decimals < 2) -> {
                sb.append(char)
                if (seenDot) decimals++
            }
            char == '.' && !seenDot && index == firstDotIndex -> {
                sb.append(char)
                seenDot = true
            }
        }
    }
    return sb.toString()
}
