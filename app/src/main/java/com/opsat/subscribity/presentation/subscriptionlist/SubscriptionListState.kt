package com.opsat.subscribity.presentation.subscriptionlist

data class SubscriptionListState(
    val isLoading: Boolean = true,
    val subscriptions: List<SubscriptionListItemUiModel> = emptyList(),
)

data class SubscriptionListItemUiModel(
    val id: Long,
    val name: String,
    val iconKey: String,
    val nextPaymentDateLabel: String,
    val priceLabel: String,
    val periodLabel: String,
)
