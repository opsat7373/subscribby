package com.opsat.subscribity.presentation.subscriptionlist

sealed interface SubscriptionListIntent {
    data class SelectSubscription(val id: Long) : SubscriptionListIntent
}
