package com.opsat.subscribity.domain.model

import java.math.BigDecimal
import java.time.LocalDate

/**
 * @property id `0L` means the subscription has not been persisted yet.
 * @property icon a lookup key (e.g. "netflix", "custom") the presentation layer resolves to an
 *   actual icon resource; the domain layer has no knowledge of drawables.
 */
data class Subscription(
    val id: Long = 0L,
    val name: String,
    val icon: String,
    val period: BillingPeriod,
    val price: BigDecimal,
    val currency: CurrencyCode,
    val nextPaymentDate: LocalDate,
    val isTrial: Boolean = false,
    val trialPeriod: BillingPeriod? = null,
    val trialPrice: BigDecimal? = null,
    val isSharedWithOthers: Boolean = false,
    val personsCount: Int = 1,
) {
    init {
        require(name.isNotBlank()) { "name must not be blank" }
        require(price >= BigDecimal.ZERO) { "price must not be negative" }
        if (isTrial) {
            require(trialPeriod != null) { "trialPeriod is required when isTrial is true" }
            require(trialPrice != null && trialPrice >= BigDecimal.ZERO) {
                "trialPrice must be set and non-negative when isTrial is true"
            }
        } else {
            require(trialPeriod == null) { "trialPeriod must be null when isTrial is false" }
            require(trialPrice == null) { "trialPrice must be null when isTrial is false" }
        }
        require(personsCount >= 1) { "personsCount must be at least 1" }
        if (isSharedWithOthers) {
            require(personsCount >= 2) { "personsCount must be at least 2 when isSharedWithOthers is true" }
        } else {
            require(personsCount == 1) { "personsCount must be 1 when isSharedWithOthers is false" }
        }
    }
}
