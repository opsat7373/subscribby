package com.opsat.subscribity.presentation.subscriptionlist

sealed interface SubscriptionListEffect {
    data class NavigateToEditSubscription(val id: Long) : SubscriptionListEffect
}
