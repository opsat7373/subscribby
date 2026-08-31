package com.opsat.subscribity.domain.model

import java.math.BigDecimal
import java.time.LocalDate

/**
 * @property id `0L` means the subscription has not been persisted yet.
 * @property iconType how [iconValue]/[iconColor] should be interpreted: [SubscriptionIconType.LETTER]
 *   (the first letter of [name], on [iconColor]), [SubscriptionIconType.BRAND] (a Simple Icons slug in
 *   [iconValue]), or [SubscriptionIconType.PHOTO] (a file path relative to internal storage in [iconValue]).
 * @property iconColor ARGB color, persisted once so a [SubscriptionIconType.LETTER] icon's color stays
 *   stable across recompositions/reloads instead of re-randomizing.
 */
data class Subscription(
    val id: Long = 0L,
    val name: String,
    val period: BillingPeriod,
    val price: BigDecimal,
    val currency: CurrencyCode,
    val nextPaymentDate: LocalDate,
    val isTrial: Boolean = false,
    val trialPeriod: BillingPeriod? = null,
    val trialPrice: BigDecimal? = null,
    val isSharedWithOthers: Boolean = false,
    val personsCount: Int = 1,
    val notificationsEnabled: Boolean = true,
    val iconType: SubscriptionIconType = SubscriptionIconType.LETTER,
    val iconValue: String? = null,
    val iconColor: Int = AvatarColors.random(),
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
